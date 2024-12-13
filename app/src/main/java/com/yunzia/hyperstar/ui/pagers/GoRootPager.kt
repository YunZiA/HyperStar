package com.yunzia.hyperstar.ui.pagers

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.drawablepainter.DrawablePainter
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.NavPager
import com.yunzia.hyperstar.ui.base.enums.EventState
import com.yunzia.hyperstar.ui.base.modifier.bounceClick
import com.yunzia.hyperstar.ui.base.modifier.bounceScale
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.media.app.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme


@SuppressLint("QueryPermissionsNeeded")
private fun getRootManagerInfo(
    context: Context,
    root: List<AppInfo>, noRoot: MutableState<List<AppInfo>?>
): ArrayList<AppInfo> {
    val appBeanList: ArrayList<AppInfo> = ArrayList<AppInfo>()
    val packageManager = context.packageManager

    val rootList = root.toMutableList()
    root.forEach {
        try {
            val packageInfo = packageManager.getPackageInfo(it.packageName, 0).applicationInfo!!

            val app_name = packageManager.getApplicationLabel(packageInfo).toString()
            val package_name = packageInfo.packageName
            val app_icon = packageManager.getApplicationIcon(packageInfo)
            //val launch = packageManager.getLaunchIntentForPackage(package_name)

            val bean = AppInfo()
            bean.apply {
                label = app_name
                packageName = package_name
                icon = app_icon
                launch = packageManager.getLaunchIntentForPackage(package_name)
            }
            appBeanList.add(bean)
            rootList.remove(it)

            Log.d("AppPackageInfo", "App found: $app_name (Package: $package_name)")
        } catch (e: PackageManager.NameNotFoundException) {
            // 未找到应用

            Log.d("AppPackageInfo", "App not found for package: $it.packageName")
        }
    }
    noRoot.value = rootList




    return appBeanList
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun GoRootPager(
    navController: NavHostController
) {
    val mContext = LocalContext.current
    ///val rootList = getRootManagerInfo(mContext)
    val root = listOf(
        AppInfo(
            label = "Magisk",
            packageName = "com.topjohnwu.magisk",
            icon = mContext.resources.getDrawable(R.drawable.magisk)
        ),
        AppInfo(
            label = "Alpha",
            packageName = "io.github.vvb2060.magisk",
            icon =  mContext.resources.getDrawable(R.drawable.alpha)
        ),
        AppInfo(
            label = "Kitsune Mask",
            packageName = "io.github.huskydg.magisk",
            icon =  mContext.resources.getDrawable(R.drawable.kitsune_mask)
        ),
        AppInfo(
            label = "KernelSU",
            packageName = "me.weishu.kernelsu",
            icon =  mContext.resources.getDrawable(R.drawable.kernelsu)
        ),
        AppInfo(
            label = "APatch",
            packageName = "me.bmax.apatch",
            icon =  mContext.resources.getDrawable(R.drawable.apatch)
        )
    )

    val noRoot = remember { mutableStateOf<List<AppInfo>?>(null) }

    val rootList = remember { mutableStateOf<ArrayList<AppInfo>?>(null) }


    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                getRootManagerInfo(mContext,root,noRoot)
            }
            rootList.value = result
        }
    }


    NavPager(
        activityTitle = stringResource(R.string.quick_authorization),
        navController = navController,
    ) {

        item {
            Text(
                stringResource(R.string.installed),
                modifier = Modifier
                    .padding(horizontal = 36.dp)
                    .padding(top = 0.dp),
                color = colorResource(R.color.class_name_color),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        rootList.value?.forEach {
            item {
                AppItem(mContext,it)
            }
        }

        item {
            Text(
                stringResource(R.string.uninstalled),
                modifier = Modifier
                    .padding(horizontal = 36.dp)
                    .padding(top = 18.dp),
                color = colorScheme.onBackgroundVariant,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        noRoot.value?.forEach {

            item {
                AppItem(mContext,it)
            }

        }

    }

}


@Composable
private fun AppItem(
    mContext: Context,
    app: AppInfo,
){
    val label = app.label

    val view = LocalView.current
    val eventState = remember { mutableStateOf(EventState.Idle) }
    //val scale by animateFloatAsState(if (eventState == EventState.Pressed) 0.90f else 1f)

    val isUsed = app.launch != null

    val go = if (isUsed) Modifier
        .bounceClick(eventState)
        .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            startAppByPackageName(mContext, app)
        }
    else{
        Modifier
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(top = 10.dp)
            .bounceScale(eventState),
        color = colorScheme.surfaceVariant
    ) {

        Row(

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .then(go)
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 12.dp)
                    .padding(vertical = 16.dp)
            ){
                app.icon?.let { icon ->
                    Image(
                        modifier = Modifier
                            .size(40.dp),
                        painter = DrawablePainter(icon),
                        contentDescription = label
                    )
                }
                if (app.icon ==  null){
                    Log.d("ggc","app.icon is null ")

                }

            }
            Text(
                text = label,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
                    .align(Alignment.CenterVertically),
                color = if (isUsed) colorScheme.onBackground else colorScheme.disabledOnSecondaryVariant
            )
            Image(
                modifier = Modifier
                    .width(40.dp)
                    .padding(vertical = 16.dp)
                    .padding(end = 16.dp),
                imageVector = ImageVector.vectorResource(R.drawable.arrow_right),
                //MiuixIcons.ArrowRight,
                contentDescription = null,
                colorFilter = ColorFilter.tint(if (isUsed) colorScheme.onBackground else colorScheme.disabledOnSecondaryVariant),
            )


        }
    }
}
private fun startAppByPackageName(context: Context, app: AppInfo) {
    val intent = app.launch!!

    val resolveInfo = context.packageManager.resolveActivity(intent, 0)
    if (resolveInfo != null) {
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // 如果找不到可以处理Intent的活动，则捕获此异常
            Log.e("AppStarter", "Unable to start app with package name: ${app.packageName}", e)
        }
    } else {
        // 如果没有找到任何匹配的活动，则记录或处理此情况
        Log.e("AppStarter", "No activity found to handle intent for package: ${app.packageName}")
    }
}
