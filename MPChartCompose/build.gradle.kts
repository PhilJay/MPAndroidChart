plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin.compose)
}

group = "com.github.PhilJay.MPAndroidChart"
version = "4.0.0-beta01"

android {
    namespace = "com.github.mikephil.charting.compose"
    compileSdk = 37

    defaultConfig {
        minSdk = 23
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

dependencies {
    api(project(":MPChartLib"))
    api(platform(libs.compose.bom))
    api(libs.compose.ui)
    implementation(libs.compose.foundation)
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

val dokkaJavadocJar by tasks.registering(Jar::class) {
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
