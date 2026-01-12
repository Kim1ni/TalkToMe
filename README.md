Mental health support shouldn't feel clinical or transactional. **TalkToMe** is your personal AI companion that listens, understands, and helps you navigate life's challenges through natural voice conversations. Inspired by the need for accessible mental wellness tools, it combines the power of real-time AI audio interactions with thoughtful session analysis to provide personalized insights and actionable guidance. Each conversation adapts to your emotional state, creating a safe space for reflection and growth. Built with Compose Multiplatform for both Android and iOS, this project demonstrates how modern AI can make mental health support more human, accessible, and effective. Dive in and discover a new way to understand yourself. Welcome to

# TalkToMe


![logo_placeholder](assets/logo.png)

# Demo

PS: unmute the video

[Add your demo video here]

# How to run the project

## Prerequisites

You need a Mac with macOS to run iOS-specific code on simulated or real devices. You will also need [Android Studio](https://developer.android.com/studio), [Xcode](https://developer.apple.com/xcode/), [JDK 17+](https://www.oracle.com/java/technologies/downloads/), [KMP plugin](https://kotlinlang.org/docs/multiplatform-plugin-releases.html) and [Kotlin plugin](https://kotlinlang.org/docs/releases.html#update-to-a-new-release). Before downloading the project, please follow the [set up environment guide](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-create-first-app.html#set-up-the-environment).

## Firebase Setup

1. Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/)
2. Add Android and iOS apps to your project
3. Download `google-services.json` (Android) and place it in `composeApp/`
4. Download `GoogleService-Info.plist` (iOS) and place it in `iosApp/iosApp/`
5. Enable **Authentication** (Google Sign-In, Anonymous), **Firestore**, and **Storage** in your Firebase project

## Set up API Keys and Configuration

Create a `local.properties` file in the project root and add the following:
```properties
apiKey=your-gemini-api-key
model=gemini-3-flash-preview
liveModel=gemini-2.5-flash-native-audio-preview-12-2025
webClientId=your-google-web-client-id
```

