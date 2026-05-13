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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.yunzia.hyperstar.ui.component.BaseActivity
import com.yunzia.hyperstar.utils.AppInfo
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.utils.LocalScopeManager
import com.yunzia.hyperstar.viewmodel.AppViewModel
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
    val newAppVersion = mutableStateOf("")
    val newAppName = mutableStateOf("")
    val enablePageUserScroll = mutableStateOf(false)
    val rebootStyle = mutableIntStateOf(0)

    var isRecreate: Boolean = false
    var isGranted = mutableStateOf(false)

    val downloadModel: UpdaterDownloadViewModel by viewModels()
    val appViewModel: AppViewModel by viewModels()

    @Composable
    override fun InitView() {
        enablePageUserScroll.value = PreferencesUtil.getBoolean("page_user_scroll", false)
        rebootStyle.intValue = PreferencesUtil.getInt("reboot_menus_style", 0)

        XposedServiceHelper.registerListener(object : XposedServiceHelper.OnServiceListener {
            override fun onServiceBind(service: XposedService) {
                appViewModel.onXposedServiceBound(service)
                SPUtils.init(service)
                service.openRemoteFile("test.txt").use { pfd ->
                    FileWriter(pfd.fileDescriptor).use {
                        it.append("Hello World!")
                    }
                }

            }

            override fun onServiceDied(service: XposedService) {
                Log.d("ggc", "onServiceDied: \n$service")
                appViewModel.onXposedServiceReleased()
            }
        })

        CompositionLocalProvider(LocalScopeManager provides appViewModel.scopeManager) {
            App()
        }
    }


    @SuppressLint("MissingPermission", "RemoteViewLayout")
    @Composable
    override fun InitData(savedInstanceState: Bundle?) {
        setLauncherIconHidden(PreferencesUtil.getBoolean("is_hide_icon", false))


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
        val client = httpClient
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
//            putBinder()
        }
    }





    private fun getAliasComponentName(): ComponentName {

        return ComponentName(this, "com.yunzia.hyperstar.MainActivityAlias")
    }

    fun isLauncherIconHidden(): Boolean {
        val component = getAliasComponentName()

        val manager = packageManager
        val intent = Intent().setComponent(component)

        val list = manager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
            )

        return list.isEmpty()
    }


    fun setLauncherIconHidden(hide: Boolean) {

        if (isLauncherIconHidden() == hide) return

        val component = getAliasComponentName()

        val newState = if (hide) {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        }

        packageManager.setComponentEnabledSetting(
            component,
            newState,
            PackageManager.DONT_KILL_APP
        )
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
        private const val TAG = "TAG"
        private const val REQUEST_CODE_INSTALLED_APPS = 999
        private const val KEY_IS_RECREATE = "isRecreate"
        private const val KEY_IS_ACTIVE = "isActive"
        private val httpClient = OkHttpClient()
    }
}
