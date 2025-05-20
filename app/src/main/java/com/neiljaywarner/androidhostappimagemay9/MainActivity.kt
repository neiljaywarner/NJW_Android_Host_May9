package com.neiljaywarner.androidhostappimagemay9

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neiljaywarner.androidhostappimagemay9.ui.theme.AndroidHostAppImageMay9Theme
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterActivityLaunchConfigs

class MainActivity : ComponentActivity() {
    private val sharedImageUris = mutableStateListOf<Uri>()

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        enableEdgeToEdge()
        
        // Handle intent when app is launched via share
        handleIntent(intent)

        Log.d(TAG, "Setting up UI with ${sharedImageUris.size} image URIs")
        setContent {
            AndroidHostAppImageMay9Theme {
                MainApp(
                    imageUris = sharedImageUris,
                    onLaunchFlutter = { launchFlutterApp() }
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
            Log.d(TAG, "Attempting to launch Flutter module")
            // Launch the real Flutter activity
            val intent = FlutterActivity
                .withCachedEngine(MyApplication.ENGINE_ID)
                .backgroundMode(FlutterActivityLaunchConfigs.BackgroundMode.transparent)
                .build(this)
            
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
}

data class TabItem(
    val title: String,
    val icon: ImageVector,
    val screen: @Composable () -> Unit
)

@Composable
fun MainApp(
    imageUris: List<Uri>,
    onLaunchFlutter: () -> Unit
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val tabs = listOf(
        TabItem(
            title = "Home",
            icon = Icons.Default.Home,
            screen = { HomeScreen(imageUris) }
        ),
        TabItem(
            title = "Favorites",
            icon = Icons.Default.Favorite,
            screen = { FavoritesScreen() }
        ),
        TabItem(
            title = "Flutter",
            icon = Icons.Default.Create,
            screen = { FlutterTabScreen(onLaunchFlutter, imageUris) }
        ),
        TabItem(
            title = "Profile",
            icon = Icons.Default.Person,
            screen = { ProfileScreen() }
        ),
        TabItem(
            title = "Settings",
            icon = Icons.Default.Settings,
            screen = { SettingsScreen() }
        )
    )

    Scaffold(
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

@Composable
fun HomeScreen(imageUris: List<Uri>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Home Screen",
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
            text = "Favorites Screen",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun FlutterTabScreen(onLaunchFlutter: () -> Unit, imageUris: List<Uri>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Flutter Module",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = onLaunchFlutter,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Launch Flutter App")
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
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Profile Screen",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Settings Screen",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}