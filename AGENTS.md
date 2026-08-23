# AGENTS.md

Kotlin Multiplatform Compose template — Android / Desktop / iOS / JS / WasmJs from one
shared codebase, wired together with Koin DI. This file is a routing file: quick
orientation + hard constraints + pointers to `docs/`. Read the linked doc before doing
the thing it covers; don't try to hold all of it in your head at once.

## Module map

```
app/androidApp          Android entry point (Activity), depends only on diApp
app/desktopApp          Desktop (JVM) entry point, depends only on diApp
app/webApp              Browser entry point (js + wasmJs), depends only on diApp
app/iosApp              Xcode project wrapping the framework diApp produces
diApp                   Koin DI graph — the ONLY module allowed to depend on *Impl modules
navigation              Navigation3 + adaptive-navigation-suite graph
ui/uiCommon             Shared composables, Compose resources
ui/uiMain               Main screen
ui/uiSplash             Splash screen
core/coreCommon         Dispatchers/platform-service abstractions
core/coreDatabaseApi    Database interface only, zero implementation deps
core/coreDatabaseRoom   Room 3 KMP implementation of coreDatabaseApi
core/coreNetworkApi     Network client interface only, zero implementation deps
core/coreNetworkKtor    Ktor implementation of coreNetworkApi
core/corePrefApi        Preferences interface only, zero implementation deps
core/corePrefDatastore  AndroidX DataStore implementation of corePrefApi
```

Full dependency graph and the `*Api`/`*Impl` rule: `docs/architecture.md` — **read
before adding or wiring a new module.**

## First commands to run

```
./gradlew ktlintFormat                    # auto-fix style before anything else
./gradlew ktlintCheck detekt lint         # static analysis gate
./gradlew jvmTest testAndroidHostTest     # fastest test targets for local iteration
./gradlew koverVerifyCoverage             # coverage gate (70% floor)
```

Full command list per gate/target: `docs/quality-gates.md`.

## Hard constraints

1. `core/*Api` modules define interfaces/models only — zero implementation dependencies
   (no Room, Ktor, DataStore).
2. Only `diApp` may depend on a `core/*Impl` module. `ui/*` and `navigation` depend on
   `*Api` modules directly, never on `*Impl`.
3. Apps (`androidApp`, `desktopApp`, `webApp`) depend only on `diApp` — never reach into
   `core/*` or `ui/*` directly.
4. Every new `core/*` or `ui/*` module applies `id("composeMultiplatformConvention")`
   and is registered in `settings.gradle.kts`.
5. A new `*Impl` module must also be added to the root `build.gradle.kts` `kover {
   dependencies { kover(projects...) } }` block so coverage stays aggregated.
6. Don't hand-write Koin `single<Api> { Impl() }` bindings — annotate the implementation
   class `@Single`; `diApp`'s `@ComponentScan` picks it up via KSP.
7. Don't apply `karma.config.d/js`'s skiko wiring to the `wasmJs` target — it's `js`-only
   (wasmJs has skiko statically linked, needs nothing).
8. Run `ktlintFormat` before `ktlintCheck` — CI does, and an unformatted diff fails the
   `Lint` job even if the code is otherwise correct.
9. Before adding a test, check `docs/testing.md` for which of the five test mechanisms
   (`commonTest`, `jvmTest`/Roborazzi, `androidHostTest`, `androidTest`,
   `jsBrowserTest`/`wasmJsBrowserTest`, `iosSimulatorArm64Test`) actually fits — most new
   tests belong in `commonTest`.
10. "Feature complete" means the app actually launched on an affected target
    (`./gradlew :app:desktopApp:run` is the cheapest check), not just "it compiles" —
    see Layer 3 in `docs/quality-gates.md`.
11. One module/feature actively worked at a time (WIP=1). A large ask ("add a new
    platform target", "add offline sync") gets broken into an ordered list before any
    code changes start.

## Docs

- `docs/architecture.md` — module dependency graph, the `*Api`/`*Impl` rule, how Koin
  wiring works, how to add a new module. Read before adding or wiring a new module.
- `docs/testing.md` — what each of the five test mechanisms is for and when to reach for
  it. Read before adding any test.
- `docs/quality-gates.md` — the exact commands CI runs per layer (static analysis, unit
  tests, coverage, app-actually-runs, screenshot verification) and where to look when
  one fails. Read before declaring a change done.
