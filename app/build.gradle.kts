
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
    id ("kotlin-parcelize")
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
        val versionName = "${appVersionName}_${createTime}"
        val runTasks = gradle.startParameter.taskNames
        System.out.println("> Configure project :runTasks = $runTasks")
        if (":app:assembleDebug" !in runTasks && "" !in runTasks){
            val lastVersionCode = properties["VERSION_CODE"].toString()
            if (lastVersionCode.take(7) == baseVersionCode) {
                versionCode = lastVersionCode.toInt() + 1
            }
            props["VERSION_CODE"] = versionCode.toString()
            props["VERSION_NAME"] = versionName
            FileOutputStream(versionFile).use { output ->
                props.store(output, null)
            }
        }
        System.out.println("> Configure project :{versionCode = $versionCode, versionName = $versionName}")
        Pair(versionCode, versionName)
    } else {
        throw GradleException("Could not find version.properties!")
    }
}

configure<ApplicationExtension> {
    namespace = "com.yunzia.hyperstar"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.yunzia.hyperstar"
        minSdk = 33
        targetSdk = 36
        versionCode = versionInfo.first
        versionName = versionInfo.second

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    val keystoreFile = System.getenv("KEYSTORE_PATH")
    signingConfigs {
        if (keystoreFile != null) {
            create("ci") {
                storeFile = file(keystoreFile)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
                enableV4Signing = true
            }
        }else{
            create("default"){
                enableV4Signing = true

            }
        }

    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName(if (keystoreFile != null) "ci" else "default")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            // 对于debug版本，可以不使用混和资源压缩
            isMinifyEnabled = false
            isShrinkResources = false
        }
        create("dev") {
            signingConfig = signingConfigs.getByName(if (keystoreFile != null) "ci" else "default")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("cc") {
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        aidl = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    androidResources {
        additionalParameters.clear() // 清空已有参数
        additionalParameters.add("--allow-reserved-package-id")
        additionalParameters.add("--package-id")
        additionalParameters.add("0x66")
    }
}

base {
    archivesName.set("HyperStar_${versionInfo.second}")
}

dependencies {
    compileOnly(project(":lsp:annotations"))
    compileOnly(project(":lsp:api"))
    implementation(project(":lsp:service"))
    implementation(libs.ezxhelper.core)
    implementation(libs.ezxhelper.xposed.api)
    implementation(libs.ezxhelper.android.utils)

    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.haze)
    implementation(libs.kyant.shapes)
    implementation(libs.miuix)
    implementation(libs.miuix.icons)
    implementation(libs.miuix.navigation3.ui)
    implementation(libs.miuix.navigation3.adaptive)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigationevent.compose)

    implementation (libs.androidx.palette.ktx)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.github.colormath.ext.jetpack.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.accompanist.drawablepainter)
    implementation(libs.androidx.compose.material)
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
