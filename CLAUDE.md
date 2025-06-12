# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the app
./gradlew assembleDebug

# Build release version
./gradlew assembleRelease

# Clean build
./gradlew clean

# Run lint checks
./gradlew lint

# Install on connected device
./gradlew installDebug
```

## Architecture Overview

This Android app connects to Bluebird RFID SLED devices for tag scanning:

**Core Components:**
- `MainActivity.java` - UI layer handling permissions and user interactions
- `RFIDManager.java` - Business logic layer wrapping RFID SDK operations
- `RFIDConnectionListener` interface - Event callback pattern for RFID events

**Key Design Patterns:**
- Callback interfaces for asynchronous RFID operations
- Handler-based UI updates from background threads
- Singleton-like manager pattern for RFID operations

## SDK Integration

The app is designed for Bluebird RFID SDK integration:
- SDK JARs should be placed in `/app/libs/` directory
- Current implementation uses mock functions for testing
- Real SDK methods need to replace mock implementations in `RFIDManager.java`

## Important Configuration

**Android Permissions:**
- Bluetooth permissions (including Android 12+ specific ones)
- Location permissions required for Bluetooth scanning
- All permissions handled in `MainActivity.checkPermissions()`

**Build Configuration:**
- Min SDK: 21 (Android 5.0)
- Target SDK: 34
- Compile SDK: 34
- Java 8 compatibility

## Development Notes

When modifying RFID functionality:
1. Check `RFIDManager.java` for SDK integration points marked with comments
2. Maintain thread safety for UI updates using the existing Handler pattern
3. Follow the established callback pattern for new RFID operations

The app uses Material Design 3 components and follows Android best practices for permissions handling.