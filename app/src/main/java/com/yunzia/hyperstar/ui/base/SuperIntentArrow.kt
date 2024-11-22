package com.yunzia.hyperstar.ui.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.yunzia.hyperstar.ui.base.navtype.PagersModel
import com.yunzia.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.extra.SuperArrow


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
            // 启动新的Activity
            navController.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    )
}

@Composable
fun SuperNavHostArrow(
    leftIcon:Int ? = null,
    title : String,
    summary : String ? = null,
    navController: NavController,
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
