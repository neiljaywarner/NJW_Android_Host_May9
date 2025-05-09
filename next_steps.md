# Next Steps: Setting Up the Flutter Module

To progress to milestone #2 (getting the Flutter module button working), follow these steps:

## 1. Create a Flutter Module

Create a new Flutter module in your project directory:

```bash
cd /Users/neil/AndroidStudioProjects/AndroidHostAppImageMay9
flutter create --template=module flutter_module
```

## 2. Integrate Flutter Module with Android

Follow the Flutter documentation to add the module to your Android app:
https://docs.flutter.dev/add-to-app/android/project-setup

You'll need to update:

- Your app's settings.gradle.kts file to include the Flutter module
- Your app's build.gradle.kts file to add Flutter dependencies

## 3. Update the Launch Flutter Method

Modify MainActivity.kt to use FlutterActivity:

```kotlin
// Add this import
import io.flutter.embedding.android.FlutterActivity

// Update the launchFlutterApp method
private fun launchFlutterApp() {
    try {
        val intent = FlutterActivity
            .withNewEngine()
            .initialRoute("/")
            .build(this)
            
        // Pass image URIs to Flutter if needed
        if (sharedImageUris.isNotEmpty()) {
            val uriStrings = sharedImageUris.map { it.toString() }.toTypedArray()
            intent.putExtra("IMAGE_URIS", uriStrings)
        }
        
        startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(this, "Could not launch Flutter app: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
```

## 4. Create a Basic Flutter UI

Update the flutter_module/lib/main.dart file with a simple UI:

```dart
import 'package:flutter/material.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Module',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatelessWidget {
  const MyHomePage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Flutter Module'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: const <Widget>[
            Text(
              'Flutter module is running!',
              style: TextStyle(fontSize: 20),
            ),
          ],
        ),
      ),
    );
  }
}
```

## 5. Test Your Implementation

Build and run your app, then test the "Launch Flutter App" button to verify it correctly launches
the Flutter module.

## Next Milestone

After completing milestone #2, we'll move on to milestone #3: configuring the Flutter module to
receive and display the file paths from the Android host app.