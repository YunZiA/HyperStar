package com.yunzia.hyperstar.ui.pagers.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.Utils
import kotlinx.coroutines.delay
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

}