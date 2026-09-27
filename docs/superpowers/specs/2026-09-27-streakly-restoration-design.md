# Streakly Complete Restoration & GitHub Actions CI Specification

**Date**: 2026-09-27  
**Status**: Approved  
**Target Environment**: Android (API 28–35), Kotlin 1.9.23, Jetpack Compose, Hilt 2.50, Room 2.6.1, Health Connect, GitHub Actions CI  

---

## 1. Executive Summary & Objectives

Streakly is an Android health, fitness, habit, and streak tracking application built with modern Android standards:
- **Clean Architecture & MVI/MVVM**: Declarative UI using Jetpack Compose with unidirectional data flow (StateFlow).
- **Offline-First Persistence**: Room database with 9 distinct tables for habits, logs, streaks, water tracking, reflection journals, daily goals, checklist items, cached health metrics, and autonomous AI agent tasks.
- **Health Connect Integration**: Reads steps, heart rates, sleep stages, active calories, HRV, VO2 Max, distance, and exercise sessions, with demo fallbacks for devices without Health Connect installed.
- **NVIDIA NIM AI Coach**: Local autonomous tool-calling loop connecting to NVIDIA NIM's OpenAI-compatible chat completions API to manage tasks, fetch daily health statistics, and personalize daily routines.
- **Background Synchronization & Notifications**: AndroidX WorkManager and AlarmManager exact alarms to ensure timely hydration reminders, bedtime wind-downs, streak-at-risk warnings, and reboot persistence.
- **Automated GitHub Actions CI**: A pipeline that compiles, tests, builds the debug APK, and uploads it as an artifact directly on GitHub runners.

---

## 2. Directory Layout & Module Structure

The project is structured as a standard single-module Gradle project:

