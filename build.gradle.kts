import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.intellij.platform)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

group = "me.forketyfork"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        create("IC", libs.versions.intellij.version.get())
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        // Add necessary plugin dependencies for compilation here
        bundledPlugin("com.intellij.java")
    }

    // Test dependencies
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit)
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = libs.versions.since.build.get()
        }

        changeNotes =
            """
            Initial version
            """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = libs.versions.java.compatibility.get()
        targetCompatibility = libs.versions.java.compatibility.get()
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions.jvmTarget.set(
            JvmTarget.fromTarget(libs.versions.java.compatibility.get()),
        )
    }

    test {
        useJUnitPlatform()
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    additionalEditorconfig.set(
        mapOf(
            "ktlint_chain_method_rule_force_multiline_when_chain_operator_count_greater_or_equal_than" to "5",
        ),
    )
}
