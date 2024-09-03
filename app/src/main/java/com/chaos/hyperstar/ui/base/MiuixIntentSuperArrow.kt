package com.chaos.hyperstar.ui.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.MiuixSuperArrow


@Composable
fun MiuixIntentSuperArrow(
    leftIcon : Int? = null,
    title : String,
    summary : String? = null,
    url : String,
    activity: ComponentActivity
)
{
    MiuixSuperArrow(
        leftAction = if (leftIcon != null){ {
            Image(
                painter = painterResource(leftIcon),
                contentDescription = title,
                modifier = Modifier.size(35.dp)
            )
        } }else{ null },
        title = title,
        summary = summary,
        onClick = {
            // 启动新的Activity
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    )
}
@Composable
fun MiuixActivitySuperArrow(
    leftIcon:Int ? = null,
    title : String,
    summary : String ? = null,
    activity: Class<*> ,
    context: Context
) {
    MiuixSuperArrow(
        leftAction = if (leftIcon != null){ {
            Image(
                painter = painterResource(leftIcon),
                contentDescription = title,
                modifier = Modifier.size(35.dp)
            )
        } }else{ null },
        title = title,
        summary = summary,
        onClick = {
            // 启动新的Activity
            context.startActivity(Intent(context, activity))
        }
    )
}
