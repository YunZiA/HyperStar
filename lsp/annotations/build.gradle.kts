plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "io.github.libxposed"

    defaultConfig {
        minSdk = 35
        compileSdk = 36
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}