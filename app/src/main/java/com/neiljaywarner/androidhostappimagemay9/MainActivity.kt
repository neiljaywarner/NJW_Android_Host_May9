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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        imageUris = sharedImageUris,
                        onLaunchFlutter = { launchFlutterApp() }
                    )
                }
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

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    imageUris: List<Uri>,
    onLaunchFlutter: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Instructions
            Text(
                text = "Please launch Google Photos manually",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            // Flutter button
            Button(
                onClick = onLaunchFlutter,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Launch Flutter App")
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Show shared image paths
            if (imageUris.isEmpty()) {
                Text(
                    text = "Please share photo(s)",
                    style = MaterialTheme.typography.headlineMedium
                )
            } else {
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
}