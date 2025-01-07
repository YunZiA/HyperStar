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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.profileinstaller.ProfileInstaller
import com.yunzia.hyperstar.ui.base.BaseActivity
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.SPUtils
import com.yunzia.hyperstar.utils.Helper.isModuleActive


class MainActivity : BaseActivity() {

    val enablePageUserScroll = mutableStateOf(false)
    val rebootStyle =  mutableIntStateOf(0)

    var isRecreate:Boolean = false

    var isGranted = mutableStateOf(false)


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
    override fun initData(savedInstanceState: Bundle?) {

        val isRecreate = savedInstanceState?.getBoolean("isRecreate",true)
        if (isRecreate != null && isRecreate){
            this.isRecreate = true
        }
        ProfileInstaller.writeProfile(this)
        if (isModuleActive()){

            SPUtils.getInstance().init(this);

        }
        showLauncherIcon(PreferencesUtil.getBoolean("is_hide_icon",false))

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
