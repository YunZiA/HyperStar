plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "io.github.libxposed.service"
    sourceSets {
        val main by getting
        main.apply {
            manifest.srcFile("service/service/src/main/AndroidManifest.xml")
            java.directories.add("service/service/src/main/java")
            aidl.directories.add("service/interface/src/main/aidl")
        }
    }

    defaultConfig {
        minSdk = 27
        compileSdk = 34
    }

    // Java 17 is required by libxposed-service
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = false
        resValues = false
        aidl = true
    }
    buildTypes {
        create("dev") {
            initWith(getByName("debug"))
        }
    }

    dependencies {
        compileOnly(project(":lsp:api"))
        compileOnly(libs.androidx.annotation)
    }

}