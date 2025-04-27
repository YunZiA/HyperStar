package com.yunzia.hyperstar

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.profileinstaller.ProfileInstaller
import com.yunzia.hyperstar.ui.base.BaseActivity
import com.yunzia.hyperstar.utils.AppInfo
import com.yunzia.hyperstar.utils.Helper.isModuleActive
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.SPUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException


class MainActivity : BaseActivity() {

    val newAppVersion = mutableStateOf("")
    val newAppName = mutableStateOf("")

    val enablePageUserScroll = mutableStateOf(false)
    val rebootStyle =  mutableIntStateOf(0)

    var isRecreate:Boolean = false

    var isGranted = mutableStateOf(false)

    val themeManager: MutableState<AppInfo?> = mutableStateOf(null)
    val barrageManger: MutableState<AppInfo?> = mutableStateOf(null)


    @Composable
    override fun InitView() {
        enablePageUserScroll.value = PreferencesUtil.getBoolean("page_user_scroll",false)
        rebootStyle.intValue = PreferencesUtil.getInt("reboot_menus_style",0)
        App()
    }

    fun getInstalledApps(){
        try {
            val permissionInfo = applicationContext.packageManager.getPermissionInfo(
                "com.android.permission.GET_INSTALLED_APPS",
                0
            )
            if (permissionInfo != null && permissionInfo.packageName == "com.lbe.security.miui") {
                //MIUI 系统支持动态申请该权限
                if (ContextCompat.checkSelfPermission(
                        applicationContext,
                        "com.android.permission.GET_INSTALLED_APPS"
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //没有权限，需要申请
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf("com.android.permission.GET_INSTALLED_APPS"),
                        999
                    )
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    fun getNewVersionApkName(): String {
        val rawFileUrl = "https://gitee.com/dongdong-gc/hyper-star-updater/raw/main/dev/apk_name.txt"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(rawFileUrl)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val fileContent = response.body?.string()
                Log.d("ggc","文件内容: $fileContent")
                return fileContent.toString()
            } else {
                Log.d("ggc","请求失败，状态码: $response.code")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "null"
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == 999){
            isGranted.value = grantResults.isNotEmpty() &&  grantResults[0] == 0
        }
    }


    @SuppressLint("MissingPermission", "RemoteViewLayout")
    @Composable
    override fun InitData(savedInstanceState: Bundle?) {

        val pm = this@MainActivity.packageManager

        LaunchedEffect(Unit) {

            try {

                val theme = pm.getPackageInfo("com.android.thememanager", PackageManager.GET_META_DATA)
                themeManager.value = AppInfo(
                    theme.applicationInfo?.loadIcon(pm),
                    theme.versionName,
                    theme.longVersionCode
                )
                val barrage = pm.getPackageInfo("com.xiaomi.barrage", PackageManager.GET_META_DATA)
                barrageManger.value = AppInfo(
                    barrage.applicationInfo?.loadIcon(pm),
                    barrage.versionName,
                    barrage.longVersionCode
                )
            } catch (e: PackageManager.NameNotFoundException) {

            }
        }

        LaunchedEffect(Unit) {
            val downloadsDir = this@MainActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            clearDirectory(downloadsDir)
            val apkName = withContext(Dispatchers.IO) {
                getNewVersionApkName()
            }
            newAppVersion.value = apkName.removePrefix("HyperStar_v").removePrefix("_dev").trim()
            newAppName.value = "$apkName.apk"
        }

        val isRecreate = savedInstanceState?.getBoolean("isRecreate",true)
        if (isRecreate != null && isRecreate){
            this.isRecreate = true
        }
        ProfileInstaller.writeProfile(this)
        if (isModuleActive()){
            SPUtils.getInstance().init(this)
            showLauncherIcon(PreferencesUtil.getBoolean("is_hide_icon",false))
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //outState.putBoolean("isRecreate",true)
    }


    private fun showLauncherIcon(isHide: Boolean) {
        val packageManager = this.packageManager
        val show = if (isHide) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        packageManager.setComponentEnabledSetting(
            getAliseComponentName(),
            show,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun getAliseComponentName(): ComponentName {
        return ComponentName(this@MainActivity, "com.yunzia.hyperstar.MainActivityAlias")


    }

    fun clearDirectory(directory: File?) {
        // 确保目录存在且是一个文件夹
        if (directory != null && directory.exists() && directory.isDirectory) {
            // 遍历目录中的所有文件并逐一删除
            directory.listFiles()?.forEach { file ->
                if (file.isFile) {
                    file.delete()
                } else if (file.isDirectory) {
                    // 如果是子目录，递归清理
                    clearDirectory(file)
                    file.delete() // 删除空的子目录
                }
            }
        }
    }

    fun goManagerFileAccess():Boolean {

        if (!Environment.isExternalStorageManager()) {
            val appIntent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            appIntent.setData(Uri.parse("package:$packageName"))
            try {
                this.startActivity(appIntent)
            } catch (ex: ActivityNotFoundException) {
                ex.printStackTrace()
                val allFileIntent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                this.startActivity(allFileIntent)
            }

        }
        return Environment.isExternalStorageManager()
    }



}
