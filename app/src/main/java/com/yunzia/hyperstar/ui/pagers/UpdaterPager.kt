package com.yunzia.hyperstar.ui.pagers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.Button
import com.yunzia.hyperstar.ui.base.NavTopAppBar
import com.yunzia.hyperstar.ui.base.XScaffold
import com.yunzia.hyperstar.ui.base.modifier.blur
import com.yunzia.hyperstar.ui.base.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import com.yunzia.hyperstar.ui.base.nav.backParentPager
import com.yunzia.hyperstar.utils.getVerName
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.getWindowSize
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun UpdaterPager(
    navController: NavController,
    currentStartDestination: MutableState<String>,
) {
    val hazeState = remember { HazeState() }
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val activity = LocalActivity.current as MainActivity
    val context = LocalContext.current

    // 当前版本和新版本
    val currentVersion = remember { getVerName(context) }
    val isNeedUpdate = remember { mutableStateOf(false) }
    val lastCommit = remember { mutableStateOf("") }

    // 更新历史和 UI 状态
    val commitHistory = remember { mutableStateListOf<CommitHistory>() }
    val title = remember { mutableStateOf("正在检查更新...") }
    val show = remember { mutableStateOf(false) }
    val showUpdater = remember { mutableStateOf(false) }


    val logo = if (activity.isDarkMode){
        painterResource(R.drawable.hyperstar2_dark)
    }else{
        painterResource(R.drawable.hyperstar2)
    }

    // 检查更新逻辑
    LaunchedEffect(activity.newAppVersion) {
        val currentVersions = extractOnlyNumbers(currentVersion)
        val newVersions = extractOnlyNumbers(activity.newAppVersion.value)

        // 加载更新历史
        commitHistory.addAll(fetchAndParseCommitHistory())
        lastCommit.value = fetchHeadCommitContent().replace("--", "")
        isNeedUpdate.value = currentVersions < newVersions
    }

    // 更新 UI 状态
    LaunchedEffect(Unit) {
        delay(600) // 延迟更新 UI
        title.value =  currentVersion
        show.value = true
    }

    // 更新 UI 状态
    LaunchedEffect(showUpdater.value) {
        if (showUpdater.value) title.value = activity.newAppVersion.value
    }

    XScaffold(
        modifier = Modifier.fillMaxSize(),
        popupHost = { },
        topBar = {
            NavTopAppBar(
                modifier = Modifier.showBlur(hazeState),
                color = Color.Transparent,
                title = "应用更新",
                largeTitle = "",
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
            modifier = Modifier
                .height(getWindowSize().height.dp)
                .blur(hazeState)
                .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 14.dp,
                bottom = padding.calculateBottomPadding() + 28.dp
            ),
        ) {
            item(1) {
                UpdateHeader(
                    summary = title.value,
                    logo = logo
                )
            }
            item(2) {
                if (show.value) {
                    if (showUpdater.value){
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(top = 100.dp, bottom = 40.dp)
                                .padding(
                                    horizontal = 26.dp
                                )
                        )
                        Column(
                            modifier = Modifier.padding(horizontal = 26.dp)
                        ) {
                            Text(
                                text = lastCommit.value,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                        }

                    }else{
                        UpdateHistory(commitHistory = commitHistory)
                    }
                }
            }
        }

        if (show.value) {
            UpdateActions(
                isNeedUpdate = isNeedUpdate.value,
                showUpdater = showUpdater,
                navController = navController
            )
        }
    }
}

@Composable
fun UpdateHeader(
    summary: String,
    logo: Painter
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            contentDescription = "",
            painter = logo,
            modifier = Modifier.width(260.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Toast.makeText(LocalContext.current,summary, Toast.LENGTH_SHORT).show()
        Text(
            text = summary,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
        )
    }
}

@Composable
fun UpdateHistory(commitHistory: List<CommitHistory>) {
    HorizontalDivider(
        modifier = Modifier
            .padding(top = 100.dp)
            .padding(horizontal = 26.dp)
    )
    commitHistory.forEach {
        Column(
            modifier = Modifier.padding(horizontal = 26.dp).padding(top = 40.dp)
        ) {
            Text(
                text = "# "+it.apk_name.replace("HyperStar_v",""),
                fontSize = 17.sp,
                fontWeight = FontWeight(550),
                color = MiuixTheme.colorScheme.onSurface
            )
            Text(
                text = it.commit_message.replace("--",""),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 20.dp),
                fontWeight = FontWeight.Medium,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
        }
    }
}

