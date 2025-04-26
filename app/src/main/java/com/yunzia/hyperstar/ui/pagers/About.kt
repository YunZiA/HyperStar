package com.yunzia.hyperstar.ui.pagers

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.PagerList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.LinearImage
import com.yunzia.hyperstar.ui.base.SuperIntentArrow
import com.yunzia.hyperstar.ui.base.SuperNavHostArrow
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.ui.base.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.base.view.BgEffectView
import kotlinx.coroutines.flow.onEach
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize
import java.util.Scanner


private fun getColorList(
    colorMode: Int):  List<Color>{
    if (colorMode == 2) {
        return listOf(
            Color("#D0A279ED".toColorInt()),
            Color("#D0E3BCB1".toColorInt())
        )
    } else {
        return listOf(
            Color("#D03A18AD".toColorInt()),
            Color("#D0A56138".toColorInt())
        )
    }
}


@SuppressLint("RestrictedApi", "SetTextI18n")
@Composable
fun ThirdPage(
    navController: NavHostController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    showBlurs: MutableState<Boolean>,
) {

    val context = navController.context
    val activity = LocalActivity.current as MainActivity

    val darkTheme = isSystemInDarkTheme()
    val colorsMode = when (activity.colorMode.intValue) {
        1 -> 1
        2 -> 2
        else -> if (darkTheme) 2 else 1
    }



    val density = LocalDensity.current
    val min = with(density) { 0.dp.toPx() }
    val sec = with(density) { 100.dp.toPx() }
    val main = with(density) { 160.dp.toPx() }
    val mainHeight = main-sec

    val bgHeight = with(density) {  332.dp.toPx() }

    val bgAlpha = remember { mutableFloatStateOf(1f) }

    val mainAlpha = remember { mutableFloatStateOf(1f) }
    val mainScale = remember { mutableFloatStateOf(1f) }

    val secAlpha = remember { mutableFloatStateOf(1f) }
    val secScale = remember { mutableFloatStateOf(1f) }

    val scroll = rememberLazyListState()

    LaunchedEffect(scroll) {
        snapshotFlow { scroll.firstVisibleItemScrollOffset }
            .onEach {

                Log.d("ggc", "ThirdPage: $it")
                if (scroll.firstVisibleItemIndex > 0){
                    bgAlpha.floatValue = 0f
                    secAlpha.floatValue = 0f
                    mainAlpha.floatValue = 0f
                    //showBlurs.value = true
                    return@onEach
                }
                val alpha = ((bgHeight-it.toFloat().coerceIn(min,bgHeight))/ bgHeight).coerceIn(0f, 1f)
                bgAlpha.floatValue = alpha
                showBlurs.value = alpha != 0f
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


//        val mResources = context.resources
//        val loadShader: String = loadShader(mResources, R.raw.bg_frag).toString()
//        BoxWithConstraints(
//            Modifier
//                .fillMaxWidth()
//                .height(430.dp)
//        ) {
//            val appContext = context.applicationContext
//            val mBgEffectPainter = BgEffectPainter(appContext)
//            mBgEffectPainter.showRuntimeShader(appContext, maxHeight.value,maxWidth.value, colorMode.intValue)
//
//            val renderEffects = remember { mutableStateOf(mBgEffectPainter.renderEffect.asComposeRenderEffect()) }
//            val startTime = System.nanoTime().toFloat()
//            val coroutineScope = rememberCoroutineScope()
//
//            LaunchedEffect(Unit) {
////                mBgEffectPainter = BgEffectPainter(appContext)
////                mBgEffectPainter.showRuntimeShader(appContext, maxHeight.value,maxWidth.value, colorMode.intValue)
//
//                // 使用协程的周期函数来模拟Handler.postDelayed的行为
//                coroutineScope.launch {
//                    while (true) { // 注意：这里需要一个适当的停止条件，否则会导致无限循环
//                        mBgEffectPainter.setAnimTime(
//                            (((System.nanoTime().toFloat()) - startTime) / 1.0E9f) % 62.831852f
//                        )
//                        mBgEffectPainter.setResolution(
//                            floatArrayOf(
//                                maxWidth.value,
//                                maxHeight.value
//                            )
//                        )
//                        mBgEffectPainter.updateMaterials()
//                        renderEffects.value = mBgEffectPainter.renderEffect.asComposeRenderEffect()
//                        Log.d("ggc", "ThirdPage:  change")
//                        delay(16L)
//                    }
//                }
//            }
//            LaunchedEffect(renderEffects.value) {
//            }
//
//            Box(
//                Modifier
//                    .fillMaxWidth()
//                    .height(430.dp)
//                    .graphicsLayer {
//                        clip = false
//                        renderEffect = renderEffects.value
//                    }
//
//            ) {  }
//
//
//        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp), // Occupy the max size in the Compose UI tree
            factory = { context ->
                BgEffectView(context,colorsMode)

            }
        ) {
            it.updateMode(colorsMode)
            it.alpha = bgAlpha.floatValue

        }

        //ShaderBrush()



        Column(
            modifier = Modifier
                .padding(top = 55.dp)
                .fillMaxWidth()
                .height(420.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            LinearImage(
                contentDescription = "",
                painter = painterResource(R.drawable.hyperstar2),
                alpha = mainAlpha.floatValue,
                colors = getColorList(colorMode = colorsMode),
                modifier = Modifier
                    .width(260.dp)
                    .scale(mainScale.floatValue),
            )

            Text(
                text = stringResource(R.string.xposed_desc),
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(secScale.floatValue)
                    .alpha(secAlpha.floatValue)
                    .padding(top = 20.dp),
                fontWeight = FontWeight.Medium,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                textAlign = TextAlign.Center
            )
//            Box(modifier = Modifier
//                .size(60.dp)
//                .background(Color.Red)
//                .clickable() {
//                    val artifactUrl =
//                        "https://api.github.com/repos/YunZiA/HyperStar/actions/artifacts/{artifact_id}/zip"
//                    val outputFileName = "artifact.zip"
//                    try {
//                        activity.downloadArtifactWithoutToken(artifactUrl, outputFileName)
//                    } catch (e: Exception) {
//                        Log.d("ggc", "ThirdPage: $e")
//                        e.printStackTrace()
//                    }
//                })
        }


        LazyColumn(
            modifier = Modifier
                .height(getWindowSize().height.dp)
                .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
            state = scroll,
            contentPadding = PaddingValues(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding()+16.dp),
        ) {

            item {
                Spacer(Modifier.height(360.dp))
            }

            firstClasses(
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
                    title = stringResource(R.string.telegram_channel),
                    navController = navController,
                    url = "https://t.me/HyperStar_release"
                )
                SuperIntentArrow(
                    title = stringResource(R.string.telegram_group),
                    navController = navController,
                    url = "https://t.me/Hyperstar_chat"
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


fun Modifier.runtimeShader(
    shader: RenderEffect
): Modifier = this.composed{
    //val runtimeShader = remember { RuntimeShader(shader) }
    //val shaderUniformProvider = remember { ShaderUniformProviderImpl(runtimeShader) }
    graphicsLayer {
        clip = true
        renderEffect = shader.asComposeRenderEffect()
    }
}


private fun loadShader(resources: Resources, i: Int): String? {
    try {
        val openRawResource = resources.openRawResource(i)
        try {
            val scanner = Scanner(openRawResource)
            try {
                val sb = StringBuilder()
                while (scanner.hasNextLine()) {
                    sb.append(scanner.nextLine())
                    sb.append("\n")
                }
                val sb2 = sb.toString()
                scanner.close()
                openRawResource.close()
                return sb2
            } finally {
            }
        } finally {
        }
    } catch (e: Exception) {
        Log.e("Error", e.toString())
        return null
    }
}

// ShaderModifier.kt
interface ShaderUniformProvider {
    fun uniform(name: String, value: Int)
    fun uniform(name: String, value: Float)
    fun uniform(name: String, value1: Float, value2: Float)
    fun uniform(name: String, color: Color)
}

private class ShaderUniformProviderImpl(
    private val runtimeShader: RuntimeShader,
) : ShaderUniformProvider {

    fun updateResolution(size: Size) {
        uniform("resolution", size.width, size.height)
    }

    override fun uniform(name: String, value: Int) {
        runtimeShader.setIntUniform(name, value)
    }

    override fun uniform(name: String, value: Float) {
        runtimeShader.setFloatUniform(name, value)
    }

    override fun uniform(name: String, value1: Float, value2: Float) {
        runtimeShader.setFloatUniform(name, value1, value2)
    }

    override fun uniform(name: String, color: Color) {
        val colorArray = floatArrayOf(color.red, color.green, color.blue, color.alpha)
        val colorArray3 = floatArrayOf(color.red, color.green, color.blue)
        runtimeShader.setFloatUniform(name, colorArray)
        // runtimeShader.setColorUniform(name, color.toArgb())
        // runtimeShader.setFloatUniform(name, colorArray3)
    }
}
