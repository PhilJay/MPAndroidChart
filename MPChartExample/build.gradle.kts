plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.xxmassdeveloper.mpchartexample"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.xxmassdeveloper.mpchartexample"
        minSdk = 23
        targetSdk = 37
        versionCode = 58
        versionName = libs.versions.mpandroidchart.get()
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Signed with the debug key so a shrunk build can be installed locally.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(project(":MPChartLib"))
    implementation(project(":MPChartCompose"))
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    testImplementation(libs.junit)
}

// The unit test source set only checks that the readme samples compile, so it has no tests to run.
tasks.withType<Test>().configureEach {
    failOnNoDiscoveredTests = false
}
