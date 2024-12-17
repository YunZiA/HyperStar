package com.yunzia.hyperstar.ui.pagers

import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.PagerList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.BaseArrow
import com.yunzia.hyperstar.ui.base.BaseButton
import com.yunzia.hyperstar.ui.base.PMiuixSuperDropdown
import com.yunzia.hyperstar.ui.base.PMiuixSuperSwitch
import com.yunzia.hyperstar.ui.base.SuperNavHostArrow
import com.yunzia.hyperstar.ui.base.SuperWarnDialogArrow
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.dialog.SuperCTDialogDefaults
import com.yunzia.hyperstar.ui.base.dialog.SuperXDialog
import com.yunzia.hyperstar.ui.base.dialog.SuperXPopupUtil.Companion.dismissXDialog
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.JBUtil
import com.yunzia.hyperstar.utils.JBUtil.saveFile
import com.yunzia.hyperstar.utils.PreferencesUtil
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import java.io.BufferedWriter
import java.io.OutputStreamWriter

@Composable
fun Settings(
    activity : MainActivity,
    navController: NavHostController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    colorMode: MutableState<Int>
) {


    val context = LocalContext.current
    val errorDialog = remember { mutableStateOf(false) }
    val results: MutableState<Uri?> = remember { mutableStateOf(null) }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result ->
        if (result == null) return@rememberLauncherForActivityResult
        results.value = result
        errorDialog.value = !JBUtil.readGson(context, result)
        activity.recreate()

    }
    val launcher2 = rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument("application/json")) { result ->
        if (result == null) return@rememberLauncherForActivityResult
        results.value = result
        errorDialog.value = !JBUtil.saveToLocal(context, result)
    }

    ErrorDialog(errorDialog,results)

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
fun ErrorDialog(
    show: MutableState<Boolean>,
    value: MutableState<Uri?>
) {

    SuperXDialog(
        title = stringResource(R.string.error_title),
        show = show,
        onDismissRequest = {}
    ) {
        Column(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 18.dp)
                .fillMaxWidth(),
        ) {
            Text(
                stringResource(R.string.send_developer),
                Modifier.padding(bottom = 8.dp),
                color = SuperCTDialogDefaults.summaryColor(),
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
                        SmoothRoundedCornerShape(12.dp, 0.5f)
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
                    dismissXDialog(show)
                }

            )
            Spacer(Modifier.width(12.dp))
            BaseButton(
                text = stringResource(R.string.sure),
                modifier = Modifier.weight(1f),
                submit = true,
                onClick = {
                    dismissXDialog(show)

                }

            )

        }

    }

}



@Composable
fun getLanguage():String{
    val languageList = stringArrayResource(R.array.language_list).toList()

    return languageList.get(PreferencesUtil.getInt("app_language",0))
}



