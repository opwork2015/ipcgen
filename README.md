# ipcgen

A lightweight Android app that lets you choose a local ISO/IMG file and launch it via QEMU in the background.

## Features

- Select a local disk image (`.iso`, `.img`, `.qcow2`)
- Start QEMU as a background service
- Stop the VM service from the app
- Uses Android foreground service for stable background execution
- QEMU binary lookup in common Android locations

## Included in this repository

- Android Studio / Gradle project structure
- Launcher icon and project branding assets
- README and setup notes
- QEMU usage instructions
- The app source for `MainActivity` and `QemuService`

## Project logo

The repository has a logo asset at:

- `assets/ipcgen-logo.svg`

## Requirements

- Android Studio
- Android SDK 23+
- Java 17+
- A QEMU binary placed on the device, typically at one of:
  - `/data/local/tmp/qemu-system-x86_64`
  - `/system/bin/qemu-system-x86_64`
  - `/system/xbin/qemu-system-x86_64`
  - `/data/data/com.iBlast.ipcgen/files/qemu-system-x86_64`

## Android setup

1. Open the project in Android Studio.
2. Let Gradle sync.
3. Connect a device or launch an emulator.
4. Build and install the APK.
5. Select a local ISO/IMG image.
6. Press Start to launch QEMU.

## QEMU notes

The app is designed to run a local QEMU binary that you provide manually. It does not download QEMU automatically.

For more details, see:

- `QEMU_DOWNLOAD.md`
- `INSTALLATION.md`

## License

This project is provided as-is for educational and local development use.

## Notes

The app is intentionally simple and can be used as a starting point for custom Android + QEMU projects.
