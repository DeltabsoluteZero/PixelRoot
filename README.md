# PixelRoot (Material 3 Monochrome)

A Material 3 Monochrome redesign and expansion of the DFRoot launcher interface built with **Kotlin** and **Jetpack Compose**.

## Key Features
- **Material 3 Monochrome Theme**: Pure OLED Dark and Paper Light grayscale tonal surfaces (`surfaceContainerLowest` through `surfaceContainerHighest`).
- **DFRoot Interface & SU Manager Selector**: Features the signature 2x2 checkered squircle emblem, `LAUNCH ROOT` pill button, and **KernelSU** / **KernelSU-Next** selection cards.
- **Bottom Bar & Animated Settings Bottom Sheet**: Docked Material 3 bottom bar plus a `ModalBottomSheet` with spring-animated close transitions (`Start at Boot`, `Auto Soft Reboot`, `Disable KSU Modules`, and `OLED Dark Monochrome`).
- **Live Root Execution Log**: Automatically captures and displays timestamped `su` / KernelSU privilege logs after launching root or targeting an app.
- **Temp Root Session Manager**: Verify and track a `fastboot boot`-based root session (nothing flashed) — see [Device Support](#device-support) below.

## Device Support

PixelRoot's Temp Root tab is a **session manager**, not a rooting method — it verifies and tracks root that already exists via KernelSU, KernelSU-Next, or Magisk. It does not patch, flash, or exploit anything itself. What it can support depends entirely on whether your device's bootloader can be unlocked:

| Bootloader state | Temp Root tab | Why |
|---|---|---|
| **Unlocked / unlockable** (most Pixels, many OEMs with OEM unlock toggle) | ✅ Supported | `fastboot boot <patched-image>.img` boots a KernelSU/Magisk-patched image from RAM for that session only. Verified Boot accepts it because the bootloader is unlocked. Reboot returns to the untouched stock boot partition automatically. |
| **Locked, no unlock option exposed** (e.g. most Samsung One UI devices — Knox-gated, many carrier variants globally block OEM unlock entirely) | ❌ Not supported | Android Verified Boot (AVB) refuses to execute any boot image not signed by a key the locked bootloader trusts. There is no `fastboot boot` workaround for this — accepting an unsigned image is exactly what a locked bootloader is designed to prevent. On these devices `su` is never present, so the Verify Session check correctly stays on "No Session Verified" rather than reporting a false positive. |
| **Locked, but OEM unlock can be enabled** (Developer Options → OEM unlocking) | ✅ Supported, after unlocking | Unlock first (this is a device-level, user-initiated action in Settings — not something PixelRoot does), then proceed as the "Unlocked" row above. |

**This is a hard boundary, not a missing feature.** Bypassing AVB on a locked bootloader requires exploiting a kernel or bootloader vulnerability to subvert verified boot — that's outside the scope of this project and won't be added. If your device's bootloader can't be unlocked, PixelRoot (and KernelSU/Magisk themselves) cannot root it, temporarily or otherwise.

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
