package com.yunzia.hyperstar

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.profileinstaller.ProfileInstaller
import com.yunzia.hyperstar.ui.base.BaseActivity
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.SPUtils
import com.yunzia.hyperstar.utils.Utils
import yunzia.utils.SystemProperties
import java.io.Serializable


class MainActivity : BaseActivity() {

    var paddings = PaddingValues(0.dp)
    var state = State.Start
    var isRecreate:Boolean = false

    private val WRITE_EXTERNAL_STORAGE_PERMISSION_CODE: Int = 1

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
        val state = savedInstanceState?.getString("state")
        if (state != null){
            this.state = State.valueOf(state)
        }
        val isRecreate = savedInstanceState?.getBoolean("isRecreate",true)
        if (isRecreate != null && isRecreate){
            this.isRecreate = true
        }
        val paddingData = savedInstanceState?.getSerializable("paddings",PaddingData::class.java)
        if (paddingData != null){
            paddings = paddingData.toPaddingValues()

        }
        ProfileInstaller.writeProfile(this)

        if (isModuleActive()){
            SPUtils.getInstance().init(this);

        }

        showLauncherIcon(PreferencesUtil.getBoolean("is_hide_icon",false))

    }

    fun savePadding(
        padding: PaddingValues
    ){
        paddings = padding
        this.state = State.Recreate

    }

    fun goManagerFileAccess():Boolean {

        //判断是否有管理外部存储的权限
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




    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val paddingData = PaddingData(paddings.calculateTopPadding().value,  paddings.calculateBottomPadding().value)
        outState.putString("state",state.name)
        outState.putBoolean("hide_root",true)
        outState.putSerializable("paddings",paddingData)
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

        // 在AndroidManifest.xml中为MainActivity定义了一个别名为MainActivity-Alias的activity，是默认启动activity、是点击桌面图标后默认程序入口
    }



}


enum class State{
    Start,Recreate
}

data class PaddingData(val top: Float, val bottom: Float) :
    Serializable {
    // 构造函数、getter/setter等

    fun toPaddingValues(): PaddingValues {
        return PaddingValues(0.dp, top.dp, 0.dp, bottom.dp)
    }

}




@Composable
fun VerDialog(showDialog: Boolean,context:Context) {
    val showDialogs = remember{ mutableStateOf(!showDialog)}

    val show = remember{ mutableStateOf(PreferencesUtil.getBoolean("no_root_waring",showDialog))}



}





