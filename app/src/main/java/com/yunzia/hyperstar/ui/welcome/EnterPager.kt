package com.yunzia.hyperstar.ui.welcome

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.Button
import com.yunzia.hyperstar.ui.base.colorMode
import com.yunzia.hyperstar.utils.PreferencesUtil
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun EnterPager(show: MutableState<Boolean>, pagerState: PagerState) {
    val view = LocalView.current
    val easing  = CubicBezierEasing(.42f,0f,0.26f,.85f)
    val go = remember { mutableStateOf(false) }
    val animatedY =  animateDpAsState(
        targetValue = if (go.value) (-30).dp else 0.dp,
        animationSpec = tween(durationMillis = 1150,easing = easing),
        label = "alpha"
    )
    val animatedAlpha =  animateFloatAsState(
        targetValue = if (go.value) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1150,easing = easing),
        label = "alpha"
    )
    val darkTheme = isSystemInDarkTheme()
    val logo = when (colorMode.intValue) {
        1 -> painterResource(R.drawable.hyperstar2)
        2 -> painterResource(R.drawable.hyperstar2_dark)
        else -> if (darkTheme) painterResource(R.drawable.hyperstar2_dark) else painterResource(R.drawable.hyperstar2)
    }

    LaunchedEffect(
        pagerState.currentPage
    ) {
        if (pagerState.currentPage == 6){
            go.value = true
        }
    }

    Column {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 100.dp)
                .offset(x = 0.dp, y = animatedY.value)
                .alpha(animatedAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                contentDescription = "",
                painter = logo,
                modifier = Modifier
                    .width(260.dp)
            )
            Text(
                text = stringResource(R.string.setup_successful),
                modifier = Modifier.padding(top = 20.dp)
            )

        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 28.dp),
            colors = Color(0xFF3482FF),
            onClick = {

                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                show.value = false
                PreferencesUtil.putBoolean("is_first_use",false)
            }
        ) {
            Text(
                text = stringResource(R.string.enter_module),
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }

}