```
Streakly/
├── .github/
│   └── workflows/
│       └── build-apk.yml
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/streakly/
│           │   ├── StreaklyApplication.kt
│           │   ├── MainActivity.kt
│           │   ├── MainViewModel.kt
│           │   ├── data/
│           │   │   ├── healthconnect/
│           │   │   │   ├── HealthConnectManager.kt
│           │   │   │   └── HealthResult.kt
│           │   │   ├── local/
│           │   │   │   ├── StreaklyDatabase.kt
│           │   │   │   ├── dao/
│           │   │   │   │   ├── AgentTaskDao.kt
│           │   │   │   │   ├── CachedHealthDataDao.kt
│           │   │   │   │   ├── ChecklistItemDao.kt
│           │   │   │   │   ├── GoalDao.kt
│           │   │   │   │   ├── HabitDao.kt
│           │   │   │   │   ├── HabitLogDao.kt
│           │   │   │   │   ├── JournalEntryDao.kt
│           │   │   │   │   ├── StreakCacheDao.kt
│           │   │   │   │   └── WaterLogDao.kt
│           │   │   │   └── entity/
│           │   │   │       ├── AgentTaskEntity.kt
│           │   │   │       ├── CachedHealthDataEntity.kt
│           │   │   │       ├── ChecklistItemEntity.kt
│           │   │   │       ├── GoalEntity.kt
│           │   │   │       ├── HabitEntity.kt
│           │   │   │       ├── HabitLogEntity.kt
│           │   │   │       ├── JournalEntryEntity.kt
│           │   │   │       ├── StreakCacheEntity.kt
│           │   │   │       └── WaterLogEntity.kt
│           │   │   ├── remote/nvidia/
│           │   │   │   ├── NvidiaNimClient.kt
│           │   │   │   ├── NvidiaChatRequest.kt
│           │   │   │   ├── NvidiaChatResponse.kt
│           │   │   │   ├── NvidiaChoice.kt
│           │   │   │   ├── NvidiaMessage.kt
│           │   │   │   ├── NvidiaTool.kt
│           │   │   │   ├── NvidiaFunction.kt
│           │   │   │   ├── NvidiaParameters.kt
│           │   │   │   ├── NvidiaProperty.kt
│           │   │   │   ├── NvidiaToolCall.kt
│           │   │   │   ├── NvidiaCallFunction.kt
│           │   │   │   └── NvidiaUsage.kt
│           │   │   └── repository/
│           │   │       ├── GoalRepositoryImpl.kt
│           │   │       ├── HabitRepositoryImpl.kt
│           │   │       ├── HealthRepositoryImpl.kt
│           │   │       ├── JournalRepositoryImpl.kt
│           │   │       ├── SettingsRepositoryImpl.kt
│           │   │       └── WaterRepositoryImpl.kt
│           │   ├── di/
│           │   │   ├── AppModule.kt
│           │   │   └── RepositoryModule.kt
│           │   ├── domain/
│           │   │   ├── model/
│           │   │   │   ├── GoalModels.kt
│           │   │   │   ├── HabitModels.kt
│           │   │   │   ├── HealthModels.kt
│           │   │   │   ├── JournalModels.kt
│           │   │   │   ├── NutritionModels.kt
│           │   │   │   ├── StreakModels.kt
│           │   │   │   └── UserProfile.kt
│           │   │   └── repository/
│           │   │       ├── GoalRepository.kt
│           │   │       ├── HabitRepository.kt
│           │   │       ├── HealthRepository.kt
│           │   │       ├── JournalRepository.kt
│           │   │       ├── SettingsRepository.kt
│           │   │       └── WaterRepository.kt
│           │   ├── service/
│           │   │   └── StreaklyFirebaseMessagingService.kt
│           │   ├── ui/
│           │   │   ├── StreaklyApp.kt
│           │   │   ├── auth/OnboardingScreen.kt
│           │   │   ├── chat/
│           │   │   │   ├── ChatScreen.kt
│           │   │   │   └── ChatViewModel.kt
│           │   │   ├── common/UiComponents.kt
│           │   │   ├── fitness/
│           │   │   │   ├── FitnessScreen.kt
│           │   │   │   └── FitnessViewModel.kt
│           │   │   ├── habits/
│           │   │   │   ├── HabitTrackerScreen.kt
│           │   │   │   └── HabitTrackerViewModel.kt
│           │   │   ├── health/HealthScreen.kt
│           │   │   ├── journal/
│           │   │   │   ├── JournalScreen.kt
│           │   │   │   └── JournalViewModel.kt
│           │   │   ├── navigation/
│           │   │   │   ├── FloatingBottomNavBar.kt
│           │   │   │   └── Screen.kt
│           │   │   ├── settings/
│           │   │   │   ├── SettingsScreen.kt
│           │   │   │   └── SettingsViewModel.kt
│           │   │   ├── sleep/
│           │   │   │   ├── SleepScreen.kt
│           │   │   │   └── SleepViewModel.kt
│           │   │   ├── theme/
│           │   │   │   ├── Color.kt
│           │   │   │   ├── Shape.kt
│           │   │   │   ├── Theme.kt
│           │   │   │   └── Type.kt
│           │   │   └── today/
│           │   │       ├── TodayScreen.kt
│           │   │       └── TodayViewModel.kt
│           │   ├── util/
│           │   │   ├── DateUtils.kt
│           │   │   ├── Extensions.kt
│           │   │   ├── NotificationHelper.kt
│           │   │   └── NotificationScheduler.kt
│           │   └── worker/
│           │       ├── AlarmReceiver.kt
│           │       ├── BootReceiver.kt
│           │       ├── HealthSyncWorker.kt
│           │       ├── NotificationWorker.kt
│           │       └── WorkScheduler.kt
│           └── res/
│               ├── drawable/ic_streakly_logo.xml
│               └── values/
│                   ├── colors.xml
│                   ├── strings.xml
│                   └── themes.xml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
└── gradlew.bat
```

---

## 3. Database Schema & Data Models

### Database Configuration
- **Database Class**: `StreaklyDatabase` extends `RoomDatabase`
- **Version**: 2
- **Export Schema**: false

### Tables & Entities
1. **`water_logs` (`WaterLogEntity`)**:
   - `id`: Long (PK, autoGenerate = true)
   - `date`: String (ISO "yyyy-MM-dd")
   - `glassesCount`: Int
   - `lastUpdated`: Long (epoch milliseconds)

2. **`journal_entries` (`JournalEntryEntity`)**:
   - `id`: Long (PK)
   - `title`: String
   - `body`: String
   - `mood`: Int (ordinal of Mood: GREAT=5, GOOD=4, NEUTRAL=3, BAD=2, TERRIBLE=1)
   - `energyLevel`: Int (1 to 5)
   - `tags`: String (JSON-encoded list of tags)
   - `hasHealthSnapshot`: Boolean
   - `snapshotSteps`: Int?
   - `snapshotSleepScore`: Int?
   - `snapshotActiveMin`: Int?
   - `snapshotCalories`: Int?
   - `createdAt`: Long
   - `updatedAt`: Long

