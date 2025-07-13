package com.yunzia.hyperstar

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.profileinstaller.ProfileInstaller
import com.yunzia.hyperstar.ui.component.BaseActivity
import com.yunzia.hyperstar.utils.AppInfo
import com.yunzia.hyperstar.utils.Helper.isModuleActive
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.SPUtils
import com.yunzia.hyperstar.viewmodel.UpdaterDownloadViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

class MainActivity : BaseActivity() {

    // Mutable states for app version and file name
    val newAppVersion = mutableStateOf("")
    val newAppName = mutableStateOf("")
    val enablePageUserScroll = mutableStateOf(false)
    val rebootStyle = mutableIntStateOf(0)

    var isRecreate: Boolean = false
    var isGranted = mutableStateOf(false)

    val appInfo =  mutableMapOf<String, AppInfo?>()
    val appNo = mutableMapOf<String, String?>()

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

    @SuppressLint("MissingPermission", "RemoteViewLayout")
    @Composable
    override fun InitData(savedInstanceState: Bundle?) {
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
            SPUtils.getInstance().init(this)
            toggleLauncherIconVisibility(PreferencesUtil.getBoolean("is_hide_icon", false))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_RECREATE, true)
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