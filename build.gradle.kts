import org.jetbrains.kotlin.abi.tools.api.AbiFilters
import org.jetbrains.kotlin.abi.tools.api.AbiToolsFactory
import org.jetbrains.kotlin.abi.tools.api.AbiToolsInterface
import java.net.URLClassLoader
import java.util.ServiceLoader

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

// The Kotlin plugin's own ABI validation is not available with the Kotlin support built into AGP, so the published
// modules write and check their dump with the Kotlin ABI tools directly.
abstract class AbiDumpTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val classes: ConfigurableFileCollection

    @get:Classpath
    abstract val toolsClasspath: ConfigurableFileCollection

    @get:OutputFile
    abstract val dumpFile: RegularFileProperty

    @TaskAction
    fun dump() {
        val classFiles = classes.asFileTree.matching { include("**/*.class") }.files
        dumpFile.get().asFile.bufferedWriter().use { writer ->
            AbiTools.load(toolsClasspath).v2.printJvmDump(writer, classFiles, AbiFilters.EMPTY)
        }
    }
}

abstract class AbiCheckTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val builtDump: RegularFileProperty

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val checkedInDump: ConfigurableFileCollection

    @get:Classpath
    abstract val toolsClasspath: ConfigurableFileCollection

    @get:Input
    abstract val projectPath: Property<String>

    @TaskAction
    fun check() {
        val expected = checkedInDump.singleOrNull()?.takeIf { it.exists() }
            ?: throw GradleException("${projectPath.get()} has no ABI dump. Run ./gradlew apiDump and commit the api folder.")
        val difference = AbiTools.load(toolsClasspath).filesDiff(expected, builtDump.get().asFile)
        if (difference != null) {
            throw GradleException("The public API of ${projectPath.get()} changed. If that is intended, run ./gradlew apiDump and commit the api folder.\n$difference")
        }
    }
}

object AbiTools {
    fun load(classpath: FileCollection): AbiToolsInterface {
        val loader = URLClassLoader(classpath.files.map { it.toURI().toURL() }.toTypedArray(), AbiToolsFactory::class.java.classLoader)
        return ServiceLoader.load(AbiToolsFactory::class.java, loader).first().get()
    }
}

val abiToolsClasspath = configurations.create("abiTools") {
    isCanBeConsumed = false
}
dependencies {
    abiToolsClasspath(libs.kotlin.abi.tools)
}

for (path in listOf(":MPChartLib", ":MPChartCompose")) {
    project(path) {
        pluginManager.withPlugin("com.android.library") {
            val dumpName = "${project.name}.api"
            val checkedInFile = layout.projectDirectory.file("api/$dumpName")
            val apiBuild = tasks.register<AbiDumpTask>("apiBuild") {
                description = "Writes the public API of the release build to the build folder."
                classes.from(tasks.named("compileReleaseKotlin"))
                toolsClasspath.from(abiToolsClasspath)
                dumpFile.set(layout.buildDirectory.file("api/$dumpName"))
            }
            val apiCheck = tasks.register<AbiCheckTask>("apiCheck") {
                group = "verification"
                description = "Fails when the public API differs from the dump in the api folder."
                builtDump.set(apiBuild.flatMap { it.dumpFile })
                checkedInDump.from(checkedInFile)
                toolsClasspath.from(abiToolsClasspath)
                projectPath.set(path)
            }
            tasks.register<Copy>("apiDump") {
                description = "Updates the dump in the api folder to the current public API."
                from(apiBuild)
                into(layout.projectDirectory.dir("api"))
            }
            tasks.named("check") { dependsOn(apiCheck) }
        }
    }
}
