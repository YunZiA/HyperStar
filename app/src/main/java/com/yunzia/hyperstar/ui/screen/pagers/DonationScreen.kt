package com.yunzia.hyperstar.ui.screen.pagers

import android.content.ComponentName
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.BaseArrow
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.pager.NavPager
import com.yunzia.hyperstar.ui.component.itemGroup
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape
import yunzia.utils.AppUtils
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask


@Composable
fun DonationPage(
    navController: NavController,
    currentStartDestination: MutableState<String>,
) {
    val mContext = navController.context
    val appUtils = AppUtils(mContext)
    val apCode = "https://qr.alipay.com/fkx14314i5s3jbfx0rcwz2e"
    val tipsList = listOf(stringResource(R.string.qr_code_save),stringResource(R.string.donate_tip_one),stringResource(R.string.donate_tip_two))
    val tipIndex = remember { mutableIntStateOf(0) }
    val size = tipsList.size

    val coroutineScope= rememberCoroutineScope()
    val change = remember {  mutableStateOf(false) }

    DisposableEffect(Unit) {
        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                coroutineScope.launch {
                    change.value = true
                }
            }

        },6000,6000)
        onDispose {
            timer.cancel()
        }
    }

    val animatedTextAlpha = animateFloatAsState(
        targetValue = if (change.value) 0f else 1f,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearEasing
        ),
        finishedListener = {
            if (it == 0f){
                tipIndex.intValue = if ( ++tipIndex.intValue < size ) tipIndex.intValue  else  0
                change.value = false
            }
        }, label = ""
    )


    NavPager(
        activityTitle = stringResource(R.string.donation),
        navController = navController,
        parentRoute = currentStartDestination,
    ) {
        itemGroup(
            position = SuperGroupPosition.FIRST
        ){
            Text(
                text = tipsList[tipIndex.intValue],
                fontSize = 15.sp,
                color = colorScheme.onBackgroundVariant,
                modifier = Modifier
                    .padding(24.dp, 16.dp)
                    .alpha(animatedTextAlpha.value)
            )


        }
        donationClass(
            mContext,R.drawable.donate_alipay,R.drawable.alipay_icon,R.string.alipay
        ){
            BaseArrow(
                title = stringResource(R.string.go_scaner),
                summary = null,
                onClick = {

                    val uri = Uri.parse("alipays://platformapi/startapp?saId=10000007");
                    val intent = Intent(Intent.ACTION_VIEW, uri);
                    val componentName = intent.resolveActivity(mContext.packageManager)
                    if (componentName != null) {
                        Toast.makeText(mContext,
                            mContext.getString(R.string.thank_donation),
                            Toast.LENGTH_SHORT).show()
                        mContext.startActivity(intent)
                    } else {
                        Toast.makeText(mContext,
                            mContext.getString(R.string.no_alipay),
                            Toast.LENGTH_SHORT).show()
                    }

                }
            )
            BaseArrow(
                title = stringResource(R.string.quick_donation),
                summary = null,
                onClick = {
                    val intentFullUrl = "alipays://platformapi/startapp?appId=20000067&url=$apCode"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(intentFullUrl))
                    val componentName = intent.resolveActivity(mContext.packageManager)
                    if (componentName != null) {
                        Toast.makeText(mContext,
                            mContext.getString(R.string.thank_donation),
                            Toast.LENGTH_SHORT).show()
                        mContext.startActivity(intent)
                    } else {
                        Toast.makeText(mContext,
                            mContext.getString(R.string.no_alipay),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        donationClass(
            mContext,R.drawable.donate_wechat,R.drawable.wechat_icon,R.string.wechat
        ){
            BaseArrow(
                title = stringResource(R.string.go_scaner),
                summary = null,
                onClick = {
                    val intent = Intent()
                    intent.setComponent(ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"))
                    intent.putExtra("LauncherUI.From.Scaner.Shortcut", true)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.setAction("android.intent.action.VIEW")

                    if (appUtils.isAppInstalled("com.tencent.mm")) {
                        Toast.makeText(mContext,
                            mContext.getString(R.string.thank_donation),
                            Toast.LENGTH_SHORT).show()
                        mContext.startActivity(intent)
                    } else {
                        Toast.makeText(mContext,
                            mContext.getString(R.string.no_wechat),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}


private fun LazyListScope.donationClass(
    context:Context,
    imageRes: Int,
    appIcon:Int,
    appName:Int,
    content: @Composable (() -> Unit),
){

    itemGroup {

        Row(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(appIcon),
                contentDescription = stringResource(appName),
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = stringResource(appName),
                modifier = Modifier
                    .padding(start = 5.dp),
                fontSize = 17.sp,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )


        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            SaveImage(
                context = context,
                imageRes = imageRes,
                contentDescription = stringResource(appName),
                modifier = Modifier
                    .width(144.dp)
            )
        }
        Box(modifier = Modifier.fillMaxWidth().height(11.dp).padding(horizontal = 22.dp).padding(top = 10.dp).background(colorScheme.secondary))
        content()

    }
}

@Composable
private fun SaveImage(
    context:Context,
    imageRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
){

    val shape = G2RoundedCornerShape(10.dp)

    val hapticFeedback = LocalHapticFeedback.current
    Image(
        painter = painterResource(imageRes),
        contentDescription = contentDescription,
        modifier = modifier
            .clip(shape)
            .border(0.5.dp,colorScheme.secondary,shape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        val drawable = context.getDrawable(imageRes)!!
                        val bitmap = drawable.toBitmap()
                        saveImageToGallery(context, bitmap)
                    }
                )
            }
    )
}

private fun saveImageToGallery(
    context: Context,
    bitmap: Bitmap
) {
    val values = ContentValues()
    val timeFormat = SimpleDateFormat("MMddHHmmss", Locale.getDefault())
    val formattedTime = timeFormat.format(Date())
    values.put(MediaStore.Images.Media.DISPLAY_NAME, "${formattedTime}.jpg")
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    values.put(
        MediaStore.Images.Media.RELATIVE_PATH,
        Environment.DIRECTORY_PICTURES + "/HyperStar"
    )

    val contentResolver: ContentResolver = context.contentResolver

    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

    if (uri != null) {
        try {
            contentResolver.openOutputStream(uri).use { stream ->
                if (stream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    Toast.makeText(context,
                        context.getString(R.string.save_success), Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, context.getString(R.string.save_fail), Toast.LENGTH_SHORT).show()
        }
    }
}