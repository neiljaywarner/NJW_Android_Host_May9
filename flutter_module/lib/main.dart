import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(const MyApp());

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
      // Use the route provided by the platform if available
      initialRoute: WidgetsBinding.instance.platformDispatcher.defaultRouteName != '/' &&
          WidgetsBinding.instance.platformDispatcher.defaultRouteName != ''
          ? WidgetsBinding.instance.platformDispatcher.defaultRouteName
          : '/',
      onGenerateRoute: (settings) {
        print('MyApp.onGenerateRoute called with settings.name: ${settings.name}');
        // Handle dynamic routes like '/item/1'
        final uri = Uri.parse(settings.name ?? '/');
        final pathSegments = uri.pathSegments;

        if (pathSegments.length == 2 && pathSegments[0] == 'item') {
          final itemId = pathSegments[1];

          return MaterialPageRoute(
            builder: (context) => ItemDetailScreen(itemId: itemId),
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
        '/items': (context) => const ItemsListScreen(),
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
                    onPressed: () => Navigator.pop(context),
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
                    onPressed: () => Navigator.pop(context),
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
    // Don't use Scaffold here since we're using a native app bar
    return const ItemsListScreen();
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

    return Material(
      color: Colors.white,
      child: PopScope(
        // Handle back button press to update title when navigating back
        canPop: true,
        onPopInvoked: (didPop) {
          if (didPop) {
            // Title update logic would be in native code now
          }
        },
        child: ListView.builder(
          itemCount: items.length,
          itemBuilder: (context, index) {
            final item = items[index];
            return ListTile(
              leading: CircleAvatar(
                backgroundColor: Colors.blue.shade100,
                child: Text(item['id'] ?? ''),
              ),
              title: Text(item['name']?.toString() ?? ''),
              subtitle: Text('${item['price']} - Tap for details'),
              onTap: () {
                Navigator.pushNamed(
                  context,
                  '/item/${item['id']}',
                );
              },
            );
          },
        ),
      ),
    );
  }
}

class ItemDetailScreen extends StatelessWidget {
  final String itemId;

  const ItemDetailScreen({
    super.key,
    required this.itemId,
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
                    // Title update logic would be in native code now
                    Navigator.pop(context);
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
    );
  }
}