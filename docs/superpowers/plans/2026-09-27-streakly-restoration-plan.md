# Streakly Complete Restoration & GitHub Actions CI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reconstruct the complete Streakly Android application (all 74 Kotlin files across data, domain, UI, background workers, and utils, plus resources, build scripts, and GitHub Actions CI workflow) to produce a fully functioning, compilable project that outputs an Android APK directly on GitHub Actions.

**Architecture:** Clean Architecture with MVI/MVVM: domain models and repository interfaces at the core, offline-first Room database (9 tables) and Health Connect / DataStore / NVIDIA NIM client in data layer, Dagger Hilt dependency injection, WorkManager / AlarmManager background workers, and declarative Jetpack Compose UI with custom typography and themes.

**Tech Stack:** Kotlin 1.9.23, Jetpack Compose (Material 3), Dagger Hilt 2.50, Room 2.6.1, Health Connect 1.1.0-alpha11, Retrofit 2.11.0 / Moshi 1.15.1, AndroidX WorkManager 2.9.0, Gradle 8.7, GitHub Actions CI.

## Global Constraints

- Android `minSdk` = 28, `targetSdk` = 35, `compileSdk` = 35, JVM Target = 17.
- Application ID = `com.streakly`, Version Code = 1, Version Name = "1.0.0".
- Database version = 2 with 9 verified tables: `water_logs`, `journal_entries`, `habits`, `habit_logs`, `streak_cache`, `cached_health_data`, `agent_tasks`, `goals`, `checklist_items`.
- All navigation routes must match: `today`, `fitness`, `sleep`, `health`, `journal`, `habits`, `chat`, `settings`.
- GitHub Actions CI workflow must build `./gradlew assembleDebug` and upload `app/build/outputs/apk/debug/app-debug.apk` as an artifact.
- End all git commit messages with:
  `Co-Authored-By: Claude Code <noreply@anthropic.com>`

---

### Task 1: Gradle Build Infrastructure & Wrapper

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `gradle.properties`
- Create: `gradle/wrapper/gradle-wrapper.properties`
- Create: `gradlew`
- Create: `gradlew.bat`
- Create: `app/build.gradle.kts`
- Create: `app/proguard-rules.pro`

**Interfaces:**
- Produces: Gradle build system configuration compatible with Gradle 8.7, AGP 8.5.1, Kotlin 1.9.23, KSP 1.9.23, Compose Compiler 1.5.11, and Hilt 2.50.

- [ ] **Step 1: Create `settings.gradle.kts`**
- [ ] **Step 2: Create root `build.gradle.kts` and `gradle.properties`**
- [ ] **Step 3: Create `gradle/wrapper/gradle-wrapper.properties` and wrapper scripts**
- [ ] **Step 4: Create `app/build.gradle.kts` with all dependencies (Compose, Hilt, Room, Health Connect, Retrofit, WorkManager)**
- [ ] **Step 5: Create `app/proguard-rules.pro`**
- [ ] **Step 6: Commit build infrastructure**

---

### Task 2: Android Manifest & Resources

