package com.yunzia.hyperstar.ui.screen.pagers

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.preference.widget.PreferenceListPage
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.utils.getVerName
import com.yunzia.hyperstar.viewmodel.UpdaterDownloadViewModel
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun CurrentVersionLogScreen() {
    val context = LocalContext.current
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    val downloadModel: UpdaterDownloadViewModel = activity.downloadModel

    val currentVersion = remember { getVerName(context) }
    val currentLog = downloadModel.currentCommit.collectAsState()

    PreferenceListPage(
        title = stringResource(R.string.current_version_log),
        navController = navController,
    ) {
        if (currentLog.value == "null") {
            item("empty") {
                Box(
                    modifier = Modifier.fillMaxSize().padding(top = 200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_log_found_current_version),
                        color = colorScheme.onSurfaceVariantSummary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        currentLog.value.lines().forEach { line ->
            item(line) {
                if (line.startsWith("#")) {
                    Text(
                        text = "|  " + line.removePrefix("#").trim(),
                        fontSize = 17.sp,
                        modifier = Modifier
                            .padding(horizontal = 26.dp)
                            .padding(top = 16.dp, bottom = 11.dp),
                        fontWeight = FontWeight(550),
                        color = colorScheme.onBackground
                    )
                } else {
                    Text(
                        text = line,
                        fontSize = 15.sp,
                        lineHeight = 2.em,
                        modifier = Modifier.padding(horizontal = 26.dp, vertical = 5.dp),
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurfaceVariantSummary
                    )
                }
            }
        }
    }
}
