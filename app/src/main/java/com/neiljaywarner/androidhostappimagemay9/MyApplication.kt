package com.neiljaywarner.androidhostappimagemay9

import android.app.Application
import android.util.Log
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.dart.DartExecutor

class MyApplication : Application() {
    companion object {
        private const val TAG = "MyApplication"
        const val ENGINE_ID = "my_engine_id"
        const val PROFILE_ENGINE_ID = "profile_engine_id"
        const val SERVICE_ENGINE_ID = "service_engine_id"
        const val ITEMS_ENGINE_ID = "items_engine_id"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Initializing Flutter engines")

        // Create a FlutterEngineGroup to manage multiple Flutter engines efficiently
        val engineGroup = FlutterEngineGroup(this)

        // Create and cache the main Flutter engine
        val mainFlutterEngine = engineGroup.createAndRunEngine(
            this,
            DartExecutor.DartEntrypoint.createDefault()
        )

        // Create and cache the Profile Flutter engine with initial route
        val profileOptions = FlutterEngineGroup.Options(this)
            .setInitialRoute("/profile")
        val profileFlutterEngine = engineGroup.createAndRunEngine(profileOptions)

        // Create and cache the Service Flutter engine with initial route
        // Make sure we're using the right route format with leading slash
        val serviceOptions = FlutterEngineGroup.Options(this)
            .setInitialRoute("/service")
        // Log the route to help with debugging
        Log.d(TAG, "Creating service engine with route: /service")
        val serviceFlutterEngine = engineGroup.createAndRunEngine(serviceOptions)

        // Create and cache the Items Flutter engine with initial route
        val itemsOptions = FlutterEngineGroup.Options(this)
            .setInitialRoute("/items")
        Log.d(TAG, "Creating items engine with route: /items")
        val itemsFlutterEngine = engineGroup.createAndRunEngine(itemsOptions)

        // Cache all engines for later use
        FlutterEngineCache.getInstance().apply {
            put(ENGINE_ID, mainFlutterEngine)
            put(PROFILE_ENGINE_ID, profileFlutterEngine)
            put(SERVICE_ENGINE_ID, serviceFlutterEngine)
            put(ITEMS_ENGINE_ID, itemsFlutterEngine)
        }

        Log.d(TAG, "Flutter engines initialized and cached successfully")
    }
}