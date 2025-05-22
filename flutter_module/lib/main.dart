import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

// Global navigator keys for nested navigation
final GlobalKey<NavigatorState> rootNavigatorKey = GlobalKey<NavigatorState>();
final GlobalKey<NavigatorState> itemsNavigatorKey = GlobalKey<NavigatorState>();

// Global method channel for communication with native code
const MethodChannel channel = MethodChannel('com.neiljaywarner.androidhostappimagemay9/appbar');

// Debug flag to help diagnose issues
const bool debugPrintEnabled = true;

void debugPrint(String message) {
  if (debugPrintEnabled) {
    print('[DEBUG] $message');
  }
}

void main() {
  // Set up back press handler
  SystemChannels.platform.setMethodCallHandler((call) async {
    if (call.method == 'SystemNavigator.pop') {
      // Handle system back button
      final handled = await handleSystemBackPress();
      if (handled) {
        // We've handled the back button ourselves
        return true;
      }
    }
    return null;
  });

  // Set up channel to receive back press events from Android
  channel.setMethodCallHandler((call) async {
    if (call.method == 'handleBackPressed') {
      print('Received handleBackPressed from Android');
      // Handle back navigation in Flutter
      final handled = await handleSystemBackPress();
      return handled;
    }
    return false;
  });

  runApp(const MyApp());
}

// Global function to handle back navigation
Future<bool> handleSystemBackPress() async {
  // First try using the items navigator if it exists and can pop
  if (itemsNavigatorKey.currentState?.canPop() == true) {
    _updateNativeAppBarTitle("Items");
    itemsNavigatorKey.currentState?.pop();
    return true;
  }

  // Then try the root navigator
  if (rootNavigatorKey.currentState?.canPop() == true) {
    rootNavigatorKey.currentState?.pop();
    return true;
  }

  return false;
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    // Print debug information to help diagnose routing issues
    print('Building MyApp with initialRoute: ${WidgetsBinding.instance.platformDispatcher
        .defaultRouteName}');

    return MaterialApp(
      title: 'Flutter Module',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),
      ),
      navigatorKey: rootNavigatorKey,
      // Use the route provided by the platform if available
      initialRoute: WidgetsBinding.instance.platformDispatcher.defaultRouteName != '/' &&
          WidgetsBinding.instance.platformDispatcher.defaultRouteName != ''
          ? WidgetsBinding.instance.platformDispatcher.defaultRouteName
          : '/',
      onGenerateRoute: (settings) {
        debugPrint('MyApp.onGenerateRoute called with settings.name: ${settings.name}');
        // Handle dynamic routes like '/item/1'
        final uri = Uri.parse(settings.name ?? '/');
        final pathSegments = uri.pathSegments;

        if (pathSegments.length == 2 && pathSegments[0] == 'item') {
          final itemId = pathSegments[1];

          // Update title when navigating to detail page
          WidgetsBinding.instance.addPostFrameCallback((_) {
            _updateNativeAppBarTitle("Item $itemId");
          });

          return MaterialPageRoute(
            builder: (context) =>
                ItemDetailScreen(
                  itemId: itemId,
                  onBack: () {
                    _updateNativeAppBarTitle("Items");
                    itemsNavigatorKey.currentState?.pop();
                  },
                ),
            // Add RouteSettings with a name to help with routing
            settings: RouteSettings(name: '/item/$itemId'),
          );
        }

        // Default routes
        return null;
      },
      routes: {
        '/': (context) => const MyHomePage(),
        '/service': (context) => const ServiceScreen(),
        '/service/screen2': (context) => const ServiceScreen2(),
        '/profile': (context) => const ProfileScreen(),
        '/help': (context) => const HelpScreen(),
      },
    );
  }
}

class MyHomePage extends StatelessWidget {
  const MyHomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Flutter Module'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text(
              'Flutter Default Route',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () {
                Navigator.pushNamed(context, '/service');
              },
              child: const Text('Go to Service Screen'),
            ),
            const SizedBox(height: 10),
            ElevatedButton(
              onPressed: () {
                Navigator.pushNamed(context, '/profile');
              },
              child: const Text('Go to Profile Screen'),
            ),
          ],
        ),
      ),
    );
  }
}

