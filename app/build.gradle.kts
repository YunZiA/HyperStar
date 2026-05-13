
import com.android.build.api.dsl.ApplicationExtension
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id ("kotlin-parcelize")
    alias(libs.plugins.baselineprofile)
}

val versionInfo: Pair<Int, String> by lazy {
    val versionFile = file("version.properties")
    val props = Properties().apply { load(FileInputStream(versionFile)) }
    if (versionFile.canRead()) {
        val appVersionName = props.getProperty("APP_VERSION_NAME", "1.0.0")
        val versionNamePart = appVersionName.split(".").first() //yyyyMMdd
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHm")
        val createTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai")).format(formatter)

        val baseVersionCode = "$versionNamePart${createTime.substring(2, 8)}"
        var versionCode = "${baseVersionCode}00".toInt()
        val generatedVersionName = "${appVersionName}_${createTime}"
        val storedVersionCode = props.getProperty("VERSION_CODE")?.toIntOrNull()
        val storedVersionName = props.getProperty("VERSION_NAME")
        val shouldUpdateVersionFile = gradle.startParameter.taskNames.any { taskName ->
            taskName.contains("Release", ignoreCase = true) || taskName.contains("Dev", ignoreCase = true)
        }
        val versionName = if (shouldUpdateVersionFile) generatedVersionName else storedVersionName ?: generatedVersionName

        if (shouldUpdateVersionFile) {
            val lastVersionCode = storedVersionCode?.toString().orEmpty()
            if (lastVersionCode.take(7) == baseVersionCode) {
                versionCode = lastVersionCode.toInt() + 1
            }
            props["VERSION_CODE"] = versionCode.toString()
            props["VERSION_NAME"] = versionName
            FileOutputStream(versionFile).use { output ->
                props.store(output, null)
            }
        } else {
            versionCode = storedVersionCode ?: versionCode
        }
        System.out.println("> Configure project :{versionCode = $versionCode, versionName = $versionName}")
        Pair(versionCode, versionName)
    } else {
        throw GradleException("Could not find version.properties!")
    }
}

configure<ApplicationExtension> {
    namespace = "com.yunzia.hyperstar"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.yunzia.hyperstar"
        minSdk = 33
        targetSdk = 37
        versionCode = versionInfo.first
        versionName = versionInfo.second

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    val localProps = Properties().apply {
        val localFile = rootProject.file("KEY.properties")
        if (localFile.exists()) load(FileInputStream(localFile))
    }

    val keystorePath = System.getenv("KEYSTORE_PATH")
        ?: localProps.getProperty("KEYSTORE_PATH")
    val keystorePassword = System.getenv("KEYSTORE_PASSWORD")
        ?: localProps.getProperty("KEYSTORE_PASSWORD")
    val keystoreAlias = System.getenv("KEY_ALIAS")
        ?: localProps.getProperty("KEY_ALIAS")
    val keystoreKeyPassword = System.getenv("KEY_PASSWORD")
        ?: localProps.getProperty("KEY_PASSWORD")

    val hasSigning = keystorePath != null && keystorePassword != null
            && keystoreAlias != null && keystoreKeyPassword != null

    signingConfigs {
        if (hasSigning) {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = keystorePassword
                keyAlias = keystoreAlias
                keyPassword = keystoreKeyPassword
                enableV4Signing = true
            }
        }
    }

    buildTypes {
        release {
            if (hasSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            optimization.enable = true
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            matchingFallbacks.add("release")
        }

        debug {
            // 对于debug版本，可以不使用混和资源压缩
//            optimization.enable = false
        }
        create("dev") {
            if (hasSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            optimization.enable = true
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            matchingFallbacks.add("dev")
            matchingFallbacks += listOf("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        aidl = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    androidResources {
        androidResources.additionalParameters += listOf("--allow-reserved-package-id", "--package-id", "0x39")
    }
}

base {
    archivesName.set("HyperStar_${versionInfo.second}")
}

dependencies {
    ksp(project(":ksp-processor"))
    implementation(project(":ksp-processor"))
    implementation(project(":ksp-annotation"))
    "baselineProfile"(project(":baselineprofile"))
//    implementation(libs.androidx.compose.ui.text)

    compileOnly(libs.api)
    implementation(libs.service)

    implementation(libs.okhttp)
    implementation(libs.gson)
//    implementation(libs.kyant.shapes)
    implementation(libs.miuix)
    implementation(libs.miuix.preference)
    implementation(libs.miuix.blur)
    implementation(libs.miuix.shapes)
    implementation(libs.miuix.icons)
    implementation(libs.miuix.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigationevent.compose)

    implementation (libs.androidx.palette.ktx)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.github.colormath.ext.jetpack.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.accompanist.drawablepainter)
//    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.tooling.preview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}
