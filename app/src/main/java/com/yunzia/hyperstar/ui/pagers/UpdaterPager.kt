package com.yunzia.hyperstar.ui.pagers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.PagerList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.BaseButton
import com.yunzia.hyperstar.ui.component.Button
import com.yunzia.hyperstar.ui.component.SuperIntentArrow
import com.yunzia.hyperstar.ui.component.XScaffold
import com.yunzia.hyperstar.ui.component.dialog.SuperXDialog
import com.yunzia.hyperstar.ui.component.dialog.SuperXPopupUtil.Companion.dismissXDialog
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.ui.component.nav.backParentPager
import com.yunzia.hyperstar.ui.component.nav.nav
import com.yunzia.hyperstar.utils.getVerName
import com.yunzia.hyperstar.viewmodel.UpdaterDownloadViewModel
import com.yunzia.hyperstar.viewmodel.UpdaterViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ListPopup
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.DropdownImpl
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.ImmersionMore
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.dismissPopup
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import java.io.File
import java.net.URLEncoder

@Composable
fun UpdaterPager(
    navController: NavController,
    currentStartDestination: MutableState<String>,
) {
    val activity = LocalActivity.current as MainActivity
    val viewModel: UpdaterViewModel = viewModel()
    val downloadModel: UpdaterDownloadViewModel = activity.downloadModel
    val hazeState = remember { HazeState() }
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val view = LocalView.current
    val context = LocalContext.current

    val pagerState = rememberPagerState(initialPage =  0, pageCount = { 2 })
    // Collect states from ViewModel
    val isNeedUpdate = downloadModel.isNeedUpdate.collectAsState()
    val showUpdater = downloadModel.showUpdater.collectAsState()
    val currentCommit = downloadModel.currentCommit.collectAsState()
    val downloadStatus = downloadModel.downloadStatus.collectAsState()
    val isLoading = downloadModel.isLoading.collectAsState()
    val uiState = viewModel.uiState.collectAsState()

    val menuShow = remember { mutableStateOf(false) }
    val currentVersion = remember { getVerName(context) }
    val fileUrl = remember {
        derivedStateOf {
            "https://gitee.com/dongdong-gc/hyper-star-updater/raw/main/dev/${activity.newAppName.value}"
        }
    }

    val logo = if (activity.isDarkMode) {
        painterResource(R.drawable.hyperstar2_dark)
    } else {
        painterResource(R.drawable.hyperstar2)
    }

    // Effect to initialize data
    LaunchedEffect(Unit) {
        downloadModel.init()
        downloadModel.getFileTotalSize(fileUrl.value)
        downloadModel.loadCommitHistory(currentVersion)
        downloadModel.checkForUpdates(currentVersion, activity.newAppVersion.value)
    }

    XScaffold(
        modifier = Modifier.fillMaxSize(),
        popupHost = { },
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.then(
                    if (uiState.value.isBlur) Modifier.showBlur(hazeState)
                    else Modifier
                ),
                color = Color.Transparent,
                title = if (pagerState.currentPage != 0 && uiState.value.isBlur) stringResource(R.string.app_update_title) else "",
                scrollBehavior = topAppBarScrollBehavior,
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(start = 12.dp),
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            if (uiState.value.newPageState.expand){
                                viewModel.handleEvent(
                                    UpdaterViewModel.UpdateDetailEvent.NavigateBack
                                )
                            }else{
                                downloadModel.clearInit()
                                navController.backParentPager(currentStartDestination.value)
                            }

                        }
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.bar_back__exit),
                            contentDescription = "back",
                            tint = colorScheme.onBackground)
                    }

                },
                actions = {

                    UpdatePup(menuShow,navController,downloadModel)

                    IconButton(
                        modifier = Modifier.padding(end = 12.dp),
                        enabled = !isLoading.value,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            menuShow.value = true
                        }
                    ) {

                        Icon(
                            imageVector = MiuixIcons.Useful.ImmersionMore,
                            contentDescription = "menu",
                            tint = colorScheme.onBackground)

                    }
                }
            )
        }
    ) { padding ->
        BackHandler(true) {
            downloadModel.clearInit()
            navController.backParentPager(currentStartDestination.value)
        }

        LaunchedEffect(isLoading.value , isNeedUpdate.value,uiState.value.newPageState.currentPage) {
            if (isLoading.value) return@LaunchedEffect
            if (isNeedUpdate.value) {

                viewModel.handleEvent(
                    UpdaterViewModel.UpdateDetailEvent.SetScrollEnabled(uiState.value.newPageState.currentPage == 0)
                )
                if (!showUpdater.value){
                    coroutineScope {
                        delay(500)
                        downloadModel.showUpdater()
                        pagerState.animateScrollToPage(1,animationSpec = tween(
                            durationMillis = 600,
                            easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
                        ))
                    }
                }
            }else{
                viewModel.handleEvent(
                    UpdaterViewModel.UpdateDetailEvent.SetScrollEnabled(false)
                )
            }

        }

        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .blur(hazeState),
            beyondViewportPageCount = PagerDefaults.BeyondViewportPageCount+1,
            state = pagerState,
            userScrollEnabled = uiState.value.isScrollEnabled,
        ) { page ->

            when (page) {
                0 ->{
                    UpdateOverviewPage(
                        pagerState = pagerState,
                        modifier = Modifier,
                        padding = padding,
                        title = currentVersion,
                        isLoading = isLoading,
                        logo = logo,
                        navController = navController
                    ){
                        if (isLoading.value) return@UpdateOverviewPage
                        val encodedLog = URLEncoder.encode(currentCommit.value.replace(" ", "%20"), "UTF-8")
                        downloadModel.noInit()
                        navController.nav(PagerList.CURRENTLOG+"?currentAllLog=${encodedLog}")
                    }
                }
                1 -> {
                    if (showUpdater.value) {
                        pagerState.UpdateDetailPage(
                            padding = padding,
                            logo = logo,
                            newVersion = activity.newAppVersion.value,
                            fileUrl = fileUrl,
                            navController = navController,
                            showUpdater = showUpdater,
                            downloadStatus = downloadStatus,
                            viewModel = viewModel,
                            topAppBarScrollBehavior = topAppBarScrollBehavior,
                            uiState = uiState,
                            downloadModel = downloadModel
                            // 添加额外的起始padding，确保内容不会太靠边
                        )

                    }
                }
            }

        }


    }
}

@Composable
fun UpdateOverviewPage(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    title: String,
    logo: Painter,
    navController: NavController,
    pagerState: PagerState,
    isLoading: State<Boolean>,
    navPager:()->Unit
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = padding.calculateTopPadding() + 14.dp,
                bottom = padding.calculateBottomPadding() + 28.dp
            ),
        contentAlignment = Alignment.BottomCenter
    ) {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 180.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(color = colorScheme.onBackground)
                    ) {
                        append("Hyper")
                    }
                    withStyle(
                        style = SpanStyle(color = Color(0xFF2856FF) )
                    ) {
                        append("Star "+title.substring(0,3))
                    }
                },
                fontSize = 40.sp,
                fontWeight = FontWeight(600),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = colorScheme.onSurfaceVariantSummary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isLoading.value) stringResource(R.string.checking_update) else stringResource(R.string.current_version_log)+" >",
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = null
                    ) {
                        navPager()
                    },
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = colorScheme.onSurfaceVariantSummary.copy(alpha = 0.4f)
            )
        }

        val view = LocalView.current
        val showDialog = remember { mutableStateOf(false) }

        if ( pagerState.currentPage == 0){

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 15.dp)
                    .padding(horizontal = 28.dp),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    showDialog.value = true
                }
            ) {
                Text(
                    stringResource(R.string.go_channel_discuss),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    fontSize = 18.sp,
                    color = colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        ChannelDialog(showDialog,navController)



    }
}

@Composable
fun UpdateContent(
    lastCommit: String,
    fileUrl: State<String>,
    navController: NavController
) {


    val styles = TextLinkStyles(style = SpanStyle(color = colorResource(R.color.blue), fontSize = 14.sp))

    val annotatedText = buildAnnotatedString {
        append(stringResource(R.string.update_attention))
        append(stringResource(R.string.manual_download_prompt))
        pushStyle(
            SpanStyle(
                color = colorResource(R.color.blue),
                textDecoration = TextDecoration.Underline
            )
        )
        withLink(LinkAnnotation.Clickable(tag = "URL", linkInteractionListener = {

            navController.context.startActivity(Intent(Intent.ACTION_VIEW, fileUrl.value.toUri()))
        }, styles = styles)) {
            append(fileUrl.value)
        }

        addStringAnnotation(
            tag = "URL",
            annotation = fileUrl.value,
            start = length - fileUrl.value.length,
            end = length
        )
        pop()
    }

    HorizontalDivider(
        modifier = Modifier
            .padding(top = 100.dp, bottom = 40.dp)
            .padding(
                horizontal = 26.dp
            )
    )

    lastCommit.lines().forEach { line ->
        if (line.startsWith("#")) {
            // 以#开头的文本加粗加大
            Text(
                text = "|  "+line.removePrefix("#").trim(),
                fontSize = 17.sp,
                modifier = Modifier
                    .padding(horizontal = 26.dp)
                    .padding(top = 16.dp, bottom = 11.dp),
                fontWeight = FontWeight(550),
                color = colorScheme.onBackground
            )
        } else {
            // 普通文本
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

    HorizontalDivider(
        modifier = Modifier
            .padding(vertical = 46.dp)
            .padding(
                horizontal = 26.dp
            )
    )
    Text(
        text = annotatedText,
        fontSize = 15.sp,
        modifier = Modifier.padding(horizontal = 26.dp),
        fontWeight = FontWeight.Medium,
        lineHeight = 1.5.em,
        color = colorScheme.onSurfaceVariantSummary,
    )



}

@Composable
private fun UpdatePup(
    show: MutableState<Boolean>,
    navController: NavController,
    downloadModel: UpdaterDownloadViewModel,
) {
    ListPopup(
        show = show,
        popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
        alignment = PopupPositionProvider.Align.TopRight,
        onDismissRequest = {
            show.value = false
        }
    ) {
        ListPopupColumn {
            DropdownImpl(
                text = stringResource(R.string.update_history_log),
                optionSize = 1,
                isSelected = false,
                index = 0,
                onSelectedIndexChange = {
                    dismissPopup(show)
                    downloadModel.noInit()
                    navController.navigate(PagerList.LOGHISTORY)
                }
            )

        }
    }


}

@Composable
private fun ChannelDialog(
    show: MutableState<Boolean>,
    navController: NavController
) {
    SuperXDialog(
        title = stringResource(R.string.go_channel_discuss),
        show = show,
        onDismissRequest = {
            dismissXDialog(show)
        }
    ) {

        Column(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 18.dp)
                .fillMaxWidth()
                .clip(SmoothRoundedCornerShape(12.dp, 0.5f))
                .background(colorScheme.secondaryContainer)
        ) {

            SuperIntentArrow(
                title = stringResource(R.string.qq_group_title),
                navController = navController,
                url = "http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&amp;k=5ONF7LuaoQS6RWEOUYBuA0x4X8ssvHJp&amp;authKey=Pic4VQJxKBJwSjFzsIzbJ50ILs0vAEPjdC8Nat4zmiuJRlftqz9%2FKjrBwZPQTc4I&amp;noverify=0&amp;group_code=810317966"
            )
            SuperIntentArrow(
                title = stringResource(R.string.telegram_group),
                navController = navController,
                url = "https://t.me/Hyperstar_chat"
            )

        }

        Row {
            BaseButton(
                text = stringResource(R.string.cancel),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    dismissXDialog(show)
                }

            )

        }

    }
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



