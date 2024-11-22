package com.yunzia.hyperstar.ui.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.navtype.PagersModel
import com.yunzia.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion


@Composable
fun BaseArrow(
    leftIcon:Int ? = null,
    title : String,
    summary : String ? = null,
    onClick: (() -> Unit)? = null
) {


    SuperArrow(
        modifier = Modifier,
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
        insideMargin = PaddingValues(24.dp, 16.dp),
        summary = summary,
        onClick = onClick
    )
}

@Composable
fun SuperIntentArrow(
    leftIcon : Int? = null,
    title : String,
    navController: NavController,
    summary : String? = null,
    url : String
)
{

    SuperArrow(
        modifier = Modifier,
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
        insideMargin = PaddingValues(24.dp, 16.dp),
        summary = summary,
        onClick = {

            navController.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    )
}

@Composable
fun SuperNavHostArrow(
    leftIcon:Int ? = null,
    title : String,
    summary : String ? = null,
    navController : NavHostController,
    route: String,
) {


    SuperArrow(
        modifier = Modifier,
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
        insideMargin = PaddingValues(24.dp, 16.dp),
        summary = summary,
        onClick = {

            navController.navigate(route)
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
    rightDo: @Composable (String)->String = {it}
) {

    val pagersModel = Gson().toJson(PagersModel(title, key))

    val style = if ( key == ""){
        null
    }else{
        rightDo(SPUtils.getString(key,def))
    }
    val routes = "$route/${Uri.encode(pagersModel)}"


    SuperArrow(
        modifier = Modifier,
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
        rightText = style,
        insideMargin = PaddingValues(24.dp, 16.dp),
        summary = summary,
        onClick = {

            navController.navigate(routes)
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
        modifier = Modifier,
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
        insideMargin = PaddingValues(24.dp, 16.dp),
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

    val show = remember { mutableStateOf(false) }

    SuperArrow(
        modifier = Modifier,
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
        insideMargin = PaddingValues(24.dp, 16.dp),
        summary = summary,
        onClick = {
            show.value = true
        }
    )

    SuperDialog(
        title = warnTitle,
        show = show,
        onDismissRequest = {

            MiuixPopupUtil.dismissDialog(show)

        }
    ) {

        Text(
            warnDes,
            Modifier.padding(horizontal = 5.dp)
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
                    MiuixPopupUtil.dismissDialog(show)
                }

            )

            Spacer(Modifier.width(20.dp))
            BaseButton(
                text = stringResource(R.string.sure),
                modifier = Modifier.weight(1f),
                submit = true,
                onClick = {
                    MiuixPopupUtil.dismissDialog(show)
                    onSure()

                }

            )

        }



    }



}
