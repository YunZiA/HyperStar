package com.yunzia.hyperstar.ui.pagers

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.PagerList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.SuperIntentArrow
import com.yunzia.hyperstar.ui.base.SuperNavHostArrow
import com.yunzia.hyperstar.ui.base.classes
import kotlinx.coroutines.flow.onEach
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize

@SuppressLint("RestrictedApi")
@Composable
fun ThirdPage(
    activity : ComponentActivity,
    navController: NavHostController,
    topAppBarScrollBehavior: ScrollBehavior,
    showBlurs: MutableState<Boolean>,
    padding: PaddingValues
) {

    val density = LocalDensity.current
    val min = with(density) { 0.dp.toPx() }
    val sec = with(density) { 110.dp.toPx() }
    val main = with(density) { 150.dp.toPx() }
    val mainHeight = main-sec

    val bgHeight = with(density) {  312.dp.toPx() }

    val bgAlpha = remember { mutableFloatStateOf(1f) }

    val mainAlpha = remember { mutableFloatStateOf(1f) }
    val mainScale = remember { mutableFloatStateOf(1f) }

    val secAlpha = remember { mutableFloatStateOf(1f) }
    val secScale = remember { mutableFloatStateOf(1f) }


    val scroll = rememberLazyListState()

    LaunchedEffect(scroll) {
        snapshotFlow { scroll.firstVisibleItemScrollOffset }
            .onEach {

                Log.d("ggc", "ThirdPage: ${it}")
                if (scroll.firstVisibleItemIndex > 0){
                    //showBlurs.value = true
                    bgAlpha.floatValue = 0f
                    secAlpha.floatValue = 0f
                    mainAlpha.floatValue = 0f
                    return@onEach
                }
                val alpha = ((bgHeight-it.toFloat().coerceIn(min,bgHeight))/ bgHeight).coerceIn(0f, 1f)
                showBlurs.value = alpha == 0f
                bgAlpha.floatValue = alpha
                val secValue =  ((sec-it.toFloat().coerceIn(min,sec))/ sec).coerceIn(0f, 1f)

                secAlpha.floatValue = secValue
                secScale.floatValue = lerp(0.9f,1f,secValue)

                val mainValue =  ((main-it.toFloat().coerceIn(sec,main))/ mainHeight).coerceIn(0f, 1f)

                mainAlpha.floatValue = mainValue
                mainScale.floatValue = lerp(0.9f,1f,mainValue)

            }.collect {

            }
    }

    Box(Modifier.clip(RoundedCornerShape(0.dp))) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp)
                .height(420.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        )  {
            Row(
                modifier = Modifier
                    .alpha(mainAlpha.floatValue)
                    .scale(mainScale.floatValue),
            ){

                Text(
                    text = "Hyper",
                    fontSize = 45.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Star",
                    fontSize = 45.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.blue),
                )

            }

            Text(
                text = stringResource(id = R.string.xposed_desc),
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp)
                    .alpha(secAlpha.floatValue)
                    .scale(secScale.floatValue),
                fontWeight = FontWeight.Medium,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                textAlign = TextAlign.Center
            )

        }

        LazyColumn(
            modifier = Modifier.height(getWindowSize().height.dp),
            state = scroll,
            contentPadding = PaddingValues(top = 0.dp, bottom = padding.calculateBottomPadding()+16.dp),
            topAppBarScrollBehavior = topAppBarScrollBehavior
        ) {
            item {
                Spacer(Modifier.height(padding.calculateTopPadding()+332.dp))
            }
            classes(
                title = R.string.developer_title
            ){
                SuperIntentArrow(
                    leftIcon = R.drawable.dd,
                    title = "东东说他舍不得",
                    summary = "@YunZiA | Hook",
                    navController = navController,
                    url = "coolmarket://u/8555749"
                )
                SuperNavHostArrow(
                    title = stringResource(R.string.translator),
                    navController = navController,
                    route = PagerList.TRANSLATOR

                )


            }

            classes(
                title = R.string.discussion_title
            ){
                SuperIntentArrow(
                    title = stringResource(R.string.qq_group_title),
                    navController = navController,
                    url = "http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&amp;k=5ONF7LuaoQS6RWEOUYBuA0x4X8ssvHJp&amp;authKey=Pic4VQJxKBJwSjFzsIzbJ50ILs0vAEPjdC8Nat4zmiuJRlftqz9%2FKjrBwZPQTc4I&amp;noverify=0&amp;group_code=810317966"
                )
                SuperIntentArrow(
                    title = "Telegram",
                    navController = navController,
                    url = "https://t.me/+QQWVM0ToHyEyZmRl"
                )

            }

            classes(
                title = R.string.others
            ) {
                SuperNavHostArrow(
                    title = stringResource(R.string.references_title),
                    navController = navController,
                    route = PagerList.REFERENCES

                )
                SuperIntentArrow(
                    title = stringResource(R.string.project_address),
                    summary = stringResource(R.string.open_source_statement),
                    navController = navController,
                    url = "https://github.com/YunZiA/HyperStar"
                )
                SuperNavHostArrow(
                    title = stringResource(R.string.donation),
                    navController = navController,
                    route = PagerList.DONATION

                )
            }


        }
    }
}
