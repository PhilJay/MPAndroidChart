import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin.compose)
}

group = "com.github.PhilJay.MPAndroidChart"
version = libs.versions.mpandroidchart.get()

android {
    namespace = "com.github.mikephil.charting.compose"
    compileSdk = 37

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

// Apps built with Kotlin 2.0 can read what Kotlin 2.0 writes, so the module is compiled as 2.0.
kotlin {
    explicitApi()
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
        allWarningsAsErrors.set(true)
    }
}

dependencies {
    api(project(":MPChartLib"))
    api(platform(libs.compose.bom))
    api(libs.compose.ui)
    implementation(libs.compose.foundation)

    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.activity.compose)
}

dokka {
    moduleName.set("MPChartCompose")
    dokkaSourceSets.configureEach {
        includes.from("Module.md")
        sourceLink {
            localDirectory.set(file("src/main/kotlin"))
            remoteUrl("https://github.com/PhilJay/MPAndroidChart/tree/master/MPChartCompose/src/main/kotlin")
        }
    }
}

val dokkaJavadocJar = tasks.register<Jar>("dokkaJavadocJar") {
    description = "Packs the Dokka HTML output as the javadoc artifact JitPack serves."
    dependsOn(tasks.dokkaGeneratePublicationHtml)
    from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "MPChartCompose"
            version = project.version.toString()
            afterEvaluate { from(components["release"]) }
            artifact(dokkaJavadocJar)
            pom {
                name.set("MPChartCompose")
                description.set("Jetpack Compose wrappers for MPAndroidChart")
                url.set("https://github.com/PhilJay/MPAndroidChart")
                licenses { license { name.set("Apache License, Version 2.0"); url.set("https://www.apache.org/licenses/LICENSE-2.0") } }
            }
        }
    }
}
