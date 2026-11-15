# PixelRoot (Material 3 Monochrome)

A Material 3 Monochrome redesign and expansion of the DFRoot launcher interface built with **Kotlin** and **Jetpack Compose**.

## Key Features
- **Material 3 Monochrome Theme**: Pure OLED Dark and Paper Light grayscale tonal surfaces (`surfaceContainerLowest` through `surfaceContainerHighest`).
- **DFRoot Interface & SU Manager Selector**: Features the signature 2x2 checkered squircle emblem, `LAUNCH ROOT` pill button, and **KernelSU** / **KernelSU-Next** selection cards.
- **Bottom Bar & Animated Settings Bottom Sheet**: Docked Material 3 bottom bar plus a `ModalBottomSheet` with spring-animated close transitions (`Start at Boot`, `Auto Soft Reboot`, `Disable KSU Modules`, and `OLED Dark Monochrome`).
- **Live Root Execution Log**: Automatically captures and displays timestamped `su` / KernelSU privilege logs after launching root or targeting an app.

## How to Build the APK

### Option 1: Command Line (Linux / macOS / Windows with Android SDK)
```bash
gradle assembleDebug
```
The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### Option 2: Android Studio
1. Extract this `.zip` archive.
2. Open the `PixelRoot` folder in **Android Studio**.
3. Let Gradle sync, then click **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

### Option 3: GitHub Actions (No PC Required)
1. Push this project to a GitHub repository.
2. Open the **Actions** tab and run **Build PixelRoot APK** (`.github/workflows/build-apk.yml`).
3. Download `PixelRoot-debug-apk` directly from the workflow artifacts.
