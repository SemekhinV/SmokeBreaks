#!/bin/bash

# Smoke Break Buddy - App Runner Script
# This script helps build and run the app with proper error handling

echo "🚬 Smoke Break Buddy - Starting Build Process..."

# Stop any running Gradle daemons
echo "Stopping Gradle daemons..."
./gradlew --stop

# Clean the project
echo "Cleaning project..."
./gradlew clean

# Build the debug version
echo "Building debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "📱 You can now install and run the app using:"
    echo "   - Open the project in Android Studio"
    echo "   - Connect your device or start an emulator"
    echo "   - Click the 'Run' button or use Shift+F10"
    echo ""
    echo "   Or install manually:"
    echo "   adb install app/build/outputs/apk/debug/app-debug.apk"
else
    echo "❌ Build failed. Please check the error messages above."
    exit 1
fi
