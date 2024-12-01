package com.yunzia.hyperstar

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.profileinstaller.ProfileInstaller
import com.yunzia.hyperstar.ui.base.BaseActivity
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.SPUtils
import com.yunzia.hyperstar.utils.Utils
import yunzia.utils.SystemProperties


class MainActivity : BaseActivity() {

    var isRecreate:Boolean = false

    fun isModuleActive() : Boolean{
        return false;
    }

    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {

        if (colorMode != null) {
            App(this,colorMode)
        }
    }

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
