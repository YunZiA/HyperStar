package com.yunzia.hyperstar.ui.pagers

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.PagerList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.BaseArrow
import com.yunzia.hyperstar.ui.base.PMiuixSuperDropdown
import com.yunzia.hyperstar.ui.base.PMiuixSuperSwitch
import com.yunzia.hyperstar.ui.base.SuperNavHostArrow
import com.yunzia.hyperstar.ui.base.SuperWarnDialogArrow
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.JBUtil
import com.yunzia.hyperstar.utils.PreferencesUtil
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior

@Composable
fun Settings(
    activity : MainActivity,
    navController: NavHostController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    colorMode: MutableState<Int>
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


    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        contentPadding =PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+14.dp),
        topAppBarScrollBehavior = topAppBarScrollBehavior
    ) {
        firstClasses(
            title = R.string.show_title
        ){
            PMiuixSuperSwitch(
                title = stringResource(R.string.is_hide_icon_title),
                key = "is_hide_icon"
            )
//            PMiuixSuperDropdown(
//                title = stringResource(R.string.language),
//                option = R.array.language_list,
//                selectedIndex = language.intValue,
//                onSelectedIndexChange = {
//                    language.intValue = it
//                    PreferencesUtil.putInt("app_language",language.intValue)
//                    activity.recreate()
//                }
//            )
            SuperNavHostArrow(
                title = stringResource(R.string.language),
                navController = navController,
                route = PagerList.LANGUAGE,
                rightText = getLanguage()
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

            SuperNavHostArrow(
                title = stringResource(R.string.model_pager_setting),
                navController = navController,
                route = PagerList.SHOW

            )

        }

        classes(
            title = context.getString(R.string.backup_restore)
        ) {

            BaseArrow(
                title = stringResource(R.string.backup_settings),
                onClick = {
                    JBUtil.saveFile(activity,launcher2)
                }
            )
            BaseArrow(
                title = stringResource(R.string.restore_settings),
                onClick = {
                    JBUtil.openFile(activity,launcher)

                }
            )

            SuperWarnDialogArrow(
                title = stringResource(R.string.clear_settings),
                warnTitle = stringResource(R.string.clear_settings_warning_title),
                warnDes = stringResource(R.string.clear_settings_warning_description)
            ){
                JBUtil.clear(activity,context)
            }

        }

    }
}



@Composable
fun getLanguage():String{
    val languageList = stringArrayResource(R.array.language_list).toList()

    return languageList.get(PreferencesUtil.getInt("app_language",0))
}



