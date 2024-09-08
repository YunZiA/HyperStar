package com.chaos.hyperstar

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.chaos.hyperstar.ui.base.BaseActivity
import com.chaos.hyperstar.ui.module.volume.VolumePager
import com.chaos.hyperstar.utils.PreferencesUtil
import com.chaos.hyperstar.utils.SPUtils
import com.chaos.hyperstar.utils.Utils
import top.yukonga.miuix.kmp.basic.MiuixButton
import top.yukonga.miuix.kmp.basic.MiuixCard
import top.yukonga.miuix.kmp.basic.MiuixText


class MainActivity : BaseActivity() {

    fun isModuleActive() : Boolean{
        return false;
    }

    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        val isRoot:Boolean = Utils.getRootPermission() == 0
        RootDialog(!isRoot)
        Log.d("ggc", "Greeting: "+isRoot)
        if (colorMode != null) {
            App(this,colorMode)
        }
    }

    override fun initData() {
        if (isModuleActive()){
            SPUtils.getInstance().init(this);
            SPUtils.setInt("control_center_universal_corner_radius",resources.getDimensionPixelSize(R.dimen.control_center_universal_corner_radius))

        }else{
            Toast.makeText(this,"模块未激活!",Toast.LENGTH_SHORT).show()
        }
        PreferencesUtil.getInstance().init(this)
        showLauncherIcon(PreferencesUtil.getBoolean("is_hide_icon",false))

    }


    fun showLauncherIcon(isHide: Boolean) {
        val packageManager = this.packageManager
        val show = if (isHide) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        packageManager.setComponentEnabledSetting(
            getAliseComponentName(),
            show,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun getAliseComponentName(): ComponentName {
        return ComponentName(this@MainActivity, "com.chaos.hyperstar.MainActivityAlias")

        // 在AndroidManifest.xml中为MainActivity定义了一个别名为MainActivity-Alias的activity，是默认启动activity、是点击桌面图标后默认程序入口
    }

}



@Composable
fun RootDialog(showDialog: Boolean) {
    val showDialogs = remember{ mutableStateOf(showDialog)}
    if (showDialogs.value) {

        Dialog(
            onDismissRequest = { showDialogs.value = false },
        ) {
            MiuixCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 28.dp)
            ) {
                MiuixText(
                    text = "提示",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(20.dp))
                MiuixText(
                    text = "未获取Root权限",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(23.dp))

                MiuixButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "确认",
                    submit = true,
                    cornerRadius = 14.dp,
                    onClick = {
                        //dismissDialog()
                        showDialogs.value = false
                    }
                )


            }

        }
    }
}







@Composable
fun MySwitch(
    modifier: Modifier = Modifier,
    text: String
    ) {
        var checkedButton by remember { mutableStateOf(false) }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    checkedButton = !checkedButton
                    //onCheckedChange(checkedButton)
                }
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
            )
            Switch(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp),
                checked = checkedButton,
                onCheckedChange = { checkedButton = it }
            )
        }
}




