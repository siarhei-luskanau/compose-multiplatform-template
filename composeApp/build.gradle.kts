plugins {
    id("androidTestConvention")
    id("composeMultiplatformConvention")
    alias(libs.plugins.roborazzi)
}

kotlin {
    androidLibrary.namespace = "template.app"
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.coreCommon)
            implementation(projects.core.corePref)
            implementation(projects.navigation)
            implementation(projects.ui.uiCommon)
            implementation(projects.ui.uiMain)
            implementation(projects.ui.uiSplash)
        }

        jvmMain.dependencies {
            implementation(libs.androidx.datastore.core.okio)
        }

        jvmTest.dependencies {
            implementation(libs.roborazzi.compose.desktop)
        }

        androidMain.dependencies {
            implementation(libs.androidx.datastore.core.okio)
        }

        androidHostTest.dependencies {
            implementation(libs.robolectric)
            implementation(libs.roborazzi)
            implementation(libs.roborazzi.compose)
        }

        iosMain.dependencies {
            implementation(libs.androidx.datastore.core.okio)
        }

        iosTest.dependencies {
            implementation(libs.roborazzi.compose.ios)
        }
    }
}

// Directory for reference images
roborazzi.outputDir.set(rootProject.file("screenshots"))