> **Note**: Get your Gemini API key from [Google AI Studio](https://aistudio.google.com/). The `webClientId` is found in your Firebase Authentication settings or Google Cloud Console.

You are now ready to [follow the run application guide for either Android or iOS](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-create-first-app.html#run-your-application)!

# Features

✨ **Live Audio Sessions** - Real-time voice conversations with Gemini Live AI that adapt to your emotional state  
📊 **Session Analysis** - Comprehensive mood insights, sentiment tracking, and personalized action plans  
📈 **Dashboard** - Track your emotional journey with mood trends  
📝 **Smart Action Plans** - AI-generated todos based on conversation insights  
🎭 **Personas** - Customize your AI companion with different personalities and voices  
☁️ **Cloud Sync** - Secure storage of sessions and insights across devices  
🔒 **Privacy First** - Anonymous guest mode and secure Google authentication

# Built using

1. **[Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)**  
   Enables sharing code across iOS, Android, and other platforms with native performance.

2. **[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)**  
   Declarative UI framework for building beautiful, native UIs across platforms.

3. **[Google Gemini 2.0](https://deepmind.google/technologies/gemini/)**  
   Advanced multimodal AI for real-time audio interactions and intelligent session analysis.

4. **[Firebase(Gitlive library)](https://github.com/GitLiveApp/firebase-kotlin-sdk)**  
   Backend infrastructure for authentication, cloud storage, and real-time database.

5. **[Koin](https://insert-koin.io/docs/reference/koin-mp/kmp/)**  
   Lightweight dependency injection framework for clean architecture.

6. **[Ktor](https://ktor.io/)**  
   Asynchronous HTTP client for API communications.

7. **[Coil](https://coil-kt.github.io/coil/)**  
   Image loading library optimized for Compose and Kotlin Multiplatform.

8. **[Napier](https://github.com/AAkira/Napier)**  
   Multiplatform logging library for debugging and monitoring.

9. **[BuildKonfig](https://github.com/yshrsmz/BuildKonfig)**  
   Type-safe build configuration management for multiplatform projects.

10. **[KMP-Auth](https://github.com/mirzemehdi/KMPAuth)**  
   Cross-platform authentication library with Firebase integration.

11. **[Vico](https://github.com/patrykandpatrick/vico)**
   Creating and rendering charts for data visualization

12. **[Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings)** 
   Local Storage for settings

# Architecture
```text
┌────────────────────────────────────────────────────────────┐
│                      Presentation Layer                    │
│  ┌─────────────────────────────────────────────────────┐   │
│  │          Compose Multiplatform UI (commonMain)      │   │
│  │  • Home Screen    • Session Screen   • Profile      │   │
│  │  • History        • Login Screen     • Settings     │   │
│  └─────────────────────────────────────────────────────┘   │
│                            ▼                               │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                   ViewModels (State)                │   │
│  │  • HomeViewModel  • SessionViewModel  • etc.        │   │
│  └─────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────┘
                            ▼
┌────────────────────────────────────────────────────────────┐
│                       Domain Layer                         │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                     Use Cases                       │   │
│  │  • GetDashboardDataUseCase                          │   │
│  │  • AnalyzeSessionUseCase                            │   │
│  │  • ToggleTodoUseCase                                │   │
│  └─────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              Domain Models & Entities               │   │
│  │  • Session  • User  • Todo  • Persona               │   │
│  └─────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────┘
                            ▼
┌────────────────────────────────────────────────────────────┐
│                        Data Layer                          │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                   Repositories                      │   │
│  │  • AuthRepository  • SessionRepository              │   │
│  │  • UserRepository  • TodoRepository                 │   │
│  └─────────────────────────────────────────────────────┘   │
│                            ▼                               │
│  ┌──────────────────┐  ┌──────────────────┐                │
│  │  Firebase APIs   │  │   Gemini API     │                │
│  │  • Auth          │  │   • Live Audio   │                │
│  │  • Firestore     │  │   • Analysis     │                │
│  │  • Storage       │  │   • Generation   │                │
│  └──────────────────┘  └──────────────────┘                │
└────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                     Platform Layer                          │
│  ┌──────────────────┐          ┌──────────────────┐         │
│  │  Android Impl    │          │    iOS Impl      │         │
│  │  • Audio Stream  │          │  • Audio Stream  │         │
│  │  • Permissions   │          │  • Permissions   │         │
│  └──────────────────┘          └──────────────────┘         │
└─────────────────────────────────────────────────────────────┘
```


**Key Architectural Principles:**
- **Clean Architecture**: Clear separation between presentation, domain, and data layers
- **Unidirectional Data Flow**: State flows down, events flow up
- **Platform-Specific Implementations**: Native audio handling for optimal performance
- **Dependency Injection**: Koin for managing dependencies across all layers
- **Repository Pattern**: Abstract data sources for flexibility and testability

# Project Structure
```text
.
├── composeApp/                    # Shared KMP module
│   ├── src/
│   │   ├── commonMain/           # Shared code
│   │   │   ├── kotlin/
│   │   │   │   ├── data/         # Repositories & data sources
│   │   │   │   ├── domain/       # Use cases & business logic
│   │   │   │   ├── ui/           # Compose UI & ViewModels
│   │   │   │   └── di/           # Dependency injection
│   │   │   └── resources/        # Shared assets
│   │   ├── androidMain/          # Android-specific code
│   │   └── iosMain/              # iOS-specific code
│   ├── google-services.json      # Firebase config (Android)
│   └── build.gradle.kts
├── iosApp/                        # iOS native entry point
│   └── iosApp/
│       ├── GoogleService-Info.plist  # Firebase config (iOS)
│       └── iosApp.swift
├── gradle/
│   └── libs.versions.toml         # Dependency versions
├── local.properties               # Local config (git-ignored)
└── build.gradle.kts
```

# Roadmap

- [ ] **Multi-language Support** - Expand beyond English
- [ ] **Offline Mode** - Basic session recording without internet
- [ ] **Journal Export** - Export sessions as PDF/text
- [ ] **Detailed Analyses & To-dos** - Have larger assignments than just todos, maybe with journalling and assessments.
- [ ] **Voice Customization** - More persona voices and accents
- [ ] **Group Sessions** - Shared reflection with friends/family
- [ ] **Professional Insights** - Optional therapist review integration

# License

This project is licensed under the [MIT License](LICENSE).

---

**TalkToMe** - Empowering conversations, one session at a time. 🎙️💚