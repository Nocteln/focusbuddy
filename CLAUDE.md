# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Product concept

Working name: **FocusBuddy** (final name not chosen yet). Target: a public release on the Play Store.
100 % local, no cloud, no account.

The core of the app is **blocking distracting apps** (Instagram, YouTube, TikTok…). A **buddy** that
lives with the user makes it motivating: its state reflects the user's habits.

- **One single, permanent buddy** with its own stats: a health score (0–100) that maps to a mood.
  It never dies: at worst it is neglected, and it recovers when the user gets back on track.
- **Skins are purely cosmetic.** The plant is the starting skin; others (puppy, cactus, cat…) are
  bought in the shop. Changing skin never changes the buddy's health.
- **Session (active blocking):** distracting apps are blocked by an overlay screen. **No pause.**
  The only way out is **Give up**, which hurts the buddy (`GIVE_UP_PENALTY`). Finishing a session
  heals it (`END_SESSION_HEALTH_BONUS`) and leads to a session summary screen that grants coins.
- **Outside a session: friction screen.** Opening a distracting app shows an overlay with a wait of
  **5 s (healthy buddy) up to 30 s (neglected buddy)**, then **Back** (small bonus) or **Continue**.
  **5-minute grace period:** no new friction screen for 5 min after Continue. Time spent on
  distracting apps slowly lowers the buddy's health.
- **Economy:** the currency is neutral ("coins", not "seeds"). Coins come from finished sessions,
  an **evening reward** (the buddy gives coins depending on its mood) and **weekly challenges**
  (e.g. "3 sessions of 45 min", "less than 1 h a day on TikTok"). The **shop** (skins) is the main
  motivation.
