package com.yunzia.hyperstar.ui.pagers.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.dialog.TopNotification
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.Utils
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.Box
import yunzia.utils.SystemProperties

@Composable
fun FirstDialog(
    activity: MainActivity,
    navController: NavHostController

) {
    if (activity.isRecreate) return

    val errVersion = (SystemProperties.getInt("ro.mi.os.version.code", 1) != 2)

    val verShow = remember{ mutableStateOf(PreferencesUtil.getBoolean("ver_waring",errVersion))}
    val rootShow = remember{ mutableStateOf(PreferencesUtil.getBoolean("no_root_waring",(Utils.getRootPermission() != 0)))}
    val activeShow = remember{ mutableStateOf(PreferencesUtil.getBoolean("no_active_waring",!activity.isModuleActive()))}

    val secShow = remember { mutableStateOf(false) }
    val thirdShow = remember { mutableStateOf(false) }
    val forthShow = remember { mutableStateOf(true) }

    LaunchedEffect(verShow.value) {
        delay(260)
        if (!verShow.value){
            secShow.value = true
        }

    }
    LaunchedEffect(activeShow.value) {
        delay(260)
        if (!activeShow.value){
            thirdShow.value = true
        }

    }


    VerDialog(verShow,navController)
    if (secShow.value){

        ActiveDialog(activeShow,navController)
    }
    if (secShow.value && thirdShow.value){

        RootDialog(rootShow,navController)

    }
//    if ( !thirdShow.value && !rootShow.value ){
//        TopNotification(forthShow,Modifier.wrapContentWidth()){
//            Box(modifier = Modifier.height(dimensionResource(R.dimen.focus_height)).wrapContentWidth()){
//                Image(
//                    painter = painterResource(R.drawable.focus_background),
//                    "cc",
////                    modifier = Modifier.fillMaxSize()
//                )
//                Column(
//                    Modifier.padding(vertical = dimensionResource(R.dimen.focus_logo_margin_top))
//                        .padding(start = dimensionResource(R.dimen.focus_logo_margin_left),)
//                ) {
//                    Image(
//                        painter = painterResource(R.drawable.focus_noti_logo),
//                        "cc",
//                        modifier = Modifier.size(
//                            dimensionResource(R.dimen.focus_logo_width),
//                            dimensionResource(R.dimen.focus_logo_height)
//                        )
//                    )
//                    Spacer(Modifier.height(dimensionResource(R.dimen.focus_summary_margin_top)))
//                    Text(
//                        stringResource(R.string.focus_notification_content),
//                        color = colorResource(R.color.focus_text_start_color)
//                    )
//                }
//            }
//        }
//    }

}