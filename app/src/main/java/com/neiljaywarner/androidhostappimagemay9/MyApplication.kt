package com.neiljaywarner.androidhostappimagemay9

import android.app.Application
import android.util.Log
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor

class MyApplication : Application() {
    companion object {
        private const val TAG = "MyApplication"
        const val ENGINE_ID = "my_engine_id"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Initializing Flutter engine")

        // Initialize Flutter engine
        val flutterEngine = FlutterEngine(this)

        // Start executing Dart code in the Flutter engine
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )

        // Cache the Flutter engine for later use
        FlutterEngineCache
            .getInstance()
            .put(ENGINE_ID, flutterEngine)

        Log.d(TAG, "Flutter engine initialized and cached successfully")
    }
}