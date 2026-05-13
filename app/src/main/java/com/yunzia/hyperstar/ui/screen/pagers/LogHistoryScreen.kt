package com.yunzia.hyperstar.ui.screen.pagers

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.preference.widget.PreferenceListPage
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.viewmodel.UpdaterDownloadViewModel
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun LogHistoryScreen() {
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    val downloadModel: UpdaterDownloadViewModel = activity.downloadModel

    val commitList = downloadModel.commitHistory.collectAsState()

    PreferenceListPage(
        title = stringResource(R.string.update_history_log),
        navController = navController,
    ) {
        commitList.value.drop(0).forEach {
            item(it.apkName) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 26.dp)
                        .padding(bottom = 16.dp),
                ) {
                    Text(
                        text = "# " + it.apkName.replace("HyperStar_v", ""),
                        fontSize = 19.sp,
                        fontWeight = FontWeight(600),
                        modifier = Modifier.padding(bottom = 11.dp),
                        color = colorScheme.onSurface
                    )
                    it.commitMessage.lines().forEach { line ->
                        if (line.startsWith("#")) {
                            Text(
                                text = "|  " + line.removePrefix("#").trim(),
                                fontSize = 17.sp,
                                modifier = Modifier
                                    .padding(horizontal = 7.dp)
                                    .padding(top = 16.dp, bottom = 11.dp),
                                fontWeight = FontWeight(550),
                                color = colorScheme.onBackground
                            )
                        } else {
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
