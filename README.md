# Android Host App with Flutter Integration

This project demonstrates how to integrate Flutter modules into a native Android application using
the "Add-to-App" approach, with a focus on navigation patterns and tab-based interfaces.

## Project Overview

The application showcases a native Android app with a bottom navigation bar hosting 5 tabs,
including two Flutter integrations:

1. **My Tab**: Native Android screen showing shared images
2. **Billing Tab**: Native Android screen (placeholder)
3. **Dashboard Tab**: Native Android screen with a button to launch Flutter as an Activity
4. **Items Tab**: Flutter Fragment embedded directly in the tab
5. **Featured Tab**: Native Android screen (placeholder)

## Flutter Integration Approach

This project demonstrates two main approaches to integrating Flutter within a native app:

1. **Embedded Flutter Fragment**: Directly embedding Flutter UI within a native screen (Items Tab)
2. **Flutter Activity**: Launching Flutter as a separate full-screen activity (Dashboard Tab)

The app uses Flutter Engine Groups to efficiently manage multiple Flutter instances with different
initial routes.

## Navigation Approaches

### Navbar Approach 1 - Navbar in Flutter

**Example Repository**: [Flutter Navigation Example](https://github.com/flutter/samples)

**Pros:**

- Consistent UI across the entire application
- Simplified state management within Flutter
- Single codebase for navigation logic
- Better animation transitions between tabs

**Cons:**

- May not integrate well with platform-specific navigation patterns
- Cannot mix native and Flutter screens in tabs without complex channel communication
- Potentially larger Flutter bundle size

### Navbar Approach 2 - Navbar in Android/Swift

**Example Repositories:**

- Android: [This repo]
- iOS: [iOS Flutter Integration](https://github.com/example/ios-flutter-integration)

**Pros:**

- Native performance for the navigation component
- Can mix native and Flutter screens seamlessly
- Better integration with platform navigation patterns and gestures
- More efficient memory usage with multiple Flutter engines

**Cons:**

- Inconsistent UI between native and Flutter components
- More complex communication between Flutter and native code
- Requires management of multiple Flutter engines
- More code maintenance across multiple platforms

## Getting Started

1. Clone the repository
2. Ensure you have Flutter SDK installed and configured
3. Run `flutter pub get` in the Flutter module directory
4. Open the Android project in Android Studio
5. Run the application on an emulator or physical device

## Communication Between Flutter and Android

The application demonstrates communication between Flutter and the host Android app through:

- Initial route parameters for Flutter navigation
- Shared image URIs passed between components
- Engine caching for performance optimization

For more details on navigation and back stack handling,
see [navigation_next_steps.md](navigation_next_steps.md).

## Requirements

- Android Studio Arctic Fox or newer
- Flutter SDK 2.10.0 or newer
- Android SDK 21+ (Android 5.0+)