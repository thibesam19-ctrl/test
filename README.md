# Axiom AI Coach — Android Application

A premium AI-powered fitness transformation platform built with Kotlin, Jetpack Compose, and MVVM architecture.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Database | Room |
| Async | Coroutines + Flow |
| AI | OpenAI / Claude / Gemini / Local |
| Camera | CameraX + ML Kit |
| Analytics | Firebase Analytics + Crashlytics |
| Monetization | RevenueCat (ready) |

---

## Building the App

### Prerequisites

- **JDK 17** or higher
- **Android Studio Hedgehog** (2023.1.1) or newer
- **Android SDK** with:
  - Platform 34 (Android 14)
  - Build Tools 34.0.0
- Firebase project (for full features; placeholder included for builds)

### Quick Build (Debug APK)

```bash
# Clone the repository
git clone https://github.com/thibesam19-ctrl/test.git
cd test

# Set SDK path
echo "sdk.dir=$ANDROID_HOME" > local.properties

# Build debug APK
./gradlew assembleDebug

# APK output location:
# app/build/outputs/apk/debug/app-debug.apk
```

### Release APK

```bash
# Generate keystore (one-time setup)
keytool -genkey -v -keystore axiom-release.jks \
  -alias axiom -keyalg RSA -keysize 2048 -validity 10000

# Build signed release APK
KEYSTORE_PATH=axiom-release.jks \
KEYSTORE_PASSWORD=<your-password> \
KEY_ALIAS=axiom \
KEY_PASSWORD=<your-password> \
./gradlew assembleRelease
```

---

## Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a new project named **Axiom AI Coach**
3. Add Android app with package name `com.axiom.aicoach`
4. Download `google-services.json` and place it at `app/google-services.json`
5. Enable these Firebase services:
   - Authentication (Email/Password + Google Sign-In)
   - Firestore Database
   - Analytics
   - Crashlytics
   - Cloud Messaging (FCM)

> **Note:** The repository includes a placeholder `google-services.json` for CI builds. Replace it with your real Firebase config for production.

---

## API Keys Configuration

Add to `local.properties` (never commit real keys):

```properties
OPENAI_API_KEY=sk-...
CLAUDE_API_KEY=sk-ant-...
GEMINI_API_KEY=AI...
REVENUECAT_API_KEY=appl_...
```

Or set as environment variables for CI/CD:

```bash
export OPENAI_API_KEY=sk-...
export CLAUDE_API_KEY=sk-ant-...
```

---

## RevenueCat Setup

1. Create account at [RevenueCat](https://www.revenuecat.com)
2. Set up app in dashboard with package `com.axiom.aicoach`
3. Create products matching entitlement IDs:
   - `premium` — monthly/yearly subscription
   - `unlimited` — lifetime or highest tier
4. Add `REVENUECAT_API_KEY` to your config

---

## CI/CD — GitHub Actions

The workflow at `.github/workflows/build.yml` automatically:

- Builds debug APK on every push to feature branches
- Builds release APK on `main`/`master`
- Runs unit tests
- Uploads APK artifacts (retained 30 days for debug, 90 for release)

### Required GitHub Secrets

| Secret | Description |
|---|---|
| `OPENAI_API_KEY` | OpenAI API key (optional) |
| `CLAUDE_API_KEY` | Anthropic Claude API key (optional) |
| `GEMINI_API_KEY` | Google Gemini API key (optional) |
| `REVENUECAT_API_KEY` | RevenueCat public SDK key |
| `RELEASE_KEYSTORE` | Base64-encoded release keystore |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |

Generate the base64-encoded keystore:
```bash
base64 -w 0 axiom-release.jks
```

---

## Project Structure

```
app/src/main/java/com/axiom/aicoach/
├── AxiomApp.kt                    # Application class
├── MainActivity.kt                # Entry point
├── ai/
│   ├── analytics/                 # InsightsEngine, RecommendationEngine
│   ├── coaching/                  # CoachingEngine, SafetyFilter
│   ├── consent/                   # AiConsentManager
│   ├── provider/                  # OpenAI, Claude, Gemini, Local providers
│   └── vision/
│       ├── body/                  # Body analysis engine
│       ├── food/                  # Food recognition
│       └── pose/                  # Pose analysis, rep counting
├── analytics/                     # Firebase Analytics tracker
├── billing/                       # RevenueCat integration
├── data/
│   ├── local/
│   │   ├── dao/                   # 20 Room DAOs
│   │   ├── database/              # AxiomDatabase
│   │   └── entities/              # 20 Room entities
│   └── repository/                # Repository implementations
├── di/                            # Hilt dependency injection modules
├── domain/model/                  # Domain models
├── network/                       # Connectivity observer
├── notifications/                 # WorkManager + FCM
├── security/                      # Encrypted storage, API key vault
└── ui/
    ├── components/                # Shared Compose components
    ├── navigation/                # NavGraph + bottom nav
    ├── screens/
    │   ├── auth/                  # Welcome, Sign In, Sign Up, Reset
    │   ├── coach/                 # AI Coach chat
    │   ├── dashboard/             # Main dashboard
    │   ├── legal/                 # Privacy, Terms
    │   ├── nutrition/             # Food tracking, barcode scanner
    │   ├── onboarding/            # Goal setup, paywall
    │   ├── progress/              # Body measurements, photos
    │   ├── settings/              # Profile, notifications, subscription
    │   └── workout/               # Plans, sessions, form analysis
    └── theme/                     # Material 3 theme + colors
```

---

## Known Limitations

- Firebase Authentication is stubbed (accepts any non-empty credentials)
- Real-time sync requires valid Firebase config
- AI features require API keys to activate (local fallback always available)
- Body scan and pose analysis require physical device with camera
- RevenueCat purchases require production app registration

---

## License

Copyright © 2025 Axiom AI Coach. All rights reserved.
