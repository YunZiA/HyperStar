package com.yunzia.hyperstar.ui.screen.pagers.main

import android.net.Uri
import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.BaseActivity
import com.yunzia.hyperstar.ui.component.BaseArrow
import com.yunzia.hyperstar.ui.component.BaseButton
import com.yunzia.hyperstar.ui.component.PDropdown
import com.yunzia.hyperstar.ui.component.PMiuixSuperSwitch
import com.yunzia.hyperstar.ui.component.SuperNavHostArrow
import com.yunzia.hyperstar.ui.component.SuperWarnDialogArrow
import com.yunzia.hyperstar.ui.component.XDropdown
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.ui.miuiStrongToast.MiuiStrongToast
import com.yunzia.hyperstar.utils.JBUtil
import com.yunzia.hyperstar.utils.JBUtil.clear
import com.yunzia.hyperstar.utils.JBUtil.openFile
import com.yunzia.hyperstar.utils.JBUtil.saveFile
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.utils.getSettingChannel
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import com.kyant.shapes.RoundedRectangle
import com.yunzia.hyperstar.LocalMainPagerState
import com.yunzia.hyperstar.LocalRebootDialogState
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.navigation.MainRoutes
import dev.chrisbanes.haze.rememberHazeState
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.SpinnerEntry
import top.yukonga.miuix.kmp.extra.DialogDefaults
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.WindowSpinner
import top.yukonga.miuix.kmp.icon.extended.More

@Composable
fun Settings(
    contentPadding: PaddingValues,
) {
    val hazeState = rememberHazeState()
    val pagerState = LocalMainPagerState.current
    val showReboot = LocalRebootDialogState.current

    val context = LocalContext.current
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity

    val view = LocalView.current
    val rebootStyle = activity.rebootStyle

    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val errorDialog = remember { mutableStateOf(false) }
    val results: MutableState<Uri?> = remember { mutableStateOf(null) }


    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result ->
        if (result == null) return@rememberLauncherForActivityResult
        results.value = result
        errorDialog.value = !JBUtil.readGson(context, result)
        if (!errorDialog.value){
            MiuiStrongToast.showStrongToast(context, context.getString(R.string.restore_success))
        }
        activity.recreate()

    }
    val launcher2 = rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument("application/json")) { result ->
        if (result == null) return@rememberLauncherForActivityResult
        results.value = result
        errorDialog.value = !JBUtil.saveToLocal(context, result)
    }

    Log.d("SettingsPager", "Settings: init")

    Scaffold(
        modifier = Modifier,
        popupHost = { },
        topBar = {
            TopAppBar(
                modifier = Modifier.showBlur(hazeState),
                color = Color.Transparent,
                title = stringResource(R.string.settings_page_title),
                scrollBehavior = topAppBarScrollBehavior,
                actions = {
                    if (rebootStyle.intValue == 1 && pagerState.currentPage == 1){
                        RebootPup(showReboot)
                    }
                    IconButton(
                        modifier = Modifier.padding(end = 12.dp),
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            showReboot.value = true
                        }
                    ) {
                        Icon(
                            imageVector = MiuixIcons.More,
                            contentDescription = "restart",
                            tint = colorScheme.onBackground
                        )
                    }
                }
            )


        }
    ) { padding ->

        ErrorDialog(errorDialog, results)

        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .blur(hazeState)
                .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 14.dp,
                bottom = contentPadding.calculateBottomPadding() + 14.dp
            ),
        ) {
            itemGroup(
                title = R.string.show_title
            ) {
                PMiuixSuperSwitch(
                    title = stringResource(R.string.is_hide_icon_title),
                    key = "is_hide_icon"
                ){
                    activity.setLauncherIconHidden(it)
                }
                SuperNavHostArrow(
                    title = stringResource(R.string.language),
                    navController = navController,
                    route = MainRoutes.Language,
                    endText = getLanguage(),
                )
                PDropdown(
                    title = stringResource(R.string.color_mode_title),
                    option = R.array.color_mode_items,
                    selectedIndex = activity.colorMode.intValue,
                    onSelectedIndexChange = {
                        activity.colorMode.intValue = it
                        PreferencesUtil.putInt("color_mode", activity.colorMode.intValue)
                    }
                )

                SuperNavHostArrow(
                    title = stringResource(R.string.model_pager_setting),
                    navController = navController,
                    route = MainRoutes.Show

                )

            }

            itemGroup(
                title = context.getString(R.string.backup_restore)
            ) {

                BaseArrow(
                    title = stringResource(R.string.backup_settings),
                    onClick = {
                        saveFile(activity, launcher2)
                    }
                )
                BaseArrow(
                    title = stringResource(R.string.restore_settings),
                    onClick = {
                        openFile(activity, launcher)
                    }
                )

                SuperWarnDialogArrow(
                    title = stringResource(R.string.clear_settings),
                    warnTitle = stringResource(R.string.clear_settings_warning_title),
                    warnDes = stringResource(R.string.clear_settings_warning_description)
                ) {
                    clear(activity, context)
                }

            }

            itemGroup(
                title = context.getString(R.string.err_find)
            ) {

                ChannelSpinner(
                    title = stringResource(R.string.hook_channel),
                    items = stringArrayResource(R.array.hook_channel_items),
                    key = "is_Hook_Channel",
                    defIndex = getSettingChannel(),
                ) {
                    activity.recreate()
                }
                XDropdown(
                    title = stringResource(R.string.title_log_level),
                    summary = stringResource(R.string.summary_log_level),
                    dfOpt = 0,
                    option = R.array.log_level,
                    key = "log_level"
                )

                SuperNavHostArrow(
                    title = stringResource(R.string.debug_message),
                    navController = navController,
                    route = MainRoutes.Message
                )
            }

        }
    }

}