@Composable
fun UpdateActions(
    isNeedUpdate: Boolean,
    showUpdater: MutableState<Boolean>,
    navController: NavController
) {
    val view = LocalView.current
    val activity = LocalActivity.current as MainActivity

    val fileUrl = remember { mutableStateOf("") }
    val context = LocalContext.current
    val fileName = activity.newAppName.value
    val downloadStatus = remember { mutableStateOf(DownloadStatus.NONE) }
    val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val outputFile = File(downloadsDir, fileName)
    val actionText = remember { mutableStateOf(if (showUpdater.value) "下载更新" else "发现新版本") }

    LaunchedEffect(showUpdater.value) {
        if (showUpdater.value){
            actionText.value = "下载更新"
        }
    }
    LaunchedEffect(downloadStatus.value) {
        when (downloadStatus.value){
            DownloadStatus.SUCCESS -> {
                actionText.value = "安装"
            }
            DownloadStatus.DOWNLOAD ->{
                actionText.value = "下载中"
            }
            DownloadStatus.FAIL -> {

            }
            else -> {}
        }
    }

    // 启动协程下载文件
    LaunchedEffect(fileUrl.value) {
        if (fileUrl.value == "") return@LaunchedEffect
        withContext(Dispatchers.IO) {
            try {
                downloadStatus.value = DownloadStatus.DOWNLOAD
                // 打开连接并下载文件
                val connection = URL(fileUrl.value).openConnection() as HttpURLConnection
                connection.inputStream.use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                }
                // 通知成功
                withContext(Dispatchers.Main) {
                    downloadStatus.value = DownloadStatus.SUCCESS
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 通知失败
                withContext(Dispatchers.Main) {
                    downloadStatus.value = DownloadStatus.FAIL
                    Toast.makeText(context, "下载失败: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .height(getWindowSize().height.dp)
            .fillMaxWidth()
            .padding(bottom = 45.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Bottom
    ) {
        if (isNeedUpdate) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 28.dp),
                colors = Color(0xFF3482FF),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    if (showUpdater.value){
                        if (downloadStatus.value == DownloadStatus.SUCCESS){
                            installApk(context,outputFile.absolutePath)
                        }else{
                            fileUrl.value = "https://gitee.com/dongdong-gc/hyper-star-updater/raw/main/dev/${fileName}"

                        }
                    }else{
                        showUpdater.value = true

                    }
                }
            ) {
                Text(
                    text = actionText.value,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (showUpdater.value) return@Column

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 28.dp),
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                navController.navigate("groupDiscussion")
            }
        ) {
            Text(
                "进入群组讨论",
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 18.sp,
                color = MiuixTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Serializable
data class CommitHistory(
    val apk_name: String,
    val commit_message: String,
)

enum class DownloadStatus{
    NONE,DOWNLOAD,SUCCESS,FAIL
}


suspend fun fetchAndParseCommitHistory(): List<CommitHistory> {
    return withContext(Dispatchers.IO) {
        try {
            val jsonContent = fetchJsonFromUrl("https://gitee.com/dongdong-gc/hyper-star-updater/raw/main/dev/commit_history.json")
            Json.decodeFromString(jsonContent)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

// 网络请求函数，用于获取 head_commit.txt 内容
suspend fun fetchHeadCommitContent(): String {
    return withContext(Dispatchers.IO) {
        try {
            val connection = URL("https://gitee.com/dongdong-gc/hyper-star-updater/raw/main/dev/head_commit.txt").openConnection() as HttpURLConnection
            connection.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            "无法获取更新内容，请检查网络连接！"

        }
    }
}

fun fetchJsonFromUrl(url: String): String {
    val connection = URL(url).openConnection() as HttpURLConnection
    return connection.inputStream.bufferedReader().use { it.readText() }
}

// 安装 APK 文件
fun installApk(context: Context, filePath: String) {
    try {
        val apkFile = File(filePath)
        val apkUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)

    } catch (e: Exception) {
        e.printStackTrace()
        Log.d("ggc", "安装失败: ${e.message}")
    }
}