3. **`habits` (`HabitEntity`)**:
   - `id`: Long (PK, autoGenerate = true)
   - `name`: String
   - `iconName`: String
   - `colorHex`: String
   - `frequencyJson`: String (JSON list of days: e.g. `["MONDAY", "WEDNESDAY"]`)
   - `timeOfDay`: String (ANYTIME, MORNING, AFTERNOON, EVENING)
   - `linkedMetric`: String?
   - `createdAt`: Long

4. **`habit_logs` (`HabitLogEntity`)**:
   - `id`: Long (PK, autoGenerate = true)
   - `habitId`: Long (Foreign key reference to habits.id)
   - `date`: String (ISO "yyyy-MM-dd")
   - `completed`: Boolean
   - `completedAt`: Long?

5. **`streak_cache` (`StreakCacheEntity`)**:
   - `type`: String (PK: WORKOUT, WATER, JOURNAL, OVERALL)
   - `currentStreak`: Int
   - `longestStreak`: Int
   - `lastCompletedDate`: String
   - `updatedAt`: Long

6. **`cached_health_data` (`CachedHealthDataEntity`)**:
   - `date`: String (PK, ISO "yyyy-MM-dd")
   - `stepsTotal`: Int
   - `activeMinutes`: Int
   - `caloriesBurned`: Int
   - `distanceMeters`: Double
   - `sleepScore`: Int?
   - `restingHR`: Int?
   - `syncedAt`: Long

7. **`agent_tasks` (`AgentTaskEntity`)**:
   - `id`: Long (PK, autoGenerate = true)
   - `title`: String
   - `time`: String ("HH:mm")
   - `isGoal`: Boolean
   - `isCompleted`: Boolean
   - `date`: String (ISO "yyyy-MM-dd")

8. **`goals` (`GoalEntity`)**:
   - `id`: Long (PK, autoGenerate = true)
   - `title`: String
   - `date`: String (ISO "yyyy-MM-dd")
   - `completed`: Boolean
   - `notificationTime`: String? ("HH:mm")

9. **`checklist_items` (`ChecklistItemEntity`)**:
   - `id`: Long (PK, autoGenerate = true)
   - `title`: String
   - `date`: String (ISO "yyyy-MM-dd")
   - `completed`: Boolean
   - `notificationTime`: String? ("HH:mm")

---

## 4. Repositories & Data Sources

1. **`HealthConnectManager` & `HealthRepository`**:
   - Checks if Health Connect SDK is installed on the device (`HealthConnectClient.getSdkStatus()`).
   - If available: Reads `StepsRecord`, `HeartRateRecord`, `SleepSessionRecord`, `ActiveCaloriesBurnedRecord`, `DistanceRecord`, `ExerciseSessionRecord`, `OxygenSaturationRecord`, `Vo2MaxRecord`, `RespiratoryRateRecord`, and `HeartRateVariabilityRmssdRecord`.
   - If unavailable: Uses deterministic mock/demo data generators based on date seed so all UI screens render interactive graphs and realistic metrics.
   - Computes daily `ReadinessScore` (0-100) based on sleep duration, resting heart rate, and previous day's activity.

2. **`SettingsRepository`**:
   - Backed by Jetpack DataStore Preferences (`settings_prefs`).
   - Keys: User profile (name, email, photo URL), daily goals (steps, active minutes, sleep, water, cardio), notification toggles, theme mode (System/Light/Dark), night notification reminder time, and NVIDIA NIM configuration (API key, base URL, model name).

3. **`WaterRepository`**:
   - Exposes `getTodayWater(): Flow<WaterIntake>`.
   - Allows incrementing water by 1 glass (`addGlass()`) and setting explicit count (`setGlasses(count)`).

4. **`HabitRepository`**:
   - Exposes `getHabits(): Flow<List<HabitWithCompletion>>`.
   - Calculates consecutive streak count and 7-day completion status array.
   - `createHabit`, `toggleHabit(id, date)`, `deleteHabit(id)`.

5. **`GoalRepository`**:
   - Manages daily goals and checklist items.
   - Automatically invokes `NotificationScheduler` to register or cancel exact alarms when goals have scheduled times.

6. **`JournalRepository`**:
   - Reactive search and listing of journal reflections.
   - Supports creating quick entries with health snapshots captured from `HealthRepository`.

7. **`NvidiaNimClient` & AI Agent**:
   - Retrofit service consuming `/v1/chat/completions`.
   - Handles multi-turn tool calling with function schemas:
     - `getStepsToday()`: Fetches current steps.
     - `getGoalsChecklist()`: Retrieves today's goals and checklist items.
     - `addGoal(title, time, isGoal)`: Inserts a goal or checklist item into `agent_tasks`.
     - `toggleGoal(id, isGoal)`: Toggles completion in Room DB.
     - `setNightNotificationTime(time)`: Updates night notification preference and reschedules alarm.

