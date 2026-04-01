package com.yunzia.hyperstar.ui.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import com.yunzia.hyperstar.ui.navigation.Navigator
import com.yunzia.hyperstar.ui.navigation.Route
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.DialogDefaults
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme


@Composable
fun BaseArrow(
    leftIcon:Int ? = null,
    title : String,
    summary : String ? = null,
    onClick: (() -> Unit)? = null
) {

    val click = remember { mutableStateOf(false) }
    SuperArrow(
        modifier = Modifier.bounceAnim {
            if (click.value){
                onClick?.invoke()
            }
            click.value = false
        },
        startAction = if (leftIcon != null){ {
            Row {
                Image(
                    painter = painterResource(leftIcon),
                    contentDescription = title,
                    modifier = Modifier.size(35.dp)
                )
                Spacer(Modifier.width(16.dp))
            }
        } }else{ null },
        title = title,
        summary = summary,
        onClick = {
            click.value = true
        }
    )
}

@Composable
fun SuperIntentArrow(
    leftIcon : Int? = null,
    title : String,
    summary : String? = null,
    context: Context,
    url : Array<String>
) {

    val click = remember { mutableStateOf(false) }
    SuperArrow(
        modifier = Modifier.bounceAnim {
            if (click.value){
                url.forEach {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
                        return@forEach
                    }catch (e: Exception){
                    }

                }
            }
            click.value = false
        },
        startAction = if (leftIcon != null){ {
            Row {
                Image(
                    painter = painterResource(leftIcon),
                    contentDescription = title,
                    modifier = Modifier.size(35.dp)
                )
                Spacer(Modifier.width(16.dp))
            }
        } }else{ null },
        title = title,
        summary = summary,
        onClick = {
            click.value = true
        }
    )
}

@Composable
fun SuperIntentArrow(
    leftIcon : Int? = null,
    title : String,
    summary : String? = null,
    context: Context,
    url : String
) {

    SuperIntentArrow(
        leftIcon  = leftIcon,
        title = title,
        summary  = summary,
        context = context,
        url = arrayOf(url)
    )
}

@Composable
fun SuperNavHostArrow(
    leftIcon:Int ? = null,
    title: String,
    summary: String ? = null,
    endText: String? = null,
    navController: Navigator,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    route: Route,
) {

    val click = remember { mutableStateOf(false) }
    SuperArrow(
        modifier = Modifier.bounceAnim{
            if (click.value){
                navController.navigate(route)
            }
            click.value = false
        },
        startAction = if (leftIcon != null){ {
            Row {
                Image(
                    painter = painterResource(leftIcon),
                    contentDescription = title,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(Modifier.width(16.dp))
            }
        } }else{ null },
        title = title,
        insideMargin = insideMargin,
        summary = summary,
        endActions = {
            endText?.let {
                Text(
                    text = it,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f, fill = false),
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = colorScheme.onSurfaceVariantActions,
                    textAlign = TextAlign.End,
                )
            } },
        onClick = {
            click.value = true
        }
    )
}


@Composable
fun SuperNavHostArrow(
    leftIcon: Painter,
    title: String,
    summary: String ? = null,
    rightText: String? = null,
    navController: Navigator,
    route: Route,
) {


    val click = remember { mutableStateOf(false) }
    SuperArrow(
        modifier = Modifier.bounceAnim{
            if (click.value){
                navController.navigate(route)
            }
            click.value = false
        },
        startAction = {
            Row {
                Image(
                    painter = leftIcon,
                    contentDescription = title,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(Modifier.width(16.dp))
            }
        },
        title = title,
        summary = summary,
        endActions = {
            rightText?.let {
                Text(
                    text = it,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f, fill = false),
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = colorScheme.onSurfaceVariantActions,
                    textAlign = TextAlign.End,
                )
            } },
        onClick = {
            click.value = true
        }
    )
}

@Composable
fun SuperActivityArrow(
    leftIcon:Int ? = null,
    title : String,
    summary : String ? = null,
    activity: Class<*> ,
    context: Context
) {

    SuperArrow(
        modifier = Modifier.bounceAnim(),
        startAction = if (leftIcon != null){ {
            Row {
                Image(
                    painter = painterResource(leftIcon),
                    contentDescription = title,
                    modifier = Modifier.size(35.dp)
                )
                Spacer(Modifier.width(16.dp))
            }
        } }else{ null },
        title = title,
        summary = summary,
        onClick = {
            // 启动新的Activity
            context.startActivity(Intent(context, activity))
        }
    )
}

@Composable
fun SuperWarnDialogArrow(
    leftIcon : Int? = null,
    title : String,
    summary : String? = null,
    warnTitle:String? = stringResource(R.string.warning),
    warnDes:String = "",
    onSure:()->Unit
) {

    val click = remember { mutableStateOf(false) }
    val show = remember { mutableStateOf(false) }

    SuperArrow(
        modifier = Modifier.bounceAnim{
            if (click.value){
                show.value = true
            }
            click.value = false
        },
        startAction = if (leftIcon != null){ {
            Row {
                Image(
                    painter = painterResource(leftIcon),
                    contentDescription = title,
                    modifier = Modifier.size(35.dp)
                )
                Spacer(Modifier.width(16.dp))
            }
        } }else{ null },
        title = title,
        summary = summary,
        onClick = {
            click.value = true
        }
    )

    SuperDialog(
        title = warnTitle,
        show = show.value,
        onDismissRequest = {
            show.value = false

        }
    ) {

        Text(
            warnDes,
            Modifier
                .padding(horizontal = 5.dp)
                .padding(top = 8.dp, bottom = 24.dp),
            color = DialogDefaults.summaryColor(),
            textAlign = TextAlign.Start,
            fontSize = 16.sp
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BaseButton(
                text = stringResource(R.string.cancel),
                modifier = Modifier.weight(1f),
                onClick = {
                    show.value = false
                }

            )

            Spacer(Modifier.width(20.dp))
            BaseButton(
                text = stringResource(R.string.sure),
                modifier = Modifier.weight(1f),
                submit = true,
                onClick = {
                    show.value = false
                    onSure()

                }

            )

        }



    }



}
