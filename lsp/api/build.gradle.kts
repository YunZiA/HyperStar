plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "io.githuab.libxposed.api"
    sourceSets {
        val main by getting
        main.apply {
            manifest.srcFile("AndroidManifest.xml")
            java.setSrcDirs(listOf("api/api/src/main/java"))
        }
    }

    defaultConfig {
        minSdk = 35
        compileSdk = 35
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    dependencies {

        compileOnly(libs.androidx.annotation)

    }

}