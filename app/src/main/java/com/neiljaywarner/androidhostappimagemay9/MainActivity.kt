package com.neiljaywarner.androidhostappimagemay9

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import com.neiljaywarner.androidhostappimagemay9.ui.theme.AndroidHostAppImageMay9Theme
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterActivityLaunchConfigs
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.plugin.common.MethodChannel

class MainActivity : FragmentActivity() {
    private val sharedImageUris = mutableStateListOf<Uri>()
    var currentTitle =
        mutableStateOf("Items") // Default to "Items" for the Items tab - public for access
    private var currentTabIndex = mutableIntStateOf(0)

    companion object {
        const val TAG = "MainActivity" // Make public for logging access
        private const val CHANNEL = "com.neiljaywarner.androidhostappimagemay9/appbar"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        enableEdgeToEdge()

        // Handle intent when app is launched via share
        handleIntent(intent)

        // Add back pressed handling
        addBackPressedCallback()

        Log.d(TAG, "Setting up UI with ${sharedImageUris.size} image URIs")
        setContent {
            AndroidHostAppImageMay9Theme {
                MainApp(
                    imageUris = sharedImageUris,
                    onLaunchFlutter = { launchFlutterApp() },
                    currentTitle = currentTitle
                )
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    
    private fun handleIntent(intent: Intent) {
        Log.d(TAG, "handleIntent called with action: ${intent.action}, type: ${intent.type}")
        sharedImageUris.clear()
        
        when {
            // Handle single image being sent
            intent.action == Intent.ACTION_SEND && 
            intent.type?.startsWith("image/") == true -> {
                Log.d(TAG, "Handling single image share")
                @Suppress("DEPRECATION")
                (intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))?.let {
                    Log.d(TAG, "Adding shared image URI: $it")
                    sharedImageUris.add(it)
                }
            }
            
            // Handle multiple images being sent
            intent.action == Intent.ACTION_SEND_MULTIPLE && 
            intent.type?.startsWith("image/") == true -> {
                Log.d(TAG, "Handling multiple image share")
                @Suppress("DEPRECATION")
                intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)?.let { uris ->
                    Log.d(TAG, "Adding ${uris.size} shared image URIs")
                    sharedImageUris.addAll(uris)
                }
            }
        }
        Log.d(TAG, "Total shared image URIs: ${sharedImageUris.size}")
    }
    
    private fun launchFlutterApp() {
        try {
            Log.d(TAG, "Attempting to launch Flutter module with service route")
            setupMethodChannel()

            // Launch the real Flutter activity
            val intent = FlutterActivity
                .withCachedEngine(MyApplication.SERVICE_ENGINE_ID)  // Use the service engine with pre-configured route
                .backgroundMode(FlutterActivityLaunchConfigs.BackgroundMode.transparent)
                .build(this)

            // Add initialRoute as an intent extra to ensure it's passed to Flutter
            intent.putExtra("route", "/service")

            // Pass image URIs as extras if needed
            if (sharedImageUris.isNotEmpty()) {
                Log.d(TAG, "Passing ${sharedImageUris.size} image URIs to Flutter")
                val uriStrings = sharedImageUris.map { it.toString() }.toTypedArray()
                intent.putExtra("IMAGE_URIS", uriStrings)
            }

            Log.d(TAG, "Starting Flutter activity")
            startActivity(intent)
            Log.d(TAG, "Flutter activity started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error launching Flutter app: ${e.message}", e)
            Toast.makeText(this, "Could not launch Flutter app: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupMethodChannel() {
        // Set up method channel for getting updates from Flutter
        val engines = listOf(
            MyApplication.ENGINE_ID,
            MyApplication.PROFILE_ENGINE_ID,
            MyApplication.SERVICE_ENGINE_ID
        ).mapNotNull { FlutterEngineCache.getInstance().get(it) }

        // For each engine, set up a method channel handler
        engines.forEach { engine ->
            MethodChannel(
                engine.dartExecutor.binaryMessenger,
                CHANNEL
            ).setMethodCallHandler { call, result ->
                when (call.method) {
                    "updateTitle" -> {
                        val title = call.argument<String>("title")
                        if (title != null) {
                            Log.d(TAG, "Updating title from Flutter to: $title")
                            currentTitle.value = title
                            result.success(true)
                        } else {
                            result.error("INVALID_ARGUMENT", "Title cannot be null", null)
                        }
                    }
                    else -> result.notImplemented()
                }
            }
        }
    }

    private fun addBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "Hardware back button pressed, current title: ${currentTitle.value}")

                // If we're on the Items tab and the title is an Item detail page
                if (currentTitle.value.startsWith("Item ") &&
                    !currentTitle.value.equals("Items") &&
                    currentTitle.value.contains("Item")
                ) {

                    Log.d(TAG, "Back pressed on item details, trying Flutter back navigation")

                    // Try to let the Flutter engine handle the back press
                    val engine =
                        FlutterEngineCache.getInstance().get(MyApplication.PROFILE_ENGINE_ID)
                    if (engine != null) {
                        // Send a message to Flutter to handle the back navigation
                        MethodChannel(engine.dartExecutor.binaryMessenger, CHANNEL)
                            .invokeMethod("handleBackPressed", null)

                        // Set title back to Items
                        currentTitle.value = "Items"
                        return
                    }
                }

                // In all other cases, let the system handle the back press
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        })
    }
}

data class TabItem(
    val title: String,
    val icon: ImageVector,
    val screen: @Composable () -> Unit
)

@Composable
fun MainApp(
    imageUris: List<Uri>,
    onLaunchFlutter: () -> Unit,
    currentTitle: androidx.compose.runtime.State<String>
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    // No need to track tab index globally - just use the local selectedTabIndex
    // Update the current tab index (for fragment reference)

    // Reset the currentTitle when tab changes (except Items tab which gets its title from Flutter)
    LaunchedEffect(selectedTabIndex) {
        Log.d(MainActivity.TAG, "Tab changed to ${selectedTabIndex}, updating title")
        if (selectedTabIndex != 3) { // If not on the Items tab
            when (selectedTabIndex) {
                0 -> (currentTitle as androidx.compose.runtime.MutableState<String>).value =
                    "My Demo"

                1 -> (currentTitle as androidx.compose.runtime.MutableState<String>).value =
                    "Billing"

                2 -> (currentTitle as androidx.compose.runtime.MutableState<String>).value =
                    "Dashboard"

                4 -> (currentTitle as androidx.compose.runtime.MutableState<String>).value =
                    "Featured"
            }
        } else {
            // When first navigating to Items tab, set the title to "Items"
            (currentTitle as androidx.compose.runtime.MutableState<String>).value = "Items"
        }
    }

    val tabs = listOf(
        TabItem(
            title = "My Demo",
            icon = Icons.Default.AccountCircle,
            screen = { MyTabScreen(imageUris) }
        ),
        TabItem(
            title = "Billing",
            icon = Icons.Default.ShoppingCart,
            screen = { BillingTabScreen() }
        ),
        TabItem(
            title = "Dashboard",
            icon = Icons.Default.Settings,
            screen = { DashboardTabScreen(onLaunchFlutter, imageUris) }
        ),
        TabItem(
            title = "Items",
            icon = Icons.Default.List,
            screen = { ItemsFlutterScreen() }
        ),
        TabItem(
            title = "Featured",
            icon = Icons.Default.Star,
            screen = { FavoritesScreen() }
        )
    )

    Scaffold(
        topBar = {
            NativeAppBar(title = if (selectedTabIndex == 3) currentTitle.value else tabs[selectedTabIndex].title)
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            tabs[selectedTabIndex].screen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NativeAppBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun MyTabScreen(imageUris: List<Uri>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "My Demo",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Instructions
        Text(
            text = "Please launch Google Photos manually to share images",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Show shared image paths
        if (imageUris.isEmpty()) {
            Text(
                text = "No shared photos yet",
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            Text(
                text = "Shared Images (${imageUris.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn {
                items(imageUris) { uri ->
                    Text(
                        text = "File path: $uri",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Featured Content",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun BillingTabScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Billing Screen",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun DashboardTabScreen(onLaunchFlutter: () -> Unit, imageUris: List<Uri>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Dashboard Screen",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "The below button launches the Flutter module with the initialRoute /service",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onLaunchFlutter,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Request Service (in Flutter)")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Show shared image paths
        if (imageUris.isNotEmpty()) {
            Text(
                text = "Shared Images (${imageUris.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn {
                items(imageUris) { uri ->
                    Text(
                        text = "File path: $uri",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ItemsFlutterScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Ensure the title is set to "Items" when this screen is shown
    DisposableEffect(Unit) {
        // Update title when the screen is shown
        (context as? MainActivity)?.let {
            (it.currentTitle as androidx.compose.runtime.MutableState<String>).value = "Items"
        }
        onDispose { }
    }

    // Titles are now managed through the parent activity
    // Override title setting not needed anymore since we're setting it in the LaunchedEffect

    // Create a mutable state to track the Flutter fragment
    var flutterFragment by remember { mutableStateOf<FlutterFragment?>(null) }

    // Create and use the Flutter fragment
    LaunchedEffect(Unit) {
        try {
            flutterFragment = FlutterFragment
                .withCachedEngine(MyApplication.PROFILE_ENGINE_ID)
                .build()
        } catch (e: Exception) {
            Log.e("ItemsFlutterScreen", "Error creating Flutter fragment", e)
        }
    }

    // The actual Flutter fragment container
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            // Create a FrameLayout to host the Flutter fragment
            FrameLayout(ctx).apply {
                id = View.generateViewId()
            }
        },
        update = { view ->
            // Add the Flutter fragment to the container if it hasn't been added yet
            try {
                flutterFragment?.let { fragment ->
                    (context as? FragmentActivity)?.let { activity ->
                        if (!fragment.isAdded) {
                            activity.supportFragmentManager
                                .beginTransaction()
                                .replace(view.id, fragment)
                                .commit()
                        }
                    } ?: run {
                        Log.e(
                            "ItemsFlutterScreen",
                            "No valid FragmentActivity found to host Flutter fragment"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ItemsFlutterScreen", "Error adding Flutter fragment", e)
            }
        }
    )

    // Clean up when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            // Nothing to clean up here as fragments are managed by the activity
        }
    }
}