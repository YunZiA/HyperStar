
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
    id ("kotlin-parcelize")
}

android {

    val versionFile = file("version.properties")
    val properties = Properties().apply {
        load(FileInputStream(versionFile))
    }

    fun getVersionCode():Int {
        if (versionFile.canRead()) {

            val versionName = properties["VERSION_NAME"].toString().split(".")[0]
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val createTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai")).format(formatter).takeLast(6)
            val baseVersionCode = "$versionName$createTime"
            var versionCode = "${baseVersionCode}00".toInt()
            val runTasks = gradle.startParameter.taskNames
            System.out.println("> Configure project :runTasks = $runTasks")
            if (":app:assembleDebug" !in runTasks && "" !in runTasks){
                val lastVersionCode = properties["VERSION_CODE"].toString()
                if (lastVersionCode.take(7) == baseVersionCode){
                    versionCode = lastVersionCode.toInt()+1
                }
                System.out.println("> Configure project :app:assembleRelease versionCode = $versionCode")

                properties["VERSION_CODE"] = versionCode.toString()
                FileOutputStream(versionFile).use { output ->
                    properties.store(output, null)
                }
            }

            return versionCode
        } else {
            throw GradleException("Could not find version.properties!")
        }
    }

    fun getVersionName():String{
        if (versionFile.canRead()) {

            val runTasks = gradle.startParameter.taskNames
            System.out.println("> Configure project :runTasks = $runTasks")

            val buildChannel = when {
                runTasks.any { it.contains("assembleDebug", ignoreCase = true) } -> "debug"
                runTasks.any { it.contains("assembleRelease", ignoreCase = true) } -> "release"
                runTasks.any { it.contains("assembleDev", ignoreCase = true) } -> "dev"
                else -> "Unknown"
            }

            val versionName = properties["VERSION_NAME"].toString()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
            val createTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai")).format(formatter)
            return "${versionName}_${createTime}_$buildChannel"
        } else {
            throw GradleException("Could not find version.properties!")
        }

    }
    namespace = "com.yunzia.hyperstar"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.yunzia.hyperstar"
        minSdk = 33
        targetSdk = 36
        versionCode = getVersionCode()
        versionName = getVersionName()

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

    applicationVariants.all {
        outputs.all {
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                val config = project.android.defaultConfig
                val appName = "HyperStar"
                val versionName = "v"+config.versionName

                this.outputFileName = "${appName}_${versionName}.apk"
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
    //outputFileName = "${defaultConfig.applicationId}${buildType.applicationIdSuffix}-${defaultConfig.versionName}${buildType.versionNameSuffix}.apk"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}
dependencies {
    implementation(project(":annotations"))
    implementation(project(":liquidglass"))
    ksp(libs.androidx.room.compiler)
    ksp(project(":processor"))
    implementation(libs.kotlinpoet) // 用于生成代码
    implementation(libs.okhttp)
    implementation(libs.dexkit)
    implementation(libs.gson)
    implementation(libs.androidx.navigation.compose)
    implementation("com.github.skydoves:cloudy:0.2.4")
    implementation(libs.haze)

    implementation (libs.androidx.palette.ktx)
    implementation(libs.kotlinx.serialization.json)

    //implementation ("com.godaddy.android.colorpicker:compose-color-picker-android:0.7.0")
    implementation(libs.androidx.compose.material)
    implementation(libs.github.colormath.ext.jetpack.compose)
    implementation(libs.androidx.profileinstaller)
    implementation (libs.androidx.constraintlayout.compose)
    implementation (libs.accompanist.drawablepainter)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    compileOnly (libs.android.xposed)
    implementation(libs.kyuubiran.ezxhelper)
    implementation(libs.androidx.foundation)
    implementation (libs.miuix)
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
