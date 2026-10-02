# QEMU Download and Offline Setup for ipcgen

## Important note

The app `ipcgen` can be built to run without internet access at runtime, but the QEMU binary itself is not a normal Android APK asset. You must provide the binary manually and keep it on the device locally.

You cannot safely embed a full native QEMU binary into an APK unless you compile a suitable Android build for the target ABI and place it in a writable location like:

- /data/local/tmp/qemu-system-x86_64
- /system/bin/qemu-system-x86_64
- /data/data/com.iBlast.ipcgen/files/qemu-system-x86_64

The app is already written to look for the binary in those standard locations.

## Official QEMU source download

Official upstream project:
- https://www.qemu.org/download/

For Linux x86_64 users, the recommended approach is to install from the distro package manager instead of downloading a raw binary manually.

Examples:
- Ubuntu/Debian:
  - sudo apt-get install qemu-system-x86
- Fedora:
  - sudo dnf install @virtualization
- Arch:
  - sudo pacman -S qemu

## Android / AOSP QEMU source

For Android builds, use the Android QEMU sources or AOSP external/qemu mirrors:
- https://android.googlesource.com/platform/external/qemu
- https://github.com/mirror/qemu-android

## Download links summary

- QEMU official: https://www.qemu.org/download/
- QEMU source tarball: https://download.qemu.org/
- Android QEMU mirror: https://github.com/mirror/qemu-android

## Offline app behavior

The app itself does not need internet to work after installation, as long as:

1. The QEMU binary is already present on the device.
2. The ISO or IMG file is already stored locally.
3. The app is installed without the network permission being used at runtime.

The project currently includes the app logic for:
- selecting ISO/IMG
- launching QEMU in background
- stop service
- Android 6 compatibility
- basic permission handling

## Recommended offline setup flow

1. Download or compile QEMU for your Android target.
2. Copy `qemu-system-x86_64` to the phone using adb or a local file manager.
3. Set executable permission:
   - chmod +x qemu-system-x86_64
4. Place it in one of the paths above.
5. Put your ISO or IMG file in internal or external storage.
6. Open the app and choose the local disk image.

## Notes

- There is no official single `qemu-system-x86_64` Android-ready binary from QEMU upstream for general mobile use.
- For a real Android emulator-like experience, you often need a custom build of QEMU and a compatible machine configuration.
- The repository is a project template and launcher around QEMU; it is not a built-in QEMU binary distribution.

## Best practical path

If the goal is a fully offline Android launcher that runs local ISO/IMG images, the most reliable workflow is:

- use a compiled QEMU binary for ARM/x86 Android
- store it locally on device
- do not rely on internet access at runtime
- use a local disk image file

This matches your requirement for `offline` and `no internet` operation.
