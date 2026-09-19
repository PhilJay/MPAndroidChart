plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
    alias(libs.plugins.dokka)
}

group = "com.github.PhilJay.MPAndroidChart"
version = "4.0.0-beta01"

android {
    namespace = "com.github.mikephil.charting"
    compileSdk = 35

    defaultConfig {
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(libs.androidx.annotation)
    testImplementation(libs.junit)
}

dokka {
    moduleName.set("MPChartLib")
    dokkaSourceSets.configureEach {
        includes.from("Module.md")
        sourceLink {
            localDirectory.set(file("src/main/kotlin"))
            remoteUrl("https://github.com/PhilJay/MPAndroidChart/tree/master/MPChartLib/src/main/kotlin")
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
            artifactId = "MPChartLib"
            version = project.version.toString()
            afterEvaluate { from(components["release"]) }
            artifact(dokkaJavadocJar)
            pom {
                name.set("MPChartLib")
                description.set("Android chart library")
                url.set("https://github.com/PhilJay/MPAndroidChart")
                licenses { license { name.set("Apache License, Version 2.0"); url.set("https://www.apache.org/licenses/LICENSE-2.0") } }
            }
        }
    }
}
