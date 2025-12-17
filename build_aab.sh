#!/bin/bash
# Script to build signed AAB for MiLupa

echo "Building signed AAB for MiLupa version 2.2 (32)..."

# Check if keystore.properties exists
if [ ! -f "keystore.properties" ]; then
    echo "ERROR: keystore.properties not found!"
    echo "Please create keystore.properties with your keystore credentials."
    echo "You can use keystore.properties.example as a template."
    exit 1
fi

# Check if keystore file exists
if [ ! -f "milupa.jks" ]; then
    echo "ERROR: milupa.jks not found!"
    echo "Please copy milupa.jks to the project root."
    exit 1
fi

# Build the AAB
echo "Building release AAB..."
./gradlew bundleRelease

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ AAB built successfully!"
    echo "Location: app/build/outputs/bundle/release/app-release.aab"
    echo ""
    ls -lh app/build/outputs/bundle/release/app-release.aab 2>/dev/null || echo "AAB file not found in expected location"
else
    echo ""
    echo "✗ Build failed. Check the error messages above."
    exit 1
fi

