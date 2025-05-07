package com.yunzia.hyperstar.ui.pagers

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.NavTopAppBar
import com.yunzia.hyperstar.ui.component.XScaffold
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.ui.component.nav.backParentPager
import com.yunzia.hyperstar.viewmodel.UpdaterDownloadViewModel
import dev.chrisbanes.haze.HazeState
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun LogHistoryScreen(
    navController: NavController,
    currentStartDestination: MutableState<String>,
) {

    val hazeState = remember { HazeState() }
    val activity = LocalActivity.current as MainActivity
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val downloadModel: UpdaterDownloadViewModel = activity.downloadModel

    val commitList = downloadModel.commitHistory.collectAsState()

    XScaffold(
        modifier = Modifier.fillMaxSize(),
        popupHost = { },
        topBar = {
            NavTopAppBar(
                modifier = Modifier.showBlur(hazeState),
                color = Color.Transparent,
                title = stringResource(R.string.update_history_log),
                scrollBehavior = topAppBarScrollBehavior,
                navController = navController,
                parentRoute = currentStartDestination,
                actions = {}
            )

        }
    ) { padding ->
        BackHandler(true) {
            navController.backParentPager(currentStartDestination.value)
        }
        LazyColumn(
            modifier = Modifier.blur(hazeState).height(getWindowSize().height.dp)
                .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(top = padding.calculateTopPadding()+30.dp, bottom = padding.calculateBottomPadding()+28.dp),
        ) {
            commitList.value.drop(0).forEach {
                item(it.apkName) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 26.dp).padding(bottom = 16.dp),
                    ) {
                        Text(
                            text = "# "+it.apkName.replace("HyperStar_v",""),
                            fontSize = 19.sp,
                            fontWeight = FontWeight(600),
                            modifier = Modifier.padding(bottom = 11.dp),
                            color = colorScheme.onSurface
                        )
                        it.commitMessage.lines().forEach { line->

                            if (line.startsWith("#")) {
                                // 以#开头的文本加粗加大
                                Text(
                                    text = "|  "+line.removePrefix("#").trim(),
                                    fontSize = 17.sp,
                                    modifier = Modifier.padding(horizontal = 7.dp).padding(top = 16.dp, bottom = 11.dp),
                                    fontWeight = FontWeight(550),
                                    color = colorScheme.onBackground
                                )
                            } else {
                                // 普通文本
                                Text(
                                    text = line,
                                    fontSize = 15.sp,
                                    lineHeight = 2.em,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onSurfaceVariantSummary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }


                }
            }

        }




    }


}
