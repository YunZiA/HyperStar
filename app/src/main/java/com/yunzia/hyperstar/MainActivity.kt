package com.yunzia.hyperstar

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.profileinstaller.ProfileInstaller
import androidx.wear.compose.material.Text
import com.yunzia.hyperstar.ui.component.BaseActivity
import com.yunzia.hyperstar.utils.AppInfo
import com.yunzia.hyperstar.utils.Helper.isModuleActive
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.viewmodel.UpdaterDownloadViewModel
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileWriter
import java.io.IOException

class MainActivity : BaseActivity() {

    var isActive by mutableStateOf(false)
    private var mService: XposedService? = null
    val newAppVersion = mutableStateOf("")
    val newAppName = mutableStateOf("")
    val enablePageUserScroll = mutableStateOf(false)
    val rebootStyle = mutableIntStateOf(0)

    var isRecreate: Boolean = false
    var isGranted = mutableStateOf(false)

    val appInfo = mutableStateMapOf<String, AppInfo?>()
    val appNo = mutableStateMapOf<String, String?>()

    val themeManager: MutableState<AppInfo?> = mutableStateOf(null)
    val barrageManager: MutableState<AppInfo?> = mutableStateOf(null)
    val miuiScreenshot: MutableState<AppInfo?> = mutableStateOf(null)
    val downloadModel: UpdaterDownloadViewModel by viewModels()

    @Composable
    override fun InitView() {
        enablePageUserScroll.value = PreferencesUtil.getBoolean("page_user_scroll", false)
        rebootStyle.intValue = PreferencesUtil.getInt("reboot_menus_style", 0)
        App()
    }


