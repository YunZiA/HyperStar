package com.yunzia.hyperstar.ui.screen.pagers.main

import android.net.Uri
import android.view.HapticFeedbackConstants
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.yunzia.hyperstar.ui.component.BaseButton
import com.yunzia.hyperstar.ui.component.PDropdown
import com.yunzia.hyperstar.ui.component.preference.widget.WarnDialogPreference
import com.yunzia.hyperstar.ui.component.preference.widget.PreferenceListPage
import com.yunzia.hyperstar.ui.component.preference.widget.ListPreference
import com.yunzia.hyperstar.ui.component.preference.widget.NavPreference
import com.yunzia.hyperstar.ui.component.preference.widget.Preference
import com.yunzia.hyperstar.ui.component.preference.widget.SwitchPreference
import com.yunzia.hyperstar.ui.component.preference.widget.itemGroup
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
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.SpinnerEntry
import top.yukonga.miuix.kmp.layout.DialogDefaults
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.WindowSpinnerPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape
import com.yunzia.hyperstar.LocalMainPagerState
import com.yunzia.hyperstar.LocalRebootDialogState
import com.yunzia.hyperstar.ui.component.preference.widget.SpinnerPreference
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.navigation.MainRoutes
import com.yunzia.hyperstar.ui.screen.pagers.main.home.AppEntryList.entries

@Composable
fun Settings(
    contentPadding: PaddingValues,
) {
    val pagerState = LocalMainPagerState.current
    val showReboot = LocalRebootDialogState.current
    val context = LocalContext.current
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    val view = LocalView.current
    val rebootStyle = activity.rebootStyle

    val errorDialog = remember { mutableStateOf(false) }
    val results: MutableState<Uri?> = remember { mutableStateOf(null) }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result ->
        if (result == null) return@rememberLauncherForActivityResult
        results.value = result
        errorDialog.value = !JBUtil.readGson(context, result)
        if (!errorDialog.value) {
            MiuiStrongToast.showStrongToast(context, context.getString(R.string.restore_success))
        }
        activity.recreate()
    }
    val launcher2 = rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument("application/json")) { result ->
        if (result == null) return@rememberLauncherForActivityResult
        results.value = result
        errorDialog.value = !JBUtil.saveToLocal(context, result)
    }

    PreferenceListPage(
        title = stringResource(R.string.settings_page_title),
        navController = navController,
        endIcon = {
            if (rebootStyle.intValue == 1 && pagerState.currentPage == 1) {
                RebootPup(showReboot)
            }

            ErrorDialog(errorDialog, results)
        },
        endClick = {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            showReboot.value = true
        },
        contentPadding = contentPadding,
    ) {
        itemGroup(R.string.show_title) {
            var hideIcon by remember { mutableStateOf(PreferencesUtil.getBoolean("is_hide_icon", false)) }
            SwitchPreference(
                title = stringResource(R.string.is_hide_icon_title),
                checked = hideIcon,
                onCheckedChange = {
                    hideIcon = it
                    PreferencesUtil.putBoolean("is_hide_icon", it)
                    activity.setLauncherIconHidden(it)
                }
            )
            NavPreference(
                title = stringResource(R.string.language),
                endText = getLanguage(),
                onClick = { navController.navigate(MainRoutes.Language) }
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
            NavPreference(
                title = stringResource(R.string.model_pager_setting),
                onClick = { navController.navigate(MainRoutes.Show) }
            )
        }

        itemGroup(context.getString(R.string.backup_restore)) {
            Preference(
                title = stringResource(R.string.backup_settings),
                onClick = { saveFile(activity, launcher2) }
            )
            Preference(
                title = stringResource(R.string.restore_settings),
                onClick = { openFile(activity, launcher) }
            )
            WarnDialogPreference(
                title = stringResource(R.string.clear_settings),
                warnTitle = stringResource(R.string.clear_settings_warning_title),
                warnDes = stringResource(R.string.clear_settings_warning_description)
            ) {
                clear(activity, context)
            }
        }

        itemGroup(context.getString(R.string.err_find)) {
            ChannelSpinner(
                title = stringResource(R.string.hook_channel),
                items = stringArrayResource(R.array.hook_channel_items),
                key = "is_Hook_Channel",
                defIndex = getSettingChannel(),
            ) {
                activity.recreate()
            }
            val logLevelOptions = stringArrayResource(R.array.log_level).toList()
            var logLevel by remember { mutableIntStateOf(SPUtils.getInt("log_level", 0)) }
            SpinnerPreference(
                title = stringResource(R.string.title_log_level),
                summary = stringResource(R.string.summary_log_level),
                entries = logLevelOptions,
                selectedIndex = logLevel,
                onSelectedIndexChange = {
                    logLevel = it
                    SPUtils.putInt("log_level", logLevel)
                }
            )
            NavPreference(
                title = stringResource(R.string.debug_message),
                onClick = { navController.navigate(MainRoutes.Message) }
            )
        }
    }
}

@Composable
fun ChannelSpinner(
    title: String,
    items: Array<String>,
    key: String,
    defIndex: Int,
    dialogButtonString: String = stringResource(R.string.cancel),
    modifier: Modifier = Modifier,
    popupModifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    startAction: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    showValue: Boolean = true,
    onSelectedIndexChange: ((Int) -> Unit)? = null,
) {
    val spinnerItems = mutableListOf<SpinnerEntry>()
    val selected = remember { mutableIntStateOf(SPUtils.getInt(key, defIndex)) }

    for (item in items) {
        spinnerItems.add(SpinnerEntry(title = item))
    }

    WindowSpinnerPreference(
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
    ) {
        selected.intValue = it + 1
        SPUtils.putInt(key, selected.intValue)
        onSelectedIndexChange?.invoke(it)
    }
}

@Composable
fun ErrorDialog(
    show: MutableState<Boolean>,
    value: MutableState<Uri?>
) {
    OverlayDialog(
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
            SelectionContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        colorScheme.secondaryContainer,
                        SmoothRoundedCornerShape(12.dp)
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
fun getLanguage(): String {
    val languageList = stringArrayResource(R.array.language_list).toList()
    val activity = LocalActivity.current as BaseActivity
    return languageList[activity.language.intValue]
}
