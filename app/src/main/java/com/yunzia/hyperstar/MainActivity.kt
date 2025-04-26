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
import java.io.File


class MainActivity : BaseActivity() {

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

//    fun downloadApkFromGithub() {
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url("https://api.github.com/repos/YunZiA/HyperStar/releases/latest") // 修改为你的仓库
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                val json = response.body?.string()
//                val downloadUrl = extractApkUrlFromJson(json) // 提取 APK 的下载链接
//                downloadAndInstallApk(downloadUrl)
//            }
//        })
//    }
//
//    fun extractApkUrlFromJson(json: String?): String {
//        // 使用 JSON 解析提取 APK 文件的下载链接
//        // 示例：解析 "assets" 中的 "browser_download_url"
//        return "https://github.com/YunZiA/HyperStar/releases/download/v1.0.0/app-release.apk"
//    }
//
//    fun downloadAndInstallApk(url: String) {
//        val client = OkHttpClient()
//        val request = Request.Builder().url(url).build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                val file = File(this.getExternalFilesDir(null), "app-release.apk")
//                val inputStream = response.body?.byteStream()
//                val outputStream = FileOutputStream(file)
//
//                inputStream?.copyTo(outputStream)
//                inputStream?.close()
//                outputStream.close()
//
//                // 安装 APK
//                installApk(file)
//            }
//        })
//    }

    fun installApk(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(intent)
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