@Composable
fun ChannelSpinner(
    title: String,
    items: Array<String>,
    key : String,
    defIndex:Int,
    dialogButtonString: String = stringResource(R.string.cancel),
    modifier: Modifier = Modifier,
    popupModifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    startAction: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    showValue: Boolean = true,
    onSelectedIndexChange: ((Int) -> Unit)?=null,
) {

    val spinnerItems = mutableListOf<SpinnerEntry>()
    val selected = remember { mutableIntStateOf(SPUtils.getInt(key,defIndex))}

    for (item in items){
        spinnerItems.add(SpinnerEntry(title = item))
    }

    WindowSpinner(
        title = title,
        items = spinnerItems,
        selectedIndex = selected.intValue - 1,
        dialogButtonString = dialogButtonString,
        modifier = modifier,
        popupModifier = popupModifier,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = startAction,
        enabled = enabled,
        showValue = showValue,
        //onSelectedIndexChange = onSelectedIndexChange
    ) {
        selected.intValue = it + 1

        Log.d("", "$key: ${selected.intValue}")
        SPUtils.putInt(key,selected.intValue)
        onSelectedIndexChange?.invoke(it)

    }
}

@Composable
fun ErrorDialog(
    show: MutableState<Boolean>,
    value: MutableState<Uri?>
) {

    SuperDialog(
        title = stringResource(R.string.error_title),
        show = show.value,
        onDismissRequest = {}
    ) {
        Column(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 18.dp)
                .fillMaxWidth(),
        ) {
            Text(
                stringResource(R.string.send_developer),
                modifier = Modifier.padding(bottom = 8.dp),
                color = DialogDefaults.summaryColor(),
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                style = TextStyle(textIndent = TextIndent(7.sp))
            )
            SelectionContainer (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        colorScheme.secondaryContainer,
                        RoundedRectangle(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    "${value.value?.pathSegments}",
                    Modifier,
                    color = Color.Red,
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp
                )

            }



        }

        Row {
            BaseButton(
                text = stringResource(R.string.cancel),
                modifier = Modifier.weight(1f),
                onClick = {
                    show.value = false
                }

            )
            Spacer(Modifier.width(12.dp))
            BaseButton(
                text = stringResource(R.string.sure),
                modifier = Modifier.weight(1f),
                submit = true,
                onClick = {
                    show.value = false

                }

            )

        }

    }

}



@Composable
fun getLanguage():String{
    val languageList = stringArrayResource(R.array.language_list).toList()

    val activity = LocalActivity.current as BaseActivity

    return languageList[activity.language.intValue]
}



