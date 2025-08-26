package com.yunzia.hyperstar.ui.screen.module.systemui.other.powermenu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.enums.EventState
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.nav.PagersModel
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPager
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize


fun getFunList():List<String>{
    return listOf(
        "recovery",
        "bootloader",
        "xiaoai",
        "screenshot",
        "silentMode",
        "airPlane",
        "wcScaner",
        "wcCode",
        "apCode",
        "apScan",)
}



@Composable
fun SelectFunScreen(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    parentRoute: MutableState<String>
) {

    val funTypes = stringArrayResource(R.array.power_fun_types).toList()
    val funTitles = stringArrayResource(R.array.power_fun_titles).toList()

    val pagersJson = backStackEntry.arguments?.getParcelable("pagersJson", PagersModel::class.java)
    val key = pagersJson?.key!!

    val selectFun = remember { mutableStateOf(SPUtils.getString(key, "null")) }


    ModuleNavPager(
        activityTitle = pagersJson.title,
        navController = navController,
        parentRoute = parentRoute,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
    ) {topAppBarScrollBehavior,padding->

        LazyColumn(
            modifier = Modifier.height(getWindowSize().height.dp)
                .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(top = padding.calculateTopPadding() + 12.dp, bottom = padding.calculateBottomPadding()),
        ) {

            funTypes.forEachIndexed { index, type ->

                item(index) {
                    FunItem(funTitles[index],type,key,selectFun)
                }
            }

        }

    }


}


@Composable
fun FunItem(
    title: String,
    type: String,
    key: String,
    selectFun: MutableState<String>,
){

    var isSelect = selectFun.value == type

    var eventState by remember { mutableStateOf(EventState.Idle) }
    val scale by animateFloatAsState(if (eventState == EventState.Pressed) 0.90f else 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(top = 10.dp)
            .scale(scale)
            .clip(G2RoundedCornerShape(16.dp))
            .background(if (isSelect) colorScheme.tertiaryContainer  else colorScheme.surfaceVariant)
            .clickable {
                selectFun.value = if (isSelect) "" else type
                isSelect = !isSelect
                SPUtils.setString(key, type)
            }
            .pointerInput(eventState) {

                awaitPointerEventScope {
                    eventState = if (eventState == EventState.Pressed) {
                        waitForUpOrCancellation()
                        EventState.Idle
                    } else {
                        awaitFirstDown(false)
                        EventState.Pressed
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize().padding(20.dp)
        ) {

            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
                    .align(Alignment.CenterVertically),
                color = if (isSelect) colorScheme.primary else colorScheme.onBackground
            )
            Checkbox(
                modifier = Modifier
                    .padding(start = 16.dp),
                enabled = true,
                checked = isSelect,
                onCheckedChange = {

                    selectFun.value = if (isSelect) "" else type
                    isSelect = !isSelect
                    SPUtils.setString(key, type)
                }
            )



        }
    }

}