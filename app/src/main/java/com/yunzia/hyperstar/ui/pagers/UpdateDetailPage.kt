package com.yunzia.hyperstar.ui.pagers

import android.os.Environment
import android.view.HapticFeedbackConstants
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.Button
import com.yunzia.hyperstar.ui.base.card.rememberTiltAnimationState
import com.yunzia.hyperstar.ui.base.card.withTiltEffect
import com.yunzia.hyperstar.ui.base.helper.getSystemCornerRadius
import com.yunzia.hyperstar.ui.base.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.viewmodel.UpdaterDownloadViewModel
import com.yunzia.hyperstar.viewmodel.UpdaterDownloadViewModel.DownloadStatus
import com.yunzia.hyperstar.viewmodel.UpdaterViewModel
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize
import java.io.File


@Composable
fun PagerState.UpdateDetailPage(
    padding: PaddingValues,
    logo: Painter,
    newVersion: String,
    fileUrl: State<String>,
    navController: NavController,
    showUpdater: State<Boolean>,
    downloadStatus: State<DownloadStatus>,
    viewModel: UpdaterViewModel,
    topAppBarScrollBehavior: ScrollBehavior,
    uiState: State<UpdaterViewModel.UpdateDetailUiState>,
    downloadModel: UpdaterDownloadViewModel
) {
    // State declarations
    val progress = this.currentPageOffsetFraction
    val currentPage = this.currentPage
    val lastCommit = downloadModel.lastCommit.collectAsState()
    val fileSize = downloadModel.fileSize.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val tiltState = rememberTiltAnimationState()
    val radius = getSystemCornerRadius()
    val listState = rememberLazyListState()

    val density = LocalDensity.current
    val width = with(density) { getWindowSize().width.toDp() }
    val alpha = remember { mutableStateOf(0f) }
    var offsetX by remember { mutableStateOf(0.dp) }
    //var offsetX = remember { Animatable((-80).dp) }

    // Effects
    LaunchedEffect(currentPage, uiState.value.newPageState.currentPage, topAppBarScrollBehavior.state.contentOffset) {
        viewModel.handleEvent(
            UpdaterViewModel.UpdateDetailEvent.PageChanged(uiState.value.newPageState.currentPage)
        )
        if (uiState.value.newPageState.currentPage != 1) {
            topAppBarScrollBehavior.state.contentOffset = 0f
            listState.animateScrollToItem(0)
        }
        viewModel.handleEvent(
            UpdaterViewModel.UpdateDetailEvent.ScrollOffsetChanged(
                topAppBarScrollBehavior.state.contentOffset
            )
        )
    }

    val animationState = viewModel.calculateAnimationState(
        newPageState = uiState.value.newPageState,
        padding = padding,
        tiltState = tiltState,

        )

    LaunchedEffect(animationState.values.radius) {
        viewModel.handleEvent(
            UpdaterViewModel.UpdateDetailEvent.AnimationCompleted(
                animationState.values.radius, radius
            )
        )
    }

    LaunchedEffect(progress, currentPage) {
        val pro = currentPage.toFloat() + progress
        alpha.value = pro
        if (showUpdater.value){
            offsetX = (-70).dp * (1 - pro)
        }
    }

    val scale = remember(animationState.values.horizontal) {
        derivedStateOf { (width - animationState.values.horizontal * 2) / width }
    }

    // Root container
    Box(
        modifier = Modifier
            .height(getWindowSize().height.dp)
            .background(Color.Black.copy(animationState.alpha.coerceAtMost(0.3f)))
    ) {
        // Update available text
        AnimatedVisibility(
            !uiState.value.newPageState.expand,
            enter = fadeIn(animationSpec = tween(300, easing = LinearOutSlowInEasing)) +
                    expandIn(animationSpec = tween(400, easing = LinearOutSlowInEasing)),
            exit = shrinkOut(animationSpec = tween(400, easing = LinearOutSlowInEasing)) +
                    fadeOut(animationSpec = tween(300, easing = LinearOutSlowInEasing)),
        ) {
            Text(
                text = stringResource(R.string.update_available),
                fontSize = MiuixTheme.textStyles.title1.fontSize,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha.value)
                    .padding(horizontal = 26.dp)
                    .padding(
                        top = (animationState.paddings.calculateTopPadding())
                            .coerceAtLeast(0.dp)
                    )
            )
        }

        // Main content container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(),
            contentAlignment = Alignment.TopStart
        ) {
            // Background card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(animationState.values.cardHeight)
                    .padding(horizontal = animationState.values.horizontal)
                    .padding(top = animationState.values.top)
                    .offset(x = offsetX, y = 0.dp)
                    .then(
                        if (uiState.value.newPageState.expand) {
                            Modifier.graphicsLayer {
                                rotationX = animationState.rotationX
                                rotationY = animationState.rotationY
                                scaleX = animationState.scaleX
                                scaleY = animationState.scaleY
                                transformOrigin = TransformOrigin(
                                    pivotFractionX = animationState.pivotX,
                                    pivotFractionY = animationState.pivotY
                                )
                                shape = SmoothRoundedCornerShape(
                                    if (uiState.value.newPageState.complete) 0.dp
                                    else animationState.values.radius,
                                    1f
                                )
                                clip = true
                            }
                        } else {
                            Modifier.withTiltEffect(tiltState)
                        }
                    )
                    .background(animationState.values.color)
            )

            // Content list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(animationState.values.cardHeight)
                    .offset(x = offsetX, y = 0.dp)
                    .padding(top = animationState.values.top)
                    .then(
                        if (uiState.value.newPageState.expand) {
                            Modifier
                                .graphicsLayer {
                                    rotationX = animationState.rotationX
                                    rotationY = animationState.rotationY
                                    scaleX = animationState.scaleX
                                    scaleY = animationState.scaleY
                                    transformOrigin = TransformOrigin(
                                        pivotFractionX = animationState.pivotX,
                                        pivotFractionY = animationState.pivotY
                                    )
                                    shape = SmoothRoundedCornerShape(
                                        if (uiState.value.newPageState.complete) 0.dp
                                        else animationState.values.radius,
                                        1f
                                    )
                                    clip = true
                                }
                                .nestedOverScrollVertical(
                                    topAppBarScrollBehavior.nestedScrollConnection
                                )
                        } else {
                            Modifier.withTiltEffect(
                                tiltState,
                                coroutineScope,
                                animationState.values.radius,
                                needCancel = uiState.value.needCancel,
                            ) { isTilting ->
                                viewModel.handleEvent(
                                    UpdaterViewModel.UpdateDetailEvent.SetScrollEnabled(!isTilting)
                                )
                            }
                        }
                    ),
                state = listState,
                userScrollEnabled = uiState.value.newPageState.expand
            ) {
                // Header item
                item {
                    Column(
                        modifier = Modifier
                            .then(
                                if (uiState.value.newPageState.currentPage == 0) {
                                    Modifier.height(480.dp)
                                } else Modifier
                            )
                            .padding(horizontal = 28.dp)
                            .clickable(
                                enabled = currentPage == 1 && !uiState.value.newPageState.expand,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                viewModel.handleEvent(
                                    UpdaterViewModel.UpdateDetailEvent.NavigateToDetailPage
                                )
                            }
                    ) {
                        // Logo and version info
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = animationState.values.titleTop),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                contentDescription = "",
                                painter = logo,
                                modifier = Modifier.width(265.dp)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "$newVersion | ${fileSize.value}",
                                fontSize = 13.5.sp,
                                modifier = Modifier,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = colorScheme.onSurfaceVariantSummary
                            )
                        }

                        // View full log text
                        AnimatedVisibility(
                            uiState.value.newPageState.currentPage == 0,
                            modifier = Modifier.weight(1f),
                            enter = fadeIn(
                                animationSpec = tween(
                                    500,
                                    delayMillis = 100,
                                    easing = LinearOutSlowInEasing
                                )
                            ),
                            exit = fadeOut(
                                animationSpec = tween(400, easing = LinearOutSlowInEasing)
                            ),
                        ) {
                            Column(verticalArrangement = Arrangement.Bottom) {
                                Text(
                                    text = "查看完整日志 >",
                                    fontSize = 14.2.sp,
                                    modifier = Modifier.padding(
                                        start = 28.dp,
                                        bottom = 28.dp
                                    ),
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onSurfaceVariantSummary
                                )
                            }
                        }
                    }
                }

                // Detail content item
                item {
                    AnimatedVisibility(
                        uiState.value.newPageState.currentPage == 1,
                        enter = fadeIn(
                            animationSpec = tween(
                                500,
                                delayMillis = 100,
                                easing = LinearOutSlowInEasing
                            )
                        ),
                        exit = fadeOut(
                            animationSpec = tween(400, easing = LinearOutSlowInEasing)
                        ),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(width)
                                .scale(scaleX = scale.value, scaleY = 1f)
                        ) {
                            DetailContent(
                                lastCommit = lastCommit.value,
                                fileUrl = fileUrl,
                                navController = navController,
                                padding = padding,
                                onBack = {
                                    viewModel.handleEvent(
                                        UpdaterViewModel.UpdateDetailEvent.NavigateBack
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // Update actions
        Box(modifier = Modifier.alpha(alpha.value)) {
            UpdateActions(
                fileUrl = fileUrl,
                downloadStatus = downloadStatus,
                onDownloadClick = { url, outputFile ->
                    if (uiState.value.newPageState.currentPage == 0){
                        viewModel.handleEvent(
                            UpdaterViewModel.UpdateDetailEvent.NavigateToDetailPage
                        )
                    }
                    downloadModel.downloadUpdate(url, outputFile)
                }
            )
        }
    }
}


@Composable
private fun DetailContent(
    lastCommit: String,
    fileUrl: State<String>,
    navController: NavController,
    padding: PaddingValues,
    onBack: () -> Unit
) {
    BackHandler(true, onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = padding.calculateBottomPadding() + 100.dp)
        //.navigationBarsPadding()
        //.clickable(onClick = onBack)
    ) {
        UpdateContent(
            lastCommit = lastCommit,
            fileUrl = fileUrl,
            navController = navController
        )
    }
}

@Composable
private fun UpdateActions(
    fileUrl: State<String>,
    downloadStatus: State<DownloadStatus>,
    onDownloadClick: (String, File) -> Unit
) {
    val view = LocalView.current
    val activity = LocalActivity.current as MainActivity
    val context = LocalContext.current
    val fileName = activity.newAppName.value
    val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    val outputFile = File(downloadsDir, fileName)
    val download_update = stringResource(R.string.download_update)
    val actionText = remember { mutableStateOf( download_update ) }

    LaunchedEffect(downloadStatus.value) {
        when (downloadStatus.value){
            DownloadStatus.SUCCESS -> {
                actionText.value = context.getString(R.string.install_update)
            }
            DownloadStatus.DOWNLOAD ->{
                actionText.value = context.getString(R.string.downloading)
            }
            DownloadStatus.FAIL -> {

            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .height(getWindowSize().height.dp)
            .fillMaxWidth()
            .padding(bottom = 28.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 28.dp),
            colors = Color(0xFF3482FF),
            onClick =  {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                if (downloadStatus.value == DownloadStatus.SUCCESS){
                    installApk(context,outputFile.absolutePath)
                }else{
                    onDownloadClick(fileUrl.value,outputFile)
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

}

