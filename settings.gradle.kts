pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("${settingsDir}/flutter_module/build/host/outputs/repo") }
        maven { url = uri("https://storage.googleapis.com/download.flutter.io") }
    }
}

rootProject.name = "Android Host App Image May9"
include(":app")

// Include the Flutter module
val filePath = settingsDir.toString() + "/flutter_module/.android/include_flutter.groovy"
apply(from = File(filePath))