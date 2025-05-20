# Navigation Next Steps

This document outlines advanced navigation considerations when integrating Flutter modules into
native Android applications, particularly focusing on back stack management, state restoration, and
cross-platform communication.

## Back Stack Management

### Challenge

When integrating Flutter into a native Android application, managing the back stack becomes more
complex since you essentially have two separate navigation systems:

1. **Android's Fragment/Activity Back Stack**: Managed by the Android platform
2. **Flutter's Navigation Stack**: Managed by Flutter's Navigator

### Solutions

#### For Flutter Fragments

When using Flutter embedded as a fragment (as in the Profile tab):

1. **Override onBackPressed**:
   ```kotlin
   override fun onBackPressed() {
       // Check if Flutter fragment can handle back press
       val flutterFragment = supportFragmentManager
           .findFragmentByTag("flutter_fragment") as? FlutterFragment
           
       if (flutterFragment != null && flutterFragment.onBackPressed()) {
           // Flutter handled the back press
           return
       }
       
       // Let Android handle the back press
       super.onBackPressed()
   }
   ```

2. **Use Method Channels to communicate navigation state**:
   ```kotlin
   // In MainActivity
   private val channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "app.channel/navigation")
   
   channel.setMethodCallHandler { call, result ->
       if (call.method == "canPopRoute") {
           result.success(true)
       } else if (call.method == "popRoute") {
           onBackPressed()
           result.success(true)
       } else {
           result.notImplemented()
       }
   }
   ```

#### For Flutter Activities

When launching Flutter as a separate activity:

1. **Use ActivityResultLauncher** for communication:
   ```kotlin
   private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
       if (result.resultCode == Activity.RESULT_OK) {
           val data = result.data
           // Handle returned data
           val returnedValue = data?.getStringExtra("return_value")
       }
   }
   
   private fun launchFlutterWithResult() {
       val intent = FlutterActivity
           .withCachedEngine(MyApplication.SERVICE_ENGINE_ID)
           .build(this)
       startForResult.launch(intent)
   }
   ```

2. **Set result from Flutter**:
   ```dart
   // In Flutter
   MethodChannel('app.channel/navigation').setMethodCallHandler((call) async {
     if (call.method == 'finishWithResult') {
       final Map<String, dynamic> arguments = call.arguments;
       final result = arguments['result'];
       
       // Send result back to Android
       ServicesBinding.instance.defaultBinaryMessenger.handlePlatformMessage(
         'app.channel/navigation',
         const StandardMethodCodec().encodeSuccessEnvelope({
           'action': 'setResult',
           'data': result,
         }),
         (_) {},
       );
     }
   });
   ```

## State Restoration

Flutter provides mechanisms to restore state when an application is recreated:

1. **RestorationScope in Flutter**:
   ```dart
   class MyWidget extends StatefulWidget {
     @override
     _MyWidgetState createState() => _MyWidgetState();
   }
   
   class _MyWidgetState extends State<MyWidget> with RestorationMixin {
     final RestorableInt _counter = RestorableInt(0);
     
     @override
     String get restorationId => 'my_widget';
     
     @override
     void restoreState(RestorationBucket? oldBucket, bool initialRestore) {
       registerForRestoration(_counter, 'counter');
     }
     
     @override
     Widget build(BuildContext context) {
       // Use _counter.value
     }
   }
   ```

2. **Save Android state for Flutter**:
   ```kotlin
   override fun onSaveInstanceState(outState: Bundle) {
       super.onSaveInstanceState(outState)
       outState.putBoolean("flutter_fragment_present", true)
   }
   
   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       val flutterFragmentPresent = savedInstanceState?.getBoolean("flutter_fragment_present") ?: false
       // Restore fragment if needed
   }
   ```

## Flutter-to-Android Communication

### Communicating the Current Route

To display the current Flutter route in the Android app:

1. **Setup a Method Channel**:
   ```kotlin
   // In MainActivity
   private val channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "app.channel/navigation")
   
   // Listen for route changes
   channel.setMethodCallHandler { call, result ->
       if (call.method == "routeChanged") {
           val route = call.arguments as String
           updateRouteDisplay(route)
           result.success(null)
       } else {
           result.notImplemented()
       }
   }
   
   private fun updateRouteDisplay(route: String) {
       // Update UI to show current Flutter route
       routeTextView.text = "Current Flutter route: $route"
   }
   ```

2. **Send route from Flutter**:
   ```dart
   class MyApp extends StatefulWidget {
     @override
     _MyAppState createState() => _MyAppState();
   }
   
   class _MyAppState extends State<MyApp> {
     final _navigatorKey = GlobalKey<NavigatorState>();
     final MethodChannel _channel = MethodChannel('app.channel/navigation');
     
     @override
     Widget build(BuildContext context) {
       return MaterialApp(
         navigatorKey: _navigatorKey,
         navigatorObservers: [
           _RouteObserver(_channel),
         ],
         // ...
       );
     }
   }
   
   class _RouteObserver extends NavigatorObserver {
     final MethodChannel _channel;
     
     _RouteObserver(this._channel);
     
     @override
     void didPush(Route<dynamic> route, Route<dynamic>? previousRoute) {
       _channel.invokeMethod('routeChanged', route.settings.name ?? '/unknown');
     }
     
     @override
     void didPop(Route<dynamic> route, Route<dynamic>? previousRoute) {
       _channel.invokeMethod(
         'routeChanged', 
         previousRoute?.settings.name ?? '/unknown'
       );
     }
   }
   ```

## Next Steps for This Demo

To fully implement the navigation patterns described above:

1. Implement Method Channel communication between Flutter and Android:
    - Create a shared communication service
    - Set up route observers in Flutter
    - Handle back press navigation

2. Extend the Flutter fragments with proper state restoration:
    - Add support for configuration changes
    - Implement instance state saving

3. Add support for deep linking:
    - Handle incoming intents to navigate to specific Flutter routes
    - Support sharing data between native and Flutter components

4. Improve visual transitions:
    - Create smooth animations between native and Flutter screens
    - Ensure consistent UI patterns across both platforms