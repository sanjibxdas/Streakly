# Streakly 🔥

> **Autonomous AI Health Operating System & Habit Tracker for Android**

Streakly is a native Android application built with modern Jetpack Compose (Material 3), Clean Architecture, Android Health Connect, and NVIDIA NIM AI Coach. It empowers users to achieve optimal physical vitality, cognitive focus, and unshakeable daily discipline through biometric scoring, autonomous habit tracking, and proactive AI coaching.

---

## 🚀 Key Features

### 1. 📊 Today Executive Dashboard
- **Daily Readiness Engine**: Autonomous 0–100 score synthesizing sleep duration, resting heart rate, and step volume.
- **Hydration Tracking**: Rapid logging with +250ml, +500ml, +750ml increments and real-time circular progress indicator.
- **Biometric Cards**: Live step count, active calories, sleep duration, and active habit status.

### 2. 🏃 Health Connect & Biometrics
- **Direct Integration with Android Health Connect**: Syncs steps, distance, active calories, sleep stages (Deep, Light, REM, Awake), resting heart rate, SpO2, and HRV.
- **Intelligent Deterministic Fallback**: Generates realistic demo biometrics if Health Connect is unavailable on the device.
- **Canvas-Powered Visualizations**: Custom animated charts (`MiniBarChart`, `MiniLineChart`, `ProgressRing`, `StackedBar`).

### 3. 🔥 Habits & Streaks
- **Discipline Engine**: Consecutive streak calculation with freeze protection.
- **7-Day Visual History**: Interactive mini-bubble indicators for weekly habit completion.
- **Time of Day Classification**: Categorize routines by Morning, Afternoon, Evening, or Anytime.

### 4. 🤖 NVIDIA NIM AI Health Coach
- **State-of-the-Art LLM Integration**: Powered by NVIDIA NIM (`meta/llama-3.3-70b-instruct`, `llama-3.1-405b`, `mixtral-8x22b`, `deepseek-r1`).
- **Autonomous Multi-Turn Tool Execution**: The AI Coach can autonomously execute app functions:
  - `get_health_metrics`: Queries live biometrics, steps, and readiness.
  - `log_water`: Records hydration amounts into Room database.
  - `get_habits`: Evaluates streaks and habits.
  - `create_habit`: Adds new routines automatically.
- **Quick Suggestion Prompts**: One-tap query chips for readiness, sleep evaluation, and hydration.

### 5. 📖 Reflective Journal & Mental Wellbeing
- **Mood & Energy Tracking**: Emoji sentiment tags with 1–5 energy scale.
- **Biometric Snapshot Attachment**: Automatically attaches today's step count, calories, and sleep to reflections.
- **Instant Search**: Filter journal entries by title, keywords, or tags.

### 6. ⚙️ Background Services & Exact Alarms
- **WorkManager**: Periodic 15-minute background synchronization via `HealthSyncWorker`.
- **Exact Alarms**: Daily evening summary and scheduled hydration reminders via `AlarmManager` with `BOOT_COMPLETED` auto-rescheduling.

---

## 🛠️ Architecture & Tech Stack

| Component | Technology |
|---|---|
| **Language** | Kotlin 1.9.23 (JVM Target 17) |
| **Minimum / Target SDK** | minSdk 28 (Android 9.0) / targetSdk 35 (Android 15) |
| **Build System** | Gradle 8.7 & Android Gradle Plugin 8.5.1 |
| **UI Framework** | Jetpack Compose (BOM 2024.06.00) with Material 3 |
| **Architecture Pattern** | Clean Architecture (Domain, Data, UI, Worker) + MVI/MVVM with unidirectional data flow |
| **Dependency Injection** | Dagger Hilt 2.50 (`@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`, `@HiltWorker`) |
| **Local Persistence** | AndroidX Room 2.6.1 (Version 2 with 9 tables & DAOs) |
| **Preferences** | Jetpack DataStore Preferences 1.1.1 |
| **Health API** | AndroidX Health Connect SDK (`1.1.0-alpha11`) |
| **AI / Networking** | Retrofit 2.11.0 + Moshi 1.15.1 + OkHttp 4.12.0 |
| **Background Tasks** | AndroidX WorkManager 2.9.0 + AlarmManager |

---

## 📦 Building the APK via GitHub Actions

This repository includes a ready-to-run GitHub Actions workflow (`.github/workflows/build-apk.yml`) to automatically compile and publish the debug APK without requiring local Android SDK installation:

1. **Push your code or open a Pull Request** to `main`, `master`, or `restore-streakly-app`.
2. Go to the **Actions** tab in your GitHub repository.
3. Select **Build Streakly Android Debug APK** and click **Run workflow**.
4. Once completed, download the compiled APK artifact named **`streakly-debug-apk`** from the workflow summary page!

---

## 💻 Local Build Instructions

### Prerequisites
- JDK 17 (Temurin or OpenJDK)
- Android Studio Ladybug / Koala or Android SDK Command-line Tools (API 35, Build-Tools 35.0.0)

### Commands

```bash
# Clone the repository
git clone https://github.com/your-username/Streakly.git
cd Streakly

# Run Unit Tests
./gradlew testDebugUnitTest

# Assemble Debug APK
./gradlew assembleDebug
```

The output APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🔒 Privacy & Permissions

- **Local-First Database**: All biometric records, habit history, and journal notes are stored in an encrypted local SQLite/Room database on the user's device.
- **Optional Cloud AI**: Communication with NVIDIA NIM occurs only if the user explicitly configures an API key in Settings.
- **Granular Health Connect Permissions**: Streakly declares only read permissions for steps, calories, sleep, heart rate, and nutrition.

---

## 📄 License

This project is licensed under the Apache License 2.0.