---

## 5. UI Presentation & Navigation

1. **Theme & Styling**:
   - **Colors**:
     - Light: Background `#FFF6F6F6`, Surface `#FFFFFF`, Primary `#4A90E2`, Secondary `#7ED6A0`, Accent `#5B6EF5`, Error `#FFE05C5C`, TextPrimary `#111111`, TextSecondary `#888888`.
     - Dark: Background `#0F0F10`, Surface `#1C1C1E`, Primary `#4A90E2`, Secondary `#7ED6A0`, Accent `#5B6EF5`, Error `#FFE05C5C`, TextPrimary `#F0F0F0`, TextSecondary `#AAAAAA`.
   - **Shapes**: `RoundedCornerShape` (Card: 16dp, Button: 12dp, Chip: 8dp, BottomNav: 24dp).
   - **Typography**: Clean scale with Plus Jakarta Sans and Inter font families.

2. **Screens**:
   - **`Screen.Today` (`today`)**: Greeting, Today's Date, Health Connect status indicator, Hero Progress Ring (steps / target), Hourly bar chart, Sleep preview card, Water Intake counter with quick +1 button, Recent workout card, and Discipline preview.
   - **`Screen.Fitness` (`fitness`)**: Weekly cardio active minutes progress, VO2 Max card, logged workouts list, and Weekly steps leaderboard.
   - **`Screen.Sleep` (`sleep`)**: Sleep score gauge, duration breakdown, sleep stages stacked bar (Deep, REM, Light, Awake), resting HR, and 7-night trendline.
   - **`Screen.Health` (`health`)**: Daily readiness score, vitals grid (Resting HR, SpO2, HRV, Respiratory Rate), nutrition & macronutrient targets.
   - **`Screen.Journal` (`journal`)**: Reflection feed, mood emoji indicators, search bar, and New Entry dialog with health snapshot attachment.
   - **`Screen.Habits` (`habits`)**: Daily habit list with flame streaks, 7-day completion bubbles, and new habit creator.
   - **`Screen.Chat` (`chat`)**: NVIDIA NIM AI Health Coach chat interface with tool-execution feedback and quick suggestions.
   - **`Screen.Settings` (`settings`)**: Profile header, goal sliders, notification switches, theme toggle, and AI API settings.
   - **`OnboardingScreen`**: Intro splash and sign-in flow.
   - **`FloatingBottomNavBar`**: Floating pill bar displaying Today, Fitness, Sleep, Health, and Journal.

---

## 6. Background Workers & Alarms

- **`WorkScheduler`**: Configures `PeriodicWorkRequestBuilder` for `HealthSyncWorker` (every 6 hours) and `NotificationWorker` (every 24 hours).
- **`HealthSyncWorker`**: Runs in background, fetches latest records from Health Connect, and caches into `cached_health_data`.
- **`AlarmReceiver`**: Receives `com.streakly.ACTION_GOAL_REMINDER` and `com.streakly.ACTION_NIGHT_REMINDER`. Shows notifications via `NotificationHelper`.
- **`BootReceiver`**: Listens for `android.intent.action.BOOT_COMPLETED` and queries `GoalRepository` + `SettingsRepository` to re-register all exact alarms with `AlarmManager`.

---

## 7. GitHub Actions CI Configuration (`.github/workflows/build-apk.yml`)

The CI workflow will:
1. Trigger on push to `main`/`master` and on manual dispatch (`workflow_dispatch`).
2. Run on `ubuntu-latest`.
3. Set up JDK 17 with Temurin distribution.
4. Set up Gradle via `gradle/actions/setup-gradle@v3`.
5. Grant execute permission to `gradlew`.
6. Run `./gradlew assembleDebug --stacktrace`.
7. Upload `app/build/outputs/apk/debug/app-debug.apk` as a workflow artifact named `streakly-debug-apk`.

---

## 8. Verification & Quality Gates

1. **Gradle Compilation**: `./gradlew assembleDebug` succeeds without missing classes or symbol errors.
2. **Schema Verification**: Room database matches the 9 tables and column types verified against `StreaklyDatabase_Impl.java`.
3. **Resource Completeness**: All `@string/*`, `@color/*`, `@style/*`, and `@drawable/*` references resolve.
4. **CI Workflow Verification**: The GitHub Actions YAML file is syntactically valid and properly references paths and artifacts.
