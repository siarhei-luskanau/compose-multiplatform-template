import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

val libs = the<LibrariesForLibs>()

plugins {
    id("com.android.kotlin.multiplatform.library")
    id("io.insert-koin.compiler.plugin")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlinx.kover")
    kotlin("multiplatform")
    kotlin("plugin.compose")
}

kover {
    currentProject {
        createVariant("coverage") {
            add("jvm", optional = true)
            add("android", optional = true)
        }
    }
}

kotlin {
    android {
        compilerOptions { jvmTarget = JvmTarget.fromTarget(libs.versions.javaVersion.get()) }
        compileSdk {
            version =
                release(
                    libs.versions.build.android.compileSdk
                        .get()
                        .toInt(),
                ) {
                    minorApiLevel =
                        libs.versions.build.android.compileSdkMinor
                            .get()
                            .toInt()
                }
        }
        minSdk =
            libs.versions.build.android.minSdk
                .get()
                .toInt()
        androidResources.enable = true
        withHostTestBuilder {}.configure {
            isIncludeAndroidResources = true
            enableCoverage = true
        }
        withDeviceTestBuilder {
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            animationsDisabled = true
            managedDevices.localDevices.create("managedVirtualDevice") {
                device = "Pixel 2"
                apiLevel = 35
            }
        }
        packaging.resources.excludes.add("META-INF/**")
    }

    jvm {
        compilerOptions { jvmTarget = JvmTarget.fromTarget(libs.versions.javaVersion.get()) }
    }

    js {
        browser()
        binaries.executable()
    }

    wasmJs {
        browser()
        binaries.executable()
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.compose.animation)
            implementation(libs.jetbrains.compose.animation.graphics)
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.ui.tooling.preview)
            implementation(libs.jetbrains.lifecycle.viewmodel.navigation3)
            implementation(libs.jetbrains.savedstate.compose)
            implementation(libs.jetbrains.window.core)
            implementation(libs.koin.annotations)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.coroutines.core)
            implementation(project.dependencies.platform(libs.koin.bom))
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.jetbrains.compose.ui.test)
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            implementation(libs.jetbrains.compose.ui.tooling)
        }

        getByName("androidHostTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.androidx.uitest.junit4)
                implementation(libs.androidx.uitest.testManifest)
                implementation(libs.junit)
                implementation(libs.robolectric)
            }
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

        iosMain.dependencies {
        }

        webMain.dependencies {
        }
    }

    targets
        .withType<KotlinNativeTarget>()
        .matching { it.konanTarget.family.isAppleFamily }
        .configureEach {
            binaries { framework { baseName = "ComposeApp" } }
        }
}

val sqliteBundledNatives: Configuration by configurations.creating {
    isTransitive = false
}

dependencies {
    sqliteBundledNatives("androidx.sqlite:sqlite-bundled-jvm:${libs.versions.androidx.sqlite.get()}")
}

val extractSqliteBundledNatives by tasks.registering(Sync::class) {
    from(provider { sqliteBundledNatives.map(::zipTree) })
    include("natives/**")
    into(layout.buildDirectory.dir("sqliteBundledNatives"))
}

val osName = System.getProperty("os.name").lowercase()
val osArch = System.getProperty("os.arch").lowercase()
val sqliteNativesPlatform =
    when {
        osName.contains("mac") -> "osx_arm64"
        osName.contains("win") -> "windows_x64"
        osArch.contains("aarch64") -> "linux_arm64"
        else -> "linux_x64"
    }
val sqliteNativesLibName =
    when {
        osName.contains("mac") -> "libsqliteJni.dylib"
        osName.contains("win") -> "sqliteJni.dll"
        else -> "libsqliteJni.so"
    }

tasks.withType<Test>().configureEach {
    if (listOf("AndroidHostTest", "IosSimulator").any { name.contains(other = it, ignoreCase = true) }) {
        dependsOn(extractSqliteBundledNatives)
        systemProperty(
            "androidx.sqlite.driver.bundled.path",
            extractSqliteBundledNatives
                .get()
                .destinationDir
                .resolve("natives/$sqliteNativesPlatform")
                .absolutePath,
        )
        systemProperty("androidx.sqlite.driver.bundled.name", sqliteNativesLibName)
    }

    if (name.contains(other = "AndroidHostTest", ignoreCase = true)) {
        exclude("**/*CommonTest*")
        systemProperties["robolectric.pixelCopyRenderMode"] = "hardware"
        // Robolectric reflectively pokes JDK internals (e.g. jdk.internal.access.SharedSecrets
        // for ApplicationSharedMemory on SDK 37+); modern JDKs (17+) hide those by default.
        jvmArgs(
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.base/java.util=ALL-UNNAMED",
            "--add-opens=java.base/java.io=ALL-UNNAMED",
            "--add-opens=java.base/java.net=ALL-UNNAMED",
            "--add-opens=java.base/java.security=ALL-UNNAMED",
            "--add-opens=java.base/java.text=ALL-UNNAMED",
            "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED",
            "--add-opens=java.base/jdk.internal.access=ALL-UNNAMED",
            "--add-opens=java.base/jdk.internal.util.random=ALL-UNNAMED",
            "--add-opens=java.desktop/java.awt.font=ALL-UNNAMED",
        )
    }
}

tasks.withType<AbstractTestTask>().configureEach {
    failOnNoDiscoveredTests = false
}