**Files:**
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/res/values/strings.xml`
- Create: `app/src/main/res/values/colors.xml`
- Create: `app/src/main/res/values/themes.xml`
- Create: `app/src/main/res/drawable/ic_streakly_logo.xml`

**Interfaces:**
- Consumes: Resources extracted from `merger.xml` and `AndroidManifest.xml`
- Produces: Complete resource set including `@string/app_name`, `@color/purple_500`, `@style/Theme.Streakly`, and `@drawable/ic_streakly_logo`

- [ ] **Step 1: Write `app/src/main/AndroidManifest.xml` with permissions (Health Connect, Notifications, Alarms, Boot) and component declarations**
- [ ] **Step 2: Write `app/src/main/res/values/strings.xml` containing all app strings and plurals**
- [ ] **Step 3: Write `app/src/main/res/values/colors.xml` and `themes.xml`**
- [ ] **Step 4: Write `app/src/main/res/drawable/ic_streakly_logo.xml` vector drawable**
- [ ] **Step 5: Commit manifest and resources**

---

### Task 3: Utilities & Extensions

**Files:**
- Create: `app/src/main/java/com/streakly/util/DateUtils.kt`
- Create: `app/src/main/java/com/streakly/util/Extensions.kt`
- Create: `app/src/main/java/com/streakly/util/NotificationHelper.kt`
- Create: `app/src/main/java/com/streakly/util/NotificationScheduler.kt`
- Test: `app/src/test/java/com/streakly/util/DateUtilsTest.kt`

**Interfaces:**
- Produces:
  - `DateUtils`: `getTodayIso()`, `getDisplayDate(date)`, `getGreeting(name)`, `formatDuration(ms)`, `formatSteps(count)`
  - `Extensions`: `safeFlow`, `catchAndReturn`, `format1`
  - `NotificationHelper`: `createChannels(context)`, `show(context, id, title, body, channel)`
  - `NotificationScheduler`: `scheduleGoalAlarm`, `scheduleChecklistAlarm`, `scheduleNightReminder`, `getTriggerTime`

- [ ] **Step 1: Implement `DateUtils.kt` with date formatting, greetings, and day boundaries**
- [ ] **Step 2: Implement `Extensions.kt` with Coroutine Flow helpers and formatting functions**
- [ ] **Step 3: Implement `NotificationHelper.kt` with notification channels and builder**
- [ ] **Step 4: Implement `NotificationScheduler.kt` with AlarmManager integration**
- [ ] **Step 5: Write unit tests in `DateUtilsTest.kt` verifying date utilities**
- [ ] **Step 6: Commit utilities**

---

### Task 4: Domain Models & Repository Interfaces

**Files:**
- Create: `app/src/main/java/com/streakly/domain/model/UserProfile.kt`
- Create: `app/src/main/java/com/streakly/domain/model/GoalModels.kt`
- Create: `app/src/main/java/com/streakly/domain/model/HabitModels.kt`
- Create: `app/src/main/java/com/streakly/domain/model/HealthModels.kt`
- Create: `app/src/main/java/com/streakly/domain/model/JournalModels.kt`
- Create: `app/src/main/java/com/streakly/domain/model/NutritionModels.kt`
- Create: `app/src/main/java/com/streakly/domain/model/StreakModels.kt`
- Create: `app/src/main/java/com/streakly/domain/repository/GoalRepository.kt`
- Create: `app/src/main/java/com/streakly/domain/repository/HabitRepository.kt`
- Create: `app/src/main/java/com/streakly/domain/repository/HealthRepository.kt`
- Create: `app/src/main/java/com/streakly/domain/repository/JournalRepository.kt`
- Create: `app/src/main/java/com/streakly/domain/repository/SettingsRepository.kt`
- Create: `app/src/main/java/com/streakly/domain/repository/WaterRepository.kt`

**Interfaces:**
- Produces: Complete domain contract for all features

- [ ] **Step 1: Implement `UserProfile.kt` (`UserProfile`, `UserGoals`, `NotificationSettings`, `AppearanceSettings`, `ThemeMode`)**
- [ ] **Step 2: Implement `GoalModels.kt` (`Goal`, `ChecklistItem`) and `HabitModels.kt` (`Habit`, `HabitLog`, `HabitWithCompletion`, `HabitDayStatus`, `TimeOfDay`)**
- [ ] **Step 3: Implement `HealthModels.kt` (`StepsByDay`, `HourlySteps`, `HeartRateSample`, `SleepSessionData`, `ReadinessScore`, etc.)**
- [ ] **Step 4: Implement `JournalModels.kt`, `NutritionModels.kt`, `StreakModels.kt`**
- [ ] **Step 5: Implement all 6 domain repository interfaces**
- [ ] **Step 6: Commit domain layer**

---

### Task 5: Room Database Entities, DAOs & Database Class

**Files:**
- Create: `app/src/main/java/com/streakly/data/local/entity/WaterLogEntity.kt`
- Create: `app/src/main/java/com/streakly/data/local/entity/JournalEntryEntity.kt`
- Create: `app/src/main/java/com/streakly/data/local/entity/HabitEntity.kt`
- Create: `app/src/main/java/com/streakly/data/local/entity/HabitLogEntity.kt`
- Create: `app/src/main/java/com/streakly/data/local/entity/StreakCacheEntity.kt`
- Create: `app/src/main/java/com/streakly/data/local/entity/CachedHealthDataEntity.kt`
- Create: `app/src/main/java/com/streakly/data/local/entity/AgentTaskEntity.kt`
- Create: `app/src/main/java/com/streakly/data/local/entity/GoalEntity.kt`
- Create: `app/src/main/java/com/streakly/data/local/entity/ChecklistItemEntity.kt`
- Create: `app/src/main/java/com/streakly/data/local/dao/WaterLogDao.kt`
- Create: `app/src/main/java/com/streakly/data/local/dao/JournalEntryDao.kt`
- Create: `app/src/main/java/com/streakly/data/local/dao/HabitDao.kt`
- Create: `app/src/main/java/com/streakly/data/local/dao/HabitLogDao.kt`
- Create: `app/src/main/java/com/streakly/data/local/dao/StreakCacheDao.kt`
- Create: `app/src/main/java/com/streakly/data/local/dao/CachedHealthDataDao.kt`
- Create: `app/src/main/java/com/streakly/data/local/dao/AgentTaskDao.kt`
- Create: `app/src/main/java/com/streakly/data/local/dao/GoalDao.kt`
- Create: `app/src/main/java/com/streakly/data/local/dao/ChecklistItemDao.kt`
- Create: `app/src/main/java/com/streakly/data/local/StreaklyDatabase.kt`

**Interfaces:**
- Consumes: Room schema verified against `StreaklyDatabase_Impl.java`
- Produces: `StreaklyDatabase` with abstract methods for all 9 DAOs

- [ ] **Step 1: Implement all 9 Entity classes matching exact table column names and primary keys**
- [ ] **Step 2: Implement all 9 DAO interfaces with Room SQL annotations matching compiled DAO implementations**
- [ ] **Step 3: Implement `StreaklyDatabase` RoomDatabase abstract class**
- [ ] **Step 4: Commit local data layer**

---

### Task 6: Health Connect Manager & Health Result

**Files:**
- Create: `app/src/main/java/com/streakly/data/healthconnect/HealthResult.kt`
- Create: `app/src/main/java/com/streakly/data/healthconnect/HealthConnectManager.kt`

**Interfaces:**
- Consumes: `androidx.health.connect.client.HealthConnectClient`
- Produces: Reactive flows for daily steps, hourly steps, sleep sessions, heart rates, workouts, and readiness score with deterministic demo fallback

- [ ] **Step 1: Implement sealed class `HealthResult<T>` (`Success`, `Error`, `Loading`)**
- [ ] **Step 2: Implement `HealthConnectManager` with permission checks, HealthConnectClient queries, and demo generator fallback**
- [ ] **Step 3: Commit Health Connect integration**

---

### Task 7: NVIDIA NIM Client & Models

**Files:**
- Create: `app/src/main/java/com/streakly/data/remote/nvidia/NvidiaNimClient.kt`
- Create: Moshi JSON data classes: `NvidiaChatRequest`, `NvidiaChatResponse`, `NvidiaChoice`, `NvidiaMessage`, `NvidiaTool`, `NvidiaFunction`, `NvidiaParameters`, `NvidiaProperty`, `NvidiaToolCall`, `NvidiaCallFunction`, `NvidiaUsage`

**Interfaces:**
- Produces: `NvidiaNimClient` interface with `@POST` Retrofit endpoint for OpenAI-compatible completions

- [ ] **Step 1: Implement all Moshi data classes with `@JsonClass(generateAdapter = true)`**
- [ ] **Step 2: Implement `NvidiaNimClient` Retrofit interface**
- [ ] **Step 3: Commit remote NVIDIA NIM client**

---

### Task 8: Repositories Implementation

**Files:**
- Create: `app/src/main/java/com/streakly/data/repository/WaterRepositoryImpl.kt`
- Create: `app/src/main/java/com/streakly/data/repository/HabitRepositoryImpl.kt`
- Create: `app/src/main/java/com/streakly/data/repository/GoalRepositoryImpl.kt`
- Create: `app/src/main/java/com/streakly/data/repository/JournalRepositoryImpl.kt`
- Create: `app/src/main/java/com/streakly/data/repository/SettingsRepositoryImpl.kt`
- Create: `app/src/main/java/com/streakly/data/repository/HealthRepositoryImpl.kt`

**Interfaces:**
- Consumes: DAOs, HealthConnectManager, DataStore Preferences, NotificationScheduler
- Produces: Concrete implementations of all 6 domain repositories

- [ ] **Step 1: Implement `WaterRepositoryImpl`**
- [ ] **Step 2: Implement `HabitRepositoryImpl` with 7-day completion status array and streak computation**
- [ ] **Step 3: Implement `GoalRepositoryImpl` with alarm scheduling**
- [ ] **Step 4: Implement `JournalRepositoryImpl` with search query flow**
- [ ] **Step 5: Implement `SettingsRepositoryImpl` with DataStore preferences**
- [ ] **Step 6: Implement `HealthRepositoryImpl` wrapping `HealthConnectManager`**
- [ ] **Step 7: Commit repositories implementation**

---

### Task 9: Dependency Injection Modules

**Files:**
- Create: `app/src/main/java/com/streakly/di/AppModule.kt`
- Create: `app/src/main/java/com/streakly/di/RepositoryModule.kt`

**Interfaces:**
- Produces: Hilt `@Module` `@InstallIn(SingletonComponent::class)` providing Database, DAOs, Gson, HealthConnectManager, NvidiaNimClient, and Repository bindings

- [ ] **Step 1: Implement `AppModule` providing Database, 9 DAOs, Gson, and NvidiaNimClient**
- [ ] **Step 2: Implement `RepositoryModule` with `@Binds` for all 6 repositories**
- [ ] **Step 3: Commit Hilt modules**

---

### Task 10: Background Workers, Receivers & Services

**Files:**
- Create: `app/src/main/java/com/streakly/worker/HealthSyncWorker.kt`
- Create: `app/src/main/java/com/streakly/worker/NotificationWorker.kt`
- Create: `app/src/main/java/com/streakly/worker/AlarmReceiver.kt`
- Create: `app/src/main/java/com/streakly/worker/BootReceiver.kt`
- Create: `app/src/main/java/com/streakly/worker/WorkScheduler.kt`
- Create: `app/src/main/java/com/streakly/service/StreaklyFirebaseMessagingService.kt`

**Interfaces:**
- Consumes: WorkManager, AlarmManager, Repositories, NotificationHelper
- Produces: Background worker synchronization and alarm broadcast receivers

- [ ] **Step 1: Implement `HealthSyncWorker` with `@HiltWorker` to sync Health Connect data**
- [ ] **Step 2: Implement `NotificationWorker` with `@HiltWorker` for hydration/inactivity reminders**
- [ ] **Step 3: Implement `AlarmReceiver` to display goal, checklist, and bedtime alerts**
- [ ] **Step 4: Implement `BootReceiver` to restore alarms on reboot**
- [ ] **Step 5: Implement `WorkScheduler` to register periodic WorkManager tasks**
- [ ] **Step 6: Implement `StreaklyFirebaseMessagingService`**
- [ ] **Step 7: Commit workers and receivers**

---

### Task 11: Theme & Reusable UI Components

**Files:**
- Create: `app/src/main/java/com/streakly/ui/theme/Color.kt`
- Create: `app/src/main/java/com/streakly/ui/theme/Shape.kt`
- Create: `app/src/main/java/com/streakly/ui/theme/Type.kt`
- Create: `app/src/main/java/com/streakly/ui/theme/Theme.kt`
- Create: `app/src/main/java/com/streakly/ui/common/UiComponents.kt`

**Interfaces:**
- Produces: Theme tokens and composables: `StreaklyCard`, `ProgressRing`, `MiniBarChart`, `MiniLineChart`, `StackedBar`, `SectionHeader`, `EmptyState`

- [ ] **Step 1: Implement `Color.kt` with Light and Dark palette constants**
- [ ] **Step 2: Implement `Shape.kt`, `Type.kt`, and `Theme.kt` (`StreaklyTheme`)**
- [ ] **Step 3: Implement `UiComponents.kt` with canvas animations and sparkline charts**
- [ ] **Step 4: Commit theme and UI components**

---

### Task 12: Navigation & App Shell

**Files:**
- Create: `app/src/main/java/com/streakly/ui/navigation/Screen.kt`
- Create: `app/src/main/java/com/streakly/ui/navigation/FloatingBottomNavBar.kt`
- Create: `app/src/main/java/com/streakly/ui/StreaklyApp.kt`
- Create: `app/src/main/java/com/streakly/StreaklyApplication.kt`
- Create: `app/src/main/java/com/streakly/MainActivity.kt`
- Create: `app/src/main/java/com/streakly/MainViewModel.kt`

**Interfaces:**
- Consumes: Screen routes, bottom items, ViewModels
- Produces: Root Compose Navigation graph and application startup configuration

- [ ] **Step 1: Implement `Screen.kt` sealed class with routes, labels, and `bottomItems`**
- [ ] **Step 2: Implement `FloatingBottomNavBar.kt` pill bottom navigation bar**
- [ ] **Step 3: Implement `StreaklyApp.kt` with NavHost routing to all screens**
- [ ] **Step 4: Implement `StreaklyApplication.kt` with `@HiltAndroidApp` and WorkScheduler init**
- [ ] **Step 5: Implement `MainActivity.kt` with `@AndroidEntryPoint` and `MainViewModel.kt`**
- [ ] **Step 6: Commit navigation and app shell**

---

### Task 13: Feature Screens & ViewModels

**Files:**
- Create: `app/src/main/java/com/streakly/ui/today/TodayUiState.kt` & `TodayViewModel.kt` & `TodayScreen.kt`
- Create: `app/src/main/java/com/streakly/ui/fitness/FitnessUiState.kt` & `FitnessViewModel.kt` & `FitnessScreen.kt`
- Create: `app/src/main/java/com/streakly/ui/sleep/SleepUiState.kt` & `SleepViewModel.kt` & `SleepScreen.kt`
- Create: `app/src/main/java/com/streakly/ui/health/HealthScreen.kt`
- Create: `app/src/main/java/com/streakly/ui/journal/JournalViewModel.kt` & `JournalScreen.kt`
- Create: `app/src/main/java/com/streakly/ui/habits/HabitTrackerViewModel.kt` & `HabitTrackerScreen.kt`
- Create: `app/src/main/java/com/streakly/ui/settings/SettingsViewModel.kt` & `SettingsScreen.kt`
- Create: `app/src/main/java/com/streakly/ui/chat/ChatMessage.kt` & `ChatUiState.kt` & `ChatViewModel.kt` & `ChatScreen.kt`
- Create: `app/src/main/java/com/streakly/ui/auth/OnboardingScreen.kt`

**Interfaces:**
- Consumes: Repositories, UI Components, AI Agent tools
- Produces: All 9 feature screens with interactive UI and ViewModel logic

- [ ] **Step 1: Implement Today screen, ViewModel, and UI state**
- [ ] **Step 2: Implement Fitness screen, ViewModel, and UI state**
- [ ] **Step 3: Implement Sleep screen, ViewModel, and UI state**
- [ ] **Step 4: Implement Health screen with vitals grid and nutrition macros**
- [ ] **Step 5: Implement Journal screen, ViewModel, and EntryDialog**
- [ ] **Step 6: Implement HabitTracker screen, ViewModel, and habit list**
- [ ] **Step 7: Implement Settings screen, ViewModel, and AI preferences**
- [ ] **Step 8: Implement Chat screen, ViewModel with autonomous tool-calling loop**
- [ ] **Step 9: Implement Onboarding screen**
- [ ] **Step 10: Commit feature screens**

---

### Task 14: Unit Tests

**Files:**
- Create: `app/src/test/java/com/streakly/util/DateUtilsTest.kt`
- Create: `app/src/test/java/com/streakly/domain/HabitStreakCalculatorTest.kt`
- Create: `app/src/test/java/com/streakly/domain/HealthReadinessTest.kt`

**Interfaces:**
- Tests: Core utility, streak computation, and health score business logic

- [ ] **Step 1: Implement `DateUtilsTest.kt`**
- [ ] **Step 2: Implement `HabitStreakCalculatorTest.kt`**
- [ ] **Step 3: Implement `HealthReadinessTest.kt`**
- [ ] **Step 4: Commit unit tests**

---

### Task 15: GitHub Actions CI Workflow

**Files:**
- Create: `.github/workflows/build-apk.yml`
- Create: `README.md`

**Interfaces:**
- Produces: Fully automated GitHub Actions workflow to build the APK and documentation on how to trigger it

- [ ] **Step 1: Create `.github/workflows/build-apk.yml` with triggers on push, PR, and `workflow_dispatch`**
- [ ] **Step 2: Create comprehensive `README.md` with features, architecture, and instructions for GitHub Actions APK build**
- [ ] **Step 3: Commit workflow and README**
- [ ] **Step 4: Final verification check of all files against the project directory**
