package com.yunzia.hyperstar.ui.pagers.dialog

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.pm.ResolveInfo
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.BaseButton
import com.yunzia.hyperstar.ui.base.dialog.SuperCTDialogDefaults
import com.yunzia.hyperstar.ui.base.dialog.SuperXDialog
import com.yunzia.hyperstar.ui.base.dialog.SuperXPopupUtil.Companion.dismissXDialog
import com.yunzia.hyperstar.utils.PreferencesUtil
import top.yukonga.miuix.kmp.basic.Text


fun checkActivityExists(context: Context, packageName: String, className: String): Boolean {
    val intent = Intent()
    intent.setComponent(ComponentName(packageName, className))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 允许从非 Activity 上下文（如 Service 或 BroadcastReceiver）启动 Activity

    val packageManager = context.packageManager

    val resolveInfo: ResolveInfo? = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)

    return resolveInfo != null
}

fun checkApplication(context: Context, packageName: String?): Boolean {
    if (packageName == null || "" == packageName) {
        return false
    }

    try {
        val info: ApplicationInfo = context.packageManager.getApplicationInfo(
            packageName,
            0
        )
        return true
    } catch (e: NameNotFoundException) {
        return false
    }
}

@Composable
fun ActiveDialog(
    show: MutableState<Boolean>,
    navController: NavHostController

) {

    val mContext = navController.context

    val packageName = "org.lsposed.manager"
    val className = "org.lsposed.manager.ui.activity.MainActivity"

    val go = checkApplication(mContext,packageName)

    val intent = Intent().apply {
        setClassName(packageName,className)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }


    SuperXDialog(
        title = stringResource(R.string.tips),
        show = show,
        onDismissRequest = {}
    ) {

        Text(
            stringResource(R.string.not_activated_toast_description),
            Modifier
                .padding(horizontal = 5.dp)
                .padding(top = 8.dp, bottom = 24.dp)
                .fillMaxWidth(),
            color = SuperCTDialogDefaults.summaryColor(),
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
        BaseButton(
            text = stringResource(R.string.cancel),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                dismissXDialog(show)
                PreferencesUtil.removePreferences("no_active_waring")
            }

        )
        Spacer(Modifier.height(12.dp))
        BaseButton(
            text = stringResource(R.string.no_warning),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                dismissXDialog(show)
                PreferencesUtil.putBoolean("no_active_waring",false)

            }

        )
        if (go){
            Spacer(Modifier.height(12.dp))
            BaseButton(
                text = stringResource(R.string.active_faster),
                modifier = Modifier.fillMaxWidth(),
                submit = true,
                onClick = {
                    dismissXDialog(show)
                    mContext.startActivity(intent)

                }

            )

        }

    }



}