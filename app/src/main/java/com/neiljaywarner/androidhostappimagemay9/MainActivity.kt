package com.neiljaywarner.androidhostappimagemay9

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

class MainActivity : ComponentActivity() {
    private val sharedImageUris = mutableStateListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Handle intent when app is launched via share
        handleIntent(intent)
        
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
        sharedImageUris.clear()
        
        when {
            // Handle single image being sent
            intent.action == Intent.ACTION_SEND && 
            intent.type?.startsWith("image/") == true -> {
                @Suppress("DEPRECATION")
                (intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))?.let {
                    sharedImageUris.add(it)
                }
            }
            
            // Handle multiple images being sent
            intent.action == Intent.ACTION_SEND_MULTIPLE && 
            intent.type?.startsWith("image/") == true -> {
                @Suppress("DEPRECATION")
                intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)?.let { uris ->
                    sharedImageUris.addAll(uris)
                }
            }
        }
    }
    
    private fun launchFlutterApp() {
        try {
            // This assumes you have a Flutter module set up 
            // Replace with actual Flutter activity if available
            val flutterIntent = packageManager.getLaunchIntentForPackage("com.example.flutter_module")
            if (flutterIntent != null) {
                startActivity(flutterIntent)
            } else {
                Toast.makeText(this, "Flutter app not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
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