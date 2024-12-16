package com.yunzia.hyperstar

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.profileinstaller.ProfileInstaller
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.ui.base.BaseActivity
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.SPUtils
import org.json.JSONObject


class MainActivity : BaseActivity() {
    private  val REQUEST_CODE_DAILY = 0x1000
    private  val NOTIFY_ID_DAILY = 0x2000
    private  val CHANNEL_ID = "channel_new"

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