class ServiceScreen extends StatelessWidget {
  const ServiceScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Material(
      child: Column(
        children: [
          // Custom app bar to match native look
          Container(
            color: Colors.blue.shade100,
            padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 8),
            child: SafeArea(
              child: Row(
                children: [
                  IconButton(
                    icon: const Icon(Icons.arrow_back),
                    onPressed: () {
                      // Update title when tapping back button
                      _updateNativeAppBarTitle("Items");
                      Navigator.pop(context);
                    },
                  ),
                  const Text(
                    'Service',
                    style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                  ),
                ],
              ),
            ),
          ),
          Expanded(
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Text(
                    'Service Screen',
                    style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 20),
                  const Text(
                    'Current route: /service',
                    style: TextStyle(fontSize: 16),
                  ),
                  const SizedBox(height: 20),
                  ElevatedButton(
                    onPressed: () {
                      Navigator.pushNamed(context, '/service/screen2');
                    },
                    child: const Text('Navigate to Service Screen 2'),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class ServiceScreen2 extends StatelessWidget {
  const ServiceScreen2({super.key});

  @override
  Widget build(BuildContext context) {
    return Material(
      child: Column(
        children: [
          // Custom app bar to match native look
          Container(
            color: Colors.blue.shade100,
            padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 8),
            child: SafeArea(
              child: Row(
                children: [
                  IconButton(
                    icon: const Icon(Icons.arrow_back),
                    onPressed: () {
                      // Update title when tapping back button
                      _updateNativeAppBarTitle("Service");
                      Navigator.pop(context);
                    },
                  ),
                  const Text(
                    'Service Screen 2',
                    style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                  ),
                ],
              ),
            ),
          ),
          Expanded(
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Text(
                    'Service Screen 2',
                    style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 20),
                  const Text(
                    'Current route: /service/screen2',
                    style: TextStyle(fontSize: 16),
                  ),
                  const SizedBox(height: 20),
                  const Text(
                    'Press back to return to Service Screen',
                    style: TextStyle(fontSize: 16, color: Colors.grey),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // Set the title to "Items" when this screen is shown
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _updateNativeAppBarTitle("Items");
      debugPrint("Profile screen initialized, setting title to Items");
    });

    // Generate the items data
    final items = List.generate(
      20,
          (index) =>
      {
        'id': '${index + 1}',
        'name': 'Item ${index + 1}',
        'description': 'Description for item ${index + 1}',
        'price': '\$${(index + 1) * 10}.99',
      },
    );

    // Use a simpler approach - directly show the items list
    return Material(
      color: Colors.white,
      child: ListView.builder(
        itemCount: items.length,
        itemBuilder: (context, index) {
          final item = items[index];
          return ListTile(
            leading: CircleAvatar(
              backgroundColor: Colors.blue.shade100,
              child: Text(item['id']?.toString() ?? ''),
            ),
            title: Text(item['name']?.toString() ?? ''),
            subtitle: Text('${item['price']?.toString() ?? ''} - Tap for details'),
            onTap: () {
              debugPrint("Tapped on item ${item['id']}");
              _updateNativeAppBarTitle("Item ${item['id']}");
              Navigator.of(context).push(
                MaterialPageRoute(
                  builder: (context) =>
                      ItemDetailScreen(
                        itemId: item['id']?.toString() ?? '',
                        onBack: () {
                          _updateNativeAppBarTitle("Items");
                          Navigator.of(context).pop();
                        },
                      ),
                ),
              );
            },
          );
        },
      ),
    );
  }
}

class ItemsListScreen extends StatelessWidget {
  const ItemsListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // Example item data
    final items = List.generate(
      20,
          (index) =>
      {
        'id': '${index + 1}',
        'name': 'Item ${index + 1}',
        'description': 'Description for item ${index + 1}',
        'price': '\$${(index + 1) * 10}.99',
      },
    );

    // Set up its own navigator
    return Navigator(
      key: itemsNavigatorKey,
      onGenerateRoute: (settings) {
        if (settings.name == '/') {
          return MaterialPageRoute(
            builder: (context) => _ItemsList(items: items),
            settings: settings,
          );
        }

        final uri = Uri.parse(settings.name ?? '/');
        final pathSegments = uri.pathSegments;

        if (pathSegments.length == 2 && pathSegments[0] == 'item') {
          final itemId = pathSegments[1];
          return MaterialPageRoute(
            builder: (context) =>
                ItemDetailScreen(
                  itemId: itemId,
                  onBack: () {
                    _updateNativeAppBarTitle("Items");
                    itemsNavigatorKey.currentState?.pop();
                  },
                ),
          );
        }

        return null;
      },
      initialRoute: '/',
    );
  }
}

// Extracted widget for the items list to avoid navigator nesting issues
class _ItemsList extends StatelessWidget {
  final List<Map<String, dynamic>> items;

  const _ItemsList({required this.items});

  @override
  Widget build(BuildContext context) {
    // Set the title to "Items" when this screen is shown
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _updateNativeAppBarTitle("Items");
    });

    return Material(
      color: Colors.white,
      child: ListView.builder(
        itemCount: items.length,
        itemBuilder: (context, index) {
          final item = items[index];
          return ListTile(
            leading: CircleAvatar(
              backgroundColor: Colors.blue.shade100,
              child: Text(item['id']?.toString() ?? ''),
            ),
            title: Text(item['name']?.toString() ?? ''),
            subtitle: Text('${item['price']?.toString() ?? ''} - Tap for details'),
            onTap: () {
              _updateNativeAppBarTitle("Item ${item['id']}");
              Navigator.pushNamed(
                context,
                '/item/${item['id']}',
              );
            },
          );
        },
      ),
    );
  }
}

class ItemDetailScreen extends StatelessWidget {
  final String itemId;
  final VoidCallback? onBack;

  const ItemDetailScreen({
    super.key,
    required this.itemId,
    this.onBack,
  });

  @override
  Widget build(BuildContext context) {
    // Simulating item data based on ID
    final item = {
      'id': itemId,
      'name': 'Item $itemId',
      'description': 'This is a detailed description for item $itemId. It contains all the important information about this specific item that a user might want to know.',
      'price': '\$${(int.tryParse(itemId) ?? 0) * 10}.99',
      'rating': '${((int.tryParse(itemId) ?? 0) % 5) + 1}/5',
      'inStock': (int.tryParse(itemId) ?? 0) % 3 != 0,
    };

    return Material(
      color: Colors.white,
      child: PopScope(
        // Prevent system from handling the back button - we'll handle it ourselves
        canPop: false,
        onPopInvoked: (didPop) {
          if (didPop) {
            // This is actually unreachable with canPop: false
            _updateNativeAppBarTitle("Items");
          }
        },
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Top section with back button
              Row(
                children: [
                  IconButton(
                    icon: const Icon(Icons.arrow_back),
                    onPressed: () {
                      // Use the callback for back navigation
                      if (onBack != null) {
                        onBack!();
                      } else {
                        _updateNativeAppBarTitle("Items");
                        Navigator.pop(context);
                      }
                    },
                  ),
                ],
              ),
              const SizedBox(height: 16),
              // Item image placeholder
              Container(
                height: 200,
                width: double.infinity,
                color: Colors.blue.shade100,
                child: Center(
                  child: Icon(
                    Icons.image,
                    size: 80,
                    color: Colors.blue.shade800,
                  ),
                ),
              ),
              const SizedBox(height: 16),
              // Item details
              Text(
                item['name']?.toString() ?? '',
                style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 8),
              Text(
                'Price: ${item['price']?.toString() ?? ''}',
                style: const TextStyle(fontSize: 20, color: Colors.green),
              ),
              const SizedBox(height: 8),
              Text('Rating: ${item['rating']?.toString() ?? ''}'),
              const SizedBox(height: 8),
              Text(
                'Availability: ${(item['inStock'] as bool?) == true ? 'In Stock' : 'Out of Stock'}',
                style: TextStyle(
                  color: (item['inStock'] as bool?) == true ? Colors.green : Colors.red,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 16),
              const Text(
                'Description:',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 8),
              Text(item['description']?.toString() ?? ''),
              const SizedBox(height: 24),
              // Route information for demonstration
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: Colors.grey.shade200,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Text(
                  'Current route: /item/$itemId',
                  style: const TextStyle(fontStyle: FontStyle.italic),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class HelpScreen extends StatelessWidget {
  const HelpScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // Set the title to "Help" when this screen is shown
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _updateNativeAppBarTitle("Help");
    });

    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Back button
              Row(
                children: [
                  IconButton(
                    icon: const Icon(Icons.arrow_back),
                    onPressed: () {
                      _updateNativeAppBarTitle("Items");
                      Navigator.pop(context);
                    },
                  ),
                  const Text(
                    "Help & FAQ",
                    style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                  ),
                ],
              ),
              const SizedBox(height: 24),
              // Help content
              Expanded(
                child: SingleChildScrollView(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      _buildFaqItem(
                        "How do I view item details?",
                        "Tap on any item in the list to see its detailed information including price, availability, and description.",
                      ),
                      _buildFaqItem(
                        "Can I navigate between screens?",
                        "Yes! Use the bottom navigation bar to switch between different sections of the app.",
                      ),
                      _buildFaqItem(
                        "What does the Items tab show?",
                        "The Items tab displays a list of all available items that you can browse and view details for.",
                      ),
                      _buildFaqItem(
                        "How do I go back to the previous screen?",
                        "Use the back button in the top-left corner or your device's back button to return to the previous screen.",
                      ),
                      _buildFaqItem(
                        "How does this app work?",
                        "This app demonstrates Flutter-Android integration using Flutter modules embedded in a native Android application.",
                      ),
                      const SizedBox(height: 20),
                      Container(
                        padding: const EdgeInsets.all(16),
                        decoration: BoxDecoration(
                          color: Colors.blue.shade50,
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text(
                              "About This App",
                              style: TextStyle(
                                fontSize: 18,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            const SizedBox(height: 8),
                            const Text(
                              "This application demonstrates how to integrate Flutter modules within a native Android application. "
                                  "It showcases multiple Flutter engines working together, deep linking, and communication between Flutter and native code.",
                              style: TextStyle(fontSize: 14),
                            ),
                            const SizedBox(height: 12),
                            const Text(
                              "Version: 1.0.0",
                              style: TextStyle(
                                fontSize: 12,
                                fontStyle: FontStyle.italic,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildFaqItem(String question, String answer) {
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey.shade300),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            question,
            style: const TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            answer,
            style: TextStyle(
              fontSize: 14,
              color: Colors.grey.shade700,
            ),
          ),
        ],
      ),
    );
  }
}

// Function to communicate with native code to update the app bar title
void _updateNativeAppBarTitle(String title) {
  try {
    debugPrint("Updating native app bar title to: $title");
    channel.invokeMethod('updateTitle', {'title': title});
  } catch (e) {
    print('Error updating native app bar title: $e');
  }
}