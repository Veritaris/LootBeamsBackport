import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.io.FileInputStream
import java.util.*

val kotlinVersion = "2.0.0"
val processResources by tasks.existing(ProcessResources::class)
val jar by tasks.existing(Jar::class)

plugins {
    kotlin("jvm") version "2.0.0" apply true
    id("forge") version "1.2-1.1.+"
    id("project-report")
    id("application")
    idea
}

val projectBuildPropertiesFile = "build.properties"
var projectBuildProperties = Properties().apply {
    load(FileInputStream(File("$projectDir/$projectBuildPropertiesFile")))
}
project.version = projectBuildProperties["version"]!!
project.extra["buildVersion"] = projectBuildProperties["buildVersion"]

group "com.dreamfinity." + { rootProject.name }

minecraft {
    version = "1.7.10-10.13.4.1614-1.7.10"
    mappings = "stable_12"
    runDir = "run"
    replace("@version@", projectBuildProperties["version"])
    replace("@debug@", projectBuildProperties["debug"])
    replace("BuildController.internalBuildState()", projectBuildProperties["isClientBuild"])
}

repositories {
    maven {
        url = uri("https://cloudrep.veritaris.me/repos/")
        metadataSources {
            mavenPom()
            artifact()
        }
    }
    flatDir {
        dirs("libs")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(8)
}

tasks.named<KotlinCompilationTask<KotlinJvmCompilerOptions>>("compileKotlin").configure {
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.named<KotlinCompilationTask<KotlinJvmCompilerOptions>>("compileTestKotlin").configure {
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.withType<Tar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<Zip> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<Jar> {
    manifest {
        attributes("FMLCorePlugin" to "org.dreamfinity.beamingdrops.HookLoader")
        attributes("FMLCorePluginContainsFMLMod" to "true")
    }
    if ((projectBuildProperties["isClientBuild"] as String?).toBoolean()) {
        archiveClassifier.set("client")
    } else {
        archiveClassifier.set("server")
    }
}

tasks.named("distZip") {
    dependsOn("reobf")
}

tasks.named("distTar") {
    dependsOn("reobf")
}

tasks.named("startScripts") {
    dependsOn("reobf")
}

processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    inputs.property("version", projectBuildProperties["version"])
    inputs.property("mcversion", project.minecraft.version)

    filesMatching("mcmod.info") {
        expand(
            "version" to projectBuildProperties["version"],
            "mcversion" to project.minecraft.version,
        )
    }

    filesMatching("mcmod.info") {
        exclude("mcmod.info")
    }
}

dependencies {
    implementation("org.dreamfinity.hooklib:HookLibUltimate-1.0:universal-dev")
    implementation("org.jetbrains:annotations:24.0.1")

    implementation(fileTree("libs"))
}

// Semantic versioning start
val semanticVersioning = mapOf(
    "Major" to "Builds .jar increasing major number: major.y.z",
    "Minor" to "Builds .jar increasing minor number: x.minor.y",
    "Patch" to "Builds .jar increasing patch number: x.y.patch",
    "JustBuild" to "Builds .jar adding \"-build-N\" suffix and increasing build number: x.y.z-build-N",
)

semanticVersioning.keys.forEach { semVerType ->
    tasks.register<WriteProperties>(semVerType) {
        destinationFile = file(projectBuildPropertiesFile)
        projectBuildProperties.replace(
            "buildVersion",
            if (semVerType.lowercase() == "justbuild") "${Integer.parseInt(projectBuildProperties["buildVersion"] as String) + 1}" else "0"
        )
        val newVersion = makeVersion(semVerType)
        projectBuildProperties.replace("version", newVersion)
        setProperties(projectBuildProperties as Map<String, Any>)
        group = "Semantic versioned"
        finalizedBy("build")
    }
}

tasks.register("TestSemVerBuilds", GradleBuild::class) {
    tasks = arrayListOf("JustBuild", "Patch", "Minor", "Major")
}

fun makeVersion(bumpType: String): String {
    val prevVersion: String = project.version as String
    var (major, minor, patch) = prevVersion.split(".")
    println("Old version: ${prevVersion}, old build number: ${project.extra["buildVersion"]}")
    patch = patch.split("-")[0]

    val newVersion = when (bumpType.lowercase()) {
        "major" -> "${Integer.parseInt(major) + 1}.0.0"
        "minor" -> "${major}.${Integer.parseInt(minor) + 1}.0"
        "patch" -> "${major}.${minor}.${Integer.parseInt(patch) + 1}"
        else -> "${major}.${minor}.${patch}-build-${Integer.parseInt("${project.extra["buildVersion"]}") + 1}"
    }

    if (bumpType in arrayOf("major", "minor", "patch")) {
        println("Migrating from $prevVersion to $newVersion")
    } else {
        println("Building version $newVersion")
    }
    project.version = newVersion
    return newVersion
}
// Semantic versioning end
