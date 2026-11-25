# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

## Product concept

A plant grows during focus sessions and wilts if the user opens a distracting app
(Instagram, YouTube, TikTok…). Sessions earn seeds, spent in a shop on plant skins
and companions (e.g. a puppy). 100 % local, no cloud, no account.

Planned stack: Room + DataStore (persistence), Foreground Service (session survives
screen off), UsageStatsManager (detect foreground app), overlay permission (alert).
Session state is currently shared between Home and EndSession through one `HomeViewModel`
(see Architecture). This was a deliberate choice for simplicity: don't push back on it.
It goes away at roadmap step 3, when the Foreground Service owns the session. Persisted
data (seeds, history) goes through a repository.

## Roadmap

1. ✅ Home screen + circular slider + timer
2. ⏳ HomeViewModel + StateFlow (in progress)
3. Foreground Service owning the session timer (end timestamp, not tick counting)
4. Distracting-app detection
5. Persistence (Room, DataStore), then shop, garden, stats screens

## Working with me

- I'm a beginner in Android development and learning by doing. Answer in French.
- Move in small, testable steps. Short explanations, no long walls of text.
- When I say I want to do something myself, give hints and explain concepts,
  not the full code. Review my code when I paste it.
- Prefer writing small things myself over adding a library.

## Commands

Use the Gradle wrapper. The Gradle daemon runs on a JDK 25 toolchain, which foojay downloads automatically (see `gradle/gradle-daemon-jvm.properties`). The configuration cache is enabled.

```bash
./gradlew assembleDebug                 # build debug APK
./gradlew installDebug                  # install on connected device/emulator
./gradlew lint                          # Android lint
./gradlew testDebugUnitTest             # JVM unit tests (app/src/test)
./gradlew testDebugUnitTest --tests "com.nocteln.app.ExampleUnitTest"          # single test class
./gradlew testDebugUnitTest --tests "com.nocteln.app.ExampleUnitTest.addition_isCorrect"  # single method
./gradlew connectedDebugAndroidTest     # instrumented tests (app/src/androidTest), needs device
./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.nocteln.app.ExampleInstrumentedTest
```

## Build setup (AGP 9)

- AGP 9.x has built-in Kotlin support. Only the `org.jetbrains.kotlin.plugin.compose` plugin is applied. Do **not** add `org.jetbrains.kotlin.android`.
- The DSL is AGP 9 style: `compileSdk { version = release(37) }` and `release { optimization { enable = true } }` take the place of `isMinifyEnabled`. R8 keep rules live in `app/src/main/keepRules/*.keep`, not `proguard-rules.pro`.
- Most dependencies are declared in `gradle/libs.versions.toml`. A few are hardcoded in `app/build.gradle.kts` (`lifecycle-viewmodel-compose`, `lifecycle-runtime-compose`, `material-icons-extended`). Put new dependencies in the version catalog.

## Architecture

Source root: `app/src/main/java/com/nocteln/app/`

- **Package by feature:** each screen has its own package under `ui/` (`ui/home/`, `ui/shop/`, `ui/settings/`) that holds its screen, and where it has one, its UiState and ViewModel. Shared UI lives in `ui/components/`, navigation in `ui/navigation/`, theme in `ui/theme/`. A new screen gets a new `ui/<feature>/` package.
- **Entry point:** `MainActivity` enables edge-to-edge and hosts `PlantFocusTheme { PlantFocusApp() }`. `PlantFocusApp` owns the `NavHostController` and a `Scaffold` whose bottom bar is a Material 3 `ShortNavigationBar`.
- **Navigation** (`ui/navigation/NavGraph.kt`): routes are the `name` strings of the `NavScreens` enum (Shop, Home, Settings). The bottom bar iterates `NavScreens.entries`, so enum order sets tab order. Home is the start destination. To add a tab, add an enum entry (label + icon) and a matching `composable(route = ...)` in `AppNavHost`. Tab clicks use the standard save/restore-state pattern (`popUpTo(startDestination) { saveState = true }`, `launchSingleTop`, `restoreState`). `END_SESSION_ROUTE` is a non-tab destination. When `sessionEnded` turns true, a `LaunchedEffect` in `HomeScreen` calls `onSessionEnded`, which navigates there and resets the flag with `resetSession()`. Its buttons pop back to Home.
- **State pattern:** each screen has an immutable `XxxUiState` data class and an `XxxViewModel`, both in its feature package. The ViewModel exposes the state as `StateFlow` (private `MutableStateFlow` + `asStateFlow()`) and changes it with `_uiState.update { it.copy(...) }`. The screen takes the ViewModel as a parameter (`viewModel: XxxViewModel`, from `lifecycle-viewmodel-compose`), collects the state, and sends user actions back through ViewModel functions.
- **Shared session ViewModel:** `HomeViewModel` is created once in `AppNavHost` with `viewModel()`, *outside* the `NavHost`, so it is scoped to the Activity. It is passed explicitly to `HomeScreen` and `EndSessionScreen`. Never rely on a default `= viewModel()` inside a `composable {}` for it: that creates a new, empty instance for each back-stack entry.
- **Focus timer:** the user picks a duration (1–60 min) on `ui/components/CircularSlider`, a custom `Canvas` ring that takes a `Float` fraction in 0..1, starts at 12 o'clock, ignores touches off the ring, and clamps at 0/1 to stop wrap-around. While a session runs, the same slider shows remaining time with input disabled. `HomeViewModel` owns the countdown (`android.os.CountDownTimer`) and turns slider fractions into minutes (`onSliderChange`). The `TIMER_MAX_MINUTES` / `TIMER_STEP_MINUTES` constants live in `HomeViewModel.kt`. Roadmap step 3 moves the timer into a Foreground Service.
- **Theme** (`ui/theme/`): `Color.kt` holds a generated Material 3 palette (light/dark, `xxxLight`/`xxxDark` naming). Dynamic color is off by default in `PlantFocusTheme`. Use `MaterialTheme.colorScheme` roles rather than raw colors.

## Conventions

- Kotlin official code style (`kotlin.code.style=official`).
- Some inline comments are in French (e.g. in `CircularSlider.kt`).
