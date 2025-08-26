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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import com.yunzia.hyperstar.ui.component.nav.PagersModel
import com.yunzia.hyperstar.ui.component.nav.nav
import com.yunzia.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDialogDefaults


@Composable
fun BaseArrow(
    leftIcon:Int ? = null,
    title : String,
    summary : String ? = null,
    onClick: (() -> Unit)? = null
) {

    val click = remember { mutableStateOf(false) }
    SuperArrow(
        modifier = Modifier.bounceAnim{
            if (click.value){
                onClick?.invoke()
            }
            click.value = false
        },
        leftAction = if (leftIcon != null){ {
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
    navController: NavController,
    summary : String? = null,
    url : String
) {

    val click = remember { mutableStateOf(false) }
    SuperArrow(
        modifier = Modifier.bounceAnim{
            if (click.value){
                navController.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            click.value = false
        },
        leftAction = if (leftIcon != null){ {
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
fun SuperNavHostArrow(
    leftIcon:Int ? = null,
    title : String,
    summary : String ? = null,
    rightText: String? = null,
    navController : NavHostController,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    route: String,
) {


    val click = remember { mutableStateOf(false) }
    SuperArrow(
        modifier = Modifier.bounceAnim{
            if (click.value){
                navController.nav(route)
            }
            click.value = false
        },
        leftAction = if (leftIcon != null){ {
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
        rightText = rightText,
        onClick = {
            click.value = true
        }
    )
}


@Composable
fun SuperNavHostArrow(
    leftIcon:Painter,
    title : String,
    summary : String ? = null,
    rightText: String? = null,
    navController : NavHostController,
    route: String,
) {

    val click = remember { mutableStateOf(false) }
    SuperArrow(
        modifier = Modifier.bounceAnim{
            if (click.value){
                navController.nav(route)
            }
            click.value = false
        },
        leftAction = {
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
        rightText = rightText,
        onClick = {
            click.value = true
        }
    )
}

@Composable
fun SuperArgNavHostArrow(
    leftIcon:Int ? = null,
    title : String,
    summary : String ? = null,
    key : String = "",
    def:String = "null",
    navController : NavHostController,
    route: String,
    rightDo: @Composable (String) -> String = {it}
) {

    val pagersModel = Gson().toJson(PagersModel(title, key))

    val style = if ( key == ""){
        null
    }else{
        rightDo(SPUtils.getString(key,def))
    }
    val routes = "$route?${Uri.encode(pagersModel)}"

    val click = remember { mutableStateOf(false) }

    SuperArrow(
        modifier = Modifier.bounceAnim{
            if (click.value){
                navController.nav(routes)
            }
            click.value = false
        },
        leftAction = if (leftIcon != null){ {
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
        rightText = style,
        summary = summary,
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
        leftAction = if (leftIcon != null){ {
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
        leftAction = if (leftIcon != null){ {
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
        show = show,
        onDismissRequest = {
            show.value = false

        }
    ) {

        Text(
            warnDes,
            Modifier
                .padding(horizontal = 5.dp)
                .padding(top = 8.dp, bottom = 24.dp),
            color = SuperDialogDefaults.summaryColor(),
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
