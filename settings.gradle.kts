pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MPAndroidChart"
include(":MPChartLib", ":MPChartCompose", ":MPChartExample", ":MPChartDeviceTest")

// Modules that exist only on one machine are added by a settings.local.gradle.kts beside this file, which is
// not part of the repository.
val localSettings = file("settings.local.gradle.kts")
if (localSettings.exists()) apply(from = localSettings)
