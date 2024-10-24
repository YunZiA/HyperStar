package com.yunzia.hyperstar

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.profileinstaller.ProfileInstaller
import com.yunzia.hyperstar.ui.base.BaseActivity
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.SPUtils
import com.yunzia.hyperstar.utils.Utils
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import java.io.Serializable


class MainActivity : BaseActivity() {

    var paddings = PaddingValues(0.dp)
    var state = State.Start

    private fun isModuleActive() : Boolean{
        return false;
    }

    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        val isRoot:Boolean = Utils.getRootPermission() == 0

        RootDialog(!isRoot)
        Log.d("ggc", "Greeting: $isRoot")
        if (colorMode != null) {
            App(this,colorMode)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        val state = savedInstanceState?.getString("state")
        if (state != null){
            this.state = State.valueOf(state)

        }
        val paddingData = savedInstanceState?.getSerializable("paddings",PaddingData::class.java)
        if (paddingData != null){
            paddings = paddingData.toPaddingValues()

        }
        ProfileInstaller.writeProfile(this)
        if (isModuleActive()){
            SPUtils.getInstance().init(this);

        }else{
            Toast.makeText(this,
                getString(R.string.not_activated_toast_description),Toast.LENGTH_SHORT).show()
        }
        showLauncherIcon(PreferencesUtil.getBoolean("is_hide_icon",false))

    }

    fun savePadding(
        padding: PaddingValues
    ){
        paddings = padding
        this.state = State.Recreate

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val paddingData = PaddingData(paddings.calculateTopPadding().value,  paddings.calculateBottomPadding().value)
        outState.putString("state",state.name)
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
fun RootDialog(showDialog: Boolean) {
    val showDialogs = remember{ mutableStateOf(showDialog)}
    if (showDialogs.value) {

        Dialog(
            onDismissRequest = { showDialogs.value = false },
        ) {
            Card(
                cornerRadius = 30.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 28.dp),
                insideMargin = DpSize(20.dp,20.dp)
            ) {
                Text(
                    text = stringResource(R.string.tips),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.no_root_description),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(23.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.sure),
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




