# Build Signed AAB for MiLupa v2.2 (32)

## ✅ Version Updated
- **versionCode**: 32
- **versionName**: 2.2

## Prerequisites

1. **Keystore File**: `milupa.jks` should be in the project root
2. **Keystore Properties**: Create `keystore.properties` file with your credentials

## Setup Keystore Properties

1. Copy the example file:
   ```bash
   cp keystore.properties.example keystore.properties
   ```

2. Edit `keystore.properties` and fill in your credentials:
   ```properties
   storeFile=milupa.jks
   storePassword=YOUR_KEYSTORE_PASSWORD
   keyAlias=YOUR_KEY_ALIAS
   keyPassword=YOUR_KEY_PASSWORD
   ```

## Build the AAB

### Option 1: Using the build script
```bash
./build_aab.sh
```

### Option 2: Using Gradle directly
```bash
./gradlew bundleRelease
```

## Output Location

The signed AAB will be created at:
```
app/build/outputs/bundle/release/app-release.aab
```

## Verify the Build

After building, verify the version:
```bash
aapt dump badging app/build/outputs/bundle/release/app-release.aab | grep -E "versionCode|versionName"
```

You should see:
- versionCode='32'
- versionName='2.2'

## Notes

- The keystore file (`milupa.jks`) and `keystore.properties` are in `.gitignore` and won't be committed to Git
- Make sure you have the correct keystore passwords from when you originally created the keystore
- If you don't remember the passwords, you'll need to create a new keystore (but this will require updating the app in Play Store with a new signing key)

