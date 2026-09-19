buildscript {
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.dokka)
}

dependencies {
    dokka(project(":MPChartLib"))
    dokka(project(":MPChartCompose"))
}

dokka {
    moduleName.set("MPAndroidChart")
}
