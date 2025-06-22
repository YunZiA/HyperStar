// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    id("com.google.devtools.ksp") version "2.1.21-2.0.1" apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false

}