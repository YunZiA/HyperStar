package com.yunzia.hyperstar.ui.pagers

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.State
import com.yunzia.hyperstar.ui.base.BaseArrow
import com.yunzia.hyperstar.ui.base.PMiuixSuperDropdown
import com.yunzia.hyperstar.ui.base.PMiuixSuperSwitch
import com.yunzia.hyperstar.ui.base.SuperWarnDialogArrow
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.JBUtil
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.utils.getWindowSize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsPage(
    activity : MainActivity,
    navController: NavController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    colorMode: MutableState<Int>,
    showFPSMonitor: MutableState<Boolean>,
    enablePageUserScroll:MutableState<Boolean>,
) {

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result ->
        if (result == null) return@rememberLauncherForActivityResult
        JBUtil.readGson(context, result)
        activity.recreate()

    }
    val launcher2 = rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument("application/json")) { result ->
        if (result == null) return@rememberLauncherForActivityResult
        JBUtil.saveToLocal(context, result)
    }

    val language = remember { mutableIntStateOf(PreferencesUtil.getInt("app_language",0)) }

    val state = remember {
        mutableStateOf(activity.state)
    }
    val paddinged by remember { mutableStateOf(activity.paddings)}
    LaunchedEffect(padding) {
        if (state.value == State.Recreate){
            state.value = State.Start
        }

    }

    LazyColumn(
        modifier = Modifier.height(getWindowSize().height.dp),
        contentPadding =if (state.value == State.Recreate) PaddingValues(top = paddinged.calculateTopPadding()+14.dp, bottom = paddinged.calculateBottomPadding()+14.dp)
        else PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+14.dp),
        topAppBarScrollBehavior = topAppBarScrollBehavior
    ) {
        firstClasses(
            title = R.string.show_title
        ){
            PMiuixSuperSwitch(
                title = stringResource(R.string.is_hide_icon_title),
                key = "is_hide_icon"
            )
            PMiuixSuperDropdown(
                title = stringResource(R.string.language),
                option = R.array.language_list,
                selectedIndex = language.intValue,
                onSelectedIndexChange = {
                    language.intValue = it
                    PreferencesUtil.putInt("app_language",language.intValue)
                    activity.savePadding(padding)
                    activity.recreate()
                }
            )


            PMiuixSuperDropdown(
                title = stringResource(R.string.color_mode_title),
                option = R.array.color_mode_items,
                selectedIndex = colorMode.value,
                onSelectedIndexChange = {
                    colorMode.value = it
                    PreferencesUtil.putInt("color_mode",colorMode.value)
                }
            )

            PMiuixSuperSwitch(
                title = stringResource(R.string.show_FPS_Monitor_title),
                checked = showFPSMonitor.value,
                onCheckedChange = {
                    showFPSMonitor.value = it
                    PreferencesUtil.putBoolean("show_FPS_Monitor",showFPSMonitor.value)
                }
            )
            PMiuixSuperSwitch(
                title = stringResource(R.string.page_user_scroll_title),
                checked = enablePageUserScroll.value,
                onCheckedChange = {
                    enablePageUserScroll.value = it
                    PreferencesUtil.putBoolean("page_user_scroll",enablePageUserScroll.value)
                }
            )
            PMiuixSuperSwitch(
                title = stringResource(R.string.progress_effect_title),
                key = "is_progress_effect"
            )

        }
        classes(
            title = context.getString(R.string.backup_restore)
        ) {

            BaseArrow(
                title = stringResource(R.string.backup_settings),
                onClick = {
                    if (activity.goManagerFileAccess()){
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                        JBUtil.saveFile(launcher2,"HyperStar_backup_${dateFormat.format(Date())}.json")
                    }

                }
            )
            BaseArrow(
                title = stringResource(R.string.restore_settings),
                onClick = {
                    if (activity.goManagerFileAccess()){
                        JBUtil.openFile(launcher)

                    }

                }
            )

            SuperWarnDialogArrow(
                title = stringResource(R.string.clear_settings),
                warnTitle = stringResource(R.string.clear_settings_warning_title),
                warnDes = stringResource(R.string.clear_settings_warning_description)
            ){
                val b1 = PreferencesUtil.clearPreferences()
                val b2 = SPUtils.clearPreferences()
                if ( b1 && b2 ){
                    Toast.makeText(activity,
                        context.getString(R.string.clear_success),Toast.LENGTH_SHORT).show()
                    activity.recreate()
                }else{
                    if (b1 || b2) {
                        Toast.makeText(activity,
                            context.getString(R.string.partial_clear_successful),Toast.LENGTH_SHORT).show()
                        activity.recreate()
                    }else{
                        Toast.makeText(activity,
                            context.getString(R.string.clear_fail),Toast.LENGTH_SHORT).show()

                    }
                }
            }

        }

    }
}





