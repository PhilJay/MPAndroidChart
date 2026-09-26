import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
    alias(libs.plugins.dokka)
}

group = "com.github.PhilJay.MPAndroidChart"
version = libs.versions.mpandroidchart.get()

android {
    namespace = "com.github.mikephil.charting"
    // The AAR asks apps to compile against at least this SDK, so it stays as low as the library allows.
    //noinspection GradleDependency
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

// Apps built with Kotlin 2.0 can read what Kotlin 2.0 writes, so the library is compiled as 2.0.
kotlin {
    explicitApi()
    coreLibrariesVersion = "2.0.21"
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
        allWarningsAsErrors.set(true)
    }
}

dependencies {
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

val dokkaJavadocJar = tasks.register<Jar>("dokkaJavadocJar") {
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