- **After V1:** a dedicated blocking menu (scheduled time slots), day streaks.
- **Before publishing:** choose the final name and fix the `applicationId` (currently
  `com.nocteln.app`; it can't change once published). Play Store permission declarations will be
  needed for usage access and drawing over other apps.

V1 screens: Home/Buddy, Session running, Session summary, Blocking overlay, Friction overlay,
Evening reward, Challenges, Shop, Stats, Settings, First launch (permissions).
The old "garden" and "harvest" screens are dropped.

## Roadmap

1. ✅ Home screen + circular slider + timer
2. ✅ HomeViewModel + StateFlow
3. ✅ Buddy system persisted with DataStore (health only; `skin` and coins not persisted yet)
4. ⏳ Distracting-app detection + friction screen (next):
   a. usage access permission (check + send the user to the system settings)
   b. detect the foreground app with UsageStatsManager (log only)
   c. list of distracting apps
   d. Foreground Service that monitors continuously
   e. friction overlay (overlay permission), wait based on mood, Back / Continue, grace period
   f. health slowly decreases with time spent on distracting apps
5. Sessions + blocking: the Foreground Service owns the session timer (end timestamp, not tick
   counting), blocking overlay during a session, session summary screen
6. Persistence: coins (DataStore), session history (Room)
7. Economy: coins on session end, evening reward, weekly challenges, shop, stats screen
8. Finishing touches: rename `PlantFocus` → `FocusBuddy` everywhere, UI strings in `strings.xml`
   (English default + French), first-launch screen, final name + `applicationId`, Play Store release

## Working with me

- I'm a beginner in Android development and learning by doing. Answer in French.
- **Code is written in English**: identifiers, comments and UI strings.
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
- Most dependencies are declared in `gradle/libs.versions.toml`. A few are hardcoded in `app/build.gradle.kts` (`lifecycle-viewmodel-compose`, `lifecycle-runtime-compose`, `material-icons-extended`, `datastore-preferences`). Put new dependencies in the version catalog.

## Architecture

Source root: `app/src/main/java/com/nocteln/app/`

Data flow: Screen → ViewModel → Repository → DataStore. Repositories are the single source of
truth; the future Foreground Service will use the same repositories (a service can't reach a
ViewModel).

- **Package by feature:** each screen has its own package under `ui/` (`ui/home/`, `ui/shop/`, `ui/settings/`, `ui/stats/`) that holds its screen, and where it has one, its UiState and ViewModel. Shared UI lives in `ui/components/`, navigation in `ui/navigation/`, theme in `ui/theme/`. A new screen gets a new `ui/<feature>/` package. Data (models, DataStore, repositories) lives under `data/<feature>/` (e.g. `data/buddy/`). The future Foreground Service goes in `service/`. Don't create empty packages in advance.
- **Entry point:** `MainApplication` (declared in the manifest via `android:name`) builds the app-wide repositories once in `onCreate` (a hand-written manual DI container, no Hilt). `MainActivity` enables edge-to-edge and hosts `PlantFocusTheme { PlantFocusApp() }`. `PlantFocusApp` (in `FocusBuddyApp.kt`; the file was renamed but the function and theme still carry the old `PlantFocus` name) owns the `NavHostController` and a plain `Scaffold` (no bottom bar).
- **Navigation** (`ui/navigation/NavGraph.kt`): routes are the `name` strings of the `NavScreens` enum (Shop, Home, Settings, EndSession, Stats; `icon` is nullable). Home is the start destination (scale + fade transitions set on the `NavHost`). There is no tab bar: `HomeScreen` has a top bar (`HomeTopBar`) whose buttons call `onOpenSettings` / `onOpenShop` / `onOpenStats`, which `navigate(...)` to the other screens. To add a screen, add an enum entry and a matching `composable(route = ...)` in `AppNavHost`. When `sessionEnded` turns true, a `LaunchedEffect` in `HomeScreen` calls `onSessionEnded`, which navigates to EndSession and resets the flag with `resetSession()`. Its buttons pop back to Home. EndSession still uses the old "harvest/seeds" wording; it becomes the session summary screen at roadmap step 5.
- **State pattern:** each screen has an immutable `XxxUiState` data class and an `XxxViewModel`, both in its feature package. The ViewModel exposes the state as `StateFlow` (private `MutableStateFlow` + `asStateFlow()`) and changes it with `_uiState.update { it.copy(...) }`. Values derived from other values are computed properties, never stored (e.g. `Buddy.mood`).
- **Stateful / stateless split:** a screen composable (e.g. `HomeScreen`) takes the ViewModel, collects its flows with `collectAsStateWithLifecycle()` and passes plain values and callbacks to a stateless `XxxContent` composable (e.g. `HomeContent`). Previews call the `Content` composable with sample values, never `viewModel()`.
- **ViewModel factory:** ViewModels that need a repository are built through `ui/AppViewModelProvider.Factory` (`viewModelFactory { initializer { ... } }`), which reads repositories from `MainApplication` via the `CreationExtras.mainApplication()` helper. Add an `initializer` there for each new ViewModel with dependencies, and create it with `viewModel(factory = AppViewModelProvider.Factory)`.
- **Shared session ViewModel:** `HomeViewModel` is created once in `AppNavHost` with `viewModel(factory = AppViewModelProvider.Factory)`, *outside* the `NavHost`, so it is scoped to the Activity. It is passed explicitly to `HomeScreen` and `EndSessionScreen`. Never rely on a default `= viewModel()` inside a `composable {}` for it: that creates a new, empty instance for each back-stack entry. This was a deliberate choice for simplicity: don't push back on it. It goes away at roadmap step 5, when the Foreground Service owns the session.
- **Focus timer:** the user picks a duration (1–60 min) on `ui/components/CircularSlider`, a custom `Canvas` ring that takes a `Float` fraction in 0..1, starts at 12 o'clock, ignores touches off the ring, and clamps at 0/1 to stop wrap-around. While a session runs, the same slider shows remaining time with input disabled. The buddy image sits inside the ring. `HomeViewModel` owns the countdown (`android.os.CountDownTimer`) and turns slider fractions into minutes (`onSliderChange`). There is no pause: the only actions are `startSession()` and `giveUpSession()`. The `TIMER_MAX_MINUTES` / `TIMER_STEP_MINUTES` / `GIVE_UP_PENALTY` / `END_SESSION_HEALTH_BONUS` constants live in `HomeViewModel.kt`. Roadmap step 5 moves the timer into a Foreground Service.
- **Buddy (`data/buddy/`):** `Buddy` (name, health, skin; `mood` is a computed property derived from health), `BuddyMood` enum (`THRIVING` 80, `HAPPY` 60, `NEUTRAL` 40, `SAD` 20, `NEGLECTED` 0, ordered best to worst; `getMoodFromHealth` in its companion object), `Context.buddyDataStore` (Preferences DataStore named `"buddy"`, declared once in `BuddyDataStore.kt`), and `BuddyRepository`, which receives the DataStore in its constructor and exposes `buddy: Flow<Buddy>` and `suspend changeHealth(delta)` (read + write in a single `edit`, clamped to 0..`MAX_BUDDY_HEALTH`). Keys are private in its companion object. `HomeViewModel` turns the flow into a `StateFlow` with `stateIn(viewModelScope, WhileSubscribed(5000), Buddy())` and calls `changeHealth` inside `viewModelScope.launch`. `skin` is not persisted yet.
- **Home screen:** `HomeContent` shows the top bar (settings, streak → stats, coins → shop), the buddy inside the circular slider (fading with health), then either the mood title + sentence + health bar (idle) or the countdown (running), and "Start a session" or "Give up". Coins and streak are placeholder parameters until their repositories exist.
- **Theme** (`ui/theme/`): `Color.kt` holds a generated Material 3 palette (light/dark, `xxxLight`/`xxxDark` naming, "Sauge" sage-green style). Dynamic color is off by default in `PlantFocusTheme`. Use `MaterialTheme.colorScheme` roles rather than raw colors.

## Conventions

- Kotlin official code style (`kotlin.code.style=official`).
- New code, comments and UI strings in English. Some older inline comments are still in French (e.g. in `CircularSlider.kt`).
