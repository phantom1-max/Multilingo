# Multilingo

A mobile-first programming-learning app concept that combines short, interactive lessons with practical coding paths.

## Current prototype

- Native Android app built with Kotlin and Jetpack Compose.
- Welcome screen with Google, email and GitHub sign-in entry points (visual placeholders only; authentication is not connected yet).
- Home dashboard with XP, daily mission and learning path.
- Course catalogue: Python, Java, JavaScript, HTML & CSS, SQL and C++.
- Interactive multiple-choice lesson flow with instant feedback and XP.
- Local XP persistence and a dark-theme setting.
- Original pixel-art companion: Byte.

## Build the APK

The GitHub Actions workflow builds a debug APK on pushes to `main` and manual workflow dispatches.

1. Open the **Actions** tab.
2. Select **Build Multilingo APK**.
3. Run the workflow or wait for the push build to finish.
4. Download the `multilingo-debug-apk` artifact.

To publish an APK under **Releases**, push a version tag such as `v0.1.0`.

## Development status

This is an early prototype, not a complete Duolingo/Mimo replacement. Authentication providers, real code execution, cloud sync, full course curricula, lesson authoring, accessibility QA and production signing still need implementation. The current lesson experience demonstrates the learning interaction; it does not yet compile or execute arbitrary programs on-device.

## Inspiration

The product borrows broad learning patterns—short exercises, progress feedback, streak-style motivation and project-based coding practice—while using original branding, UI and character art.