    @SuppressLint("MissingPermission", "RemoteViewLayout")
    @Composable
    override fun InitData(savedInstanceState: Bundle?) {
        isActive = savedInstanceState?.getBoolean(KEY_IS_ACTIVE,false)?:false
//
//        TextView(this).setText()
//        TypedArray().getText()
        var serviceInfo = ""
        XposedServiceHelper.registerListener(object : XposedServiceHelper.OnServiceListener {
            override fun onServiceBind(service: XposedService) {
                isActive = true
                mService = service
                SPUtils.init(service)
                serviceInfo += "Binder acquired"
                serviceInfo += "\nAPI " + service.apiVersion
                serviceInfo += "\nFramework " + service.frameworkName
                serviceInfo += "\nFramework version " + service.frameworkVersion
                serviceInfo += "\nFramework version code " + service.frameworkVersionCode
                serviceInfo += "\nScope: " + service.scope
                Log.d("ggc", "onServiceBind: \n$serviceInfo")

//                binding.requestScope.setOnClickListener {
//                    service.requestScope("com.android.settings", mCallback)
//                }

                service.openRemoteFile("test.txt").use { pfd ->
                    FileWriter(pfd.fileDescriptor).use {
                        it.append("Hello World!")
                    }
                }

                service.openRemoteFile("test.txt").use { pfd ->
                    FileWriter(pfd.fileDescriptor).use {
                        it.append("Hello World!")
                    }
                }

                this@MainActivity.resources.getDrawable(R.drawable.ic_bootloader)

            }

            override fun onServiceDied(service: XposedService) {
                Log.d("ggc", "onServiceDied: \n$service")

            }
        })

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if (mService == null) {
                Log.d("BaseActivity", "onCreate: Binder is null")
            }
        }, 5000)
        val packageManager = this.packageManager

        // Load theme and barrage manager info
        LaunchedEffect(Unit) {
            appInfo.putAll(
                coroutineScope {
                    this@MainActivity.resources.getStringArray(R.array.module_scope).associateWith { packageName ->
                        async {
                            try {
                                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
                                AppInfo(
                                    appIcon = packageInfo.applicationInfo?.loadIcon(packageManager),
                                    appName = packageManager.getApplicationLabel(packageInfo.applicationInfo!!).toString(),
                                    versionName = packageInfo.versionName,
                                    versionCode = packageInfo.longVersionCode
                                )
                            } catch (e: PackageManager.NameNotFoundException) {
                                Log.w("ggc", "Package not found: $packageName")
                                appNo[packageName] = e.message
                                null
                            }
                        }
                    }
                }.mapValues { (_, deferred) -> deferred.await() }


            )
        }

        // Fetch new app version and name
        LaunchedEffect(Unit) {
            val downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            clearDirectory(downloadsDir)

            val apkName = fetchNewVersionApkName()
            newAppVersion.value = apkName.removePrefix("HyperStar_v").removePrefix("_dev")
            newAppName.value = "$apkName.apk"
        }

        // Check if the activity is being recreated
        savedInstanceState?.getBoolean(KEY_IS_RECREATE, true)?.let {
            isRecreate = it
        }

        // Initialize profile installer and module settings
        ProfileInstaller.writeProfile(this)
        if (isModuleActive()) {
            toggleLauncherIconVisibility(PreferencesUtil.getBoolean("is_hide_icon", false))
        }
    }

    /**
     * Request permissions for accessing installed apps (MIUI-specific).
     */
    internal fun requestInstalledAppsPermission() {
        try {
            val permissionInfo = applicationContext.packageManager.getPermissionInfo(
                "com.android.permission.GET_INSTALLED_APPS",
                0
            )
            if (permissionInfo?.packageName == "com.lbe.security.miui" &&
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    "com.android.permission.GET_INSTALLED_APPS"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf("com.android.permission.GET_INSTALLED_APPS"),
                    REQUEST_CODE_INSTALLED_APPS
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * Fetch the new APK version name from a remote server.
     */
    private suspend fun fetchNewVersionApkName(): String = withContext(Dispatchers.IO) {
        val rawFileUrl = "https://gitee.com/dongdong-gc/hyper-star-updater/raw/main/dev/apk_name.m3u"
        val client = OkHttpClient()
        val request = Request.Builder().url(rawFileUrl).build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Log.e(TAG, "Request: ${response}")
                response.body?.string()?.trim().orEmpty()
            } else {
                Log.e(TAG, "Request failed with status code: ${response.code}")
                "null"
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error fetching APK name: ${e.message}")
            "null"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == REQUEST_CODE_INSTALLED_APPS) {
            isGranted.value = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putBoolean(KEY_IS_RECREATE, true)
            putBoolean(KEY_IS_ACTIVE, isActive)
        }
    }

    /**
     * Toggle the visibility of the launcher icon.
     */
    private fun toggleLauncherIconVisibility(isHide: Boolean) {
        val state = if (isHide) {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        }
        packageManager.setComponentEnabledSetting(getAliasComponentName(), state, PackageManager.DONT_KILL_APP)
    }

    /**
     * Get the alias component name for the launcher icon.
     */
    private fun getAliasComponentName(): ComponentName {
        return ComponentName(this, "com.yunzia.hyperstar.MainActivityAlias")
    }

    /**
     * Clear the specified directory.
     */
    private fun clearDirectory(directory: File?) {
        directory?.takeIf { it.exists() && it.isDirectory }?.listFiles()?.forEach { file ->
            if (file.isFile) {
                file.delete()
            } else if (file.isDirectory) {
                clearDirectory(file)
                file.delete()
            }
        }
    }

    /**
     * Launch the file access manager to request storage management permissions.
     */
    fun requestFileAccessPermission(): Boolean {
        if (!Environment.isExternalStorageManager()) {
            val appIntent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = "package:$packageName".toUri()
            }
            try {
                startActivity(appIntent)
            } catch (ex: ActivityNotFoundException) {
                val allFileIntent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(allFileIntent)
            }
        }
        return Environment.isExternalStorageManager()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE_INSTALLED_APPS = 999
        private const val KEY_IS_RECREATE = "isRecreate"
        private const val KEY_IS_ACTIVE = "isActive"
    }
}

/**
 * Extension function to get app info by package name.
 */
private suspend fun PackageManager.getApplicationInfos(
    vararg packageNames: String
): Map<String, AppInfo?> {
    return coroutineScope {
        packageNames.associateWith { packageName ->
            async {
                try {
                    val packageInfo = this@getApplicationInfos.getPackageInfo(packageName, PackageManager.GET_META_DATA)
                    AppInfo(
                        appIcon = packageInfo.applicationInfo?.loadIcon(this@getApplicationInfos),
                        appName = this@getApplicationInfos.getApplicationLabel(packageInfo.applicationInfo!!).toString(),
                        versionName = packageInfo.versionName,
                        versionCode = packageInfo.longVersionCode
                    )
                    null
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.w("ggc", "Package not found: $packageName")
                    null
                }
            }
        }.mapValues { it.value.await() }
    }
}
private fun PackageManager.getAppInfo(packageName: String): AppInfo {
    val packageInfo = getPackageInfo(packageName, PackageManager.GET_META_DATA)
    return AppInfo(
        appIcon = packageInfo.applicationInfo?.loadIcon(this),
        appName = this.getApplicationLabel(packageInfo.applicationInfo!!).toString(),
        versionName = packageInfo.versionName,
        versionCode = packageInfo.longVersionCode
    )
}