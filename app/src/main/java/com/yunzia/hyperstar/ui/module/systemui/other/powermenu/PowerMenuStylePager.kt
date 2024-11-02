package com.yunzia.hyperstar.ui.module.systemui.other.powermenu

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
//import com.chaos.hyperstar.R
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.XSuperDropdown
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.SPUtils
import com.yunzia.hyperstar.utils.Utils
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun PowerMenuStylePager(
    navController: NavController
) {

    val coroutineScope = rememberCoroutineScope()

    val style = remember { mutableIntStateOf( SPUtils.getInt("is_power_menu_style",0) ) }


    val pagerState = rememberPagerState(initialPage = style.intValue,pageCount = { 3 })

    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(style.intValue, isInitialized) {
        if (isInitialized) {
            pagerState.animateScrollToPage(style.intValue)
        } else {
            isInitialized = true
        }
    }

    ModuleNavPagers(
        activityTitle = "电源菜单",
        navController = navController,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ) {
        firstClasses {
            XSuperDropdown(
                key = "is_power_menu_style",
                option = R.array.power_menu_style,
                title = "样式",
                selectedIndex = style
            )
        }

        classes {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(630.dp),
                state = pagerState,
                userScrollEnabled = false
            ) { page ->
                //Toast.makeText(navController.context, "page ${page}", Toast.LENGTH_SHORT).show()
                when (page) {
                    0 -> {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Box(
                                modifier = Modifier.width(80.dp)
                                    .height(300.dp)
                                    .clip(RoundedCornerShape(40.dp))
                                    .background(colorScheme.secondary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "默认")

                            }



                        }
                    }

                    1 -> {
                        PowerMenuStyleA()
                    }

                    2 -> {
                        PowerMenuStyleB()
                    }
                }

            }
        }
    }


}