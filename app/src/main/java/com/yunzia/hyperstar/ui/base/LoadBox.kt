package com.yunzia.hyperstar.ui.base

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun rememberLoadStatus(): LoadStatus {
    val loadStatus by remember { mutableStateOf(LoadStatus()) }
    return loadStatus
}

@Stable
class LoadStatus{
    var current by mutableStateOf(Status.Loading)
    var isEmpty by mutableStateOf(false)

    fun isLoading() = (current == Status.Loading)
    fun isComplete() = (current == Status.Complete)

    enum class Status{
        Loading,Complete
    }
}


@Composable
fun LoadBox(
    loadStatus:LoadStatus,
    modifier:Modifier,
    content: @Composable ()->Unit
) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            loadStatus.isLoading(),
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + scaleOut() + slideOutVertically(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,

            ) {
                Loading()
                Text(
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight(550),
                    text = stringResource(R.string.loading),
                )
            }

        }
        AnimatedVisibility(
            loadStatus.isComplete() && loadStatus.isEmpty,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn() + slideInVertically(
                animationSpec =  spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                ),
            ){
                it / 3
            },
            exit = slideOutVertically() + fadeOut(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 140.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = if ((LocalActivity.current as MainActivity).isDarkMode) painterResource(id = R.drawable.nodata_dark)
                    else painterResource(id = R.drawable.nodata_light),
                    modifier = Modifier.size(100.dp),
                    contentDescription = null,
                )
                Text(
                    color = colorScheme.onSurfaceVariantSummary,
                    fontWeight = FontWeight.Medium,
                    text = stringResource(R.string.empty),
                )
            }

        }
        AnimatedVisibility(
            loadStatus.isComplete() && !loadStatus.isEmpty,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn() + slideInVertically(
                animationSpec =  spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                ),
            ){
                it / 3
            },
            exit = slideOutVertically() + fadeOut(),
        ) {
            content()

        }
    }

}
