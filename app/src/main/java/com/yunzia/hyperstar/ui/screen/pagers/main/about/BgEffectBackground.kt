package com.yunzia.hyperstar.ui.screen.pagers.main.about

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.blend.BlendMode
import com.yunzia.hyperstar.ui.blend.BlendedBlurPainter
import com.yunzia.hyperstar.ui.blend.blurShaderCode
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

private fun getLayerList(isDarkMode: Boolean): List<Pair<Color, BlendMode>> {
    return if (isDarkMode) {
        listOf(
            Pair(Color(0xe6a1a1a1), BlendMode.COLOR_DODGE),
            Pair(Color(0x4de6e6e6), BlendMode.LINEAR_LIGHT),
            Pair(Color(0xff1af500), BlendMode.LAB)
        )
    } else {
        listOf(
            Pair(Color(0xcc4a4a4a), BlendMode.COLOR_BURN),
            Pair(Color(0xff4f4f4f), BlendMode.LINEAR_LIGHT),
            Pair(Color(0xff1af200), BlendMode.LAB)
        )
    }
}

data class BlendShaderBrush(
    val backgroundBrush: ShaderBrush,
    val textBrush: ShaderBrush
)



@Composable
fun BgEffectBackground(
    modifier: Modifier = Modifier,
    content:  @Composable (BoxScope.(MutableState<ShaderBrush?>,  MutableState<IntSize>, MutableState<Offset>) -> Unit)
) {
    val context = LocalContext.current
    val activity = LocalActivity.current as MainActivity

    val painter = remember(context) { BgEffectPainter(context.applicationContext) }
    val blendedBlurPainter = remember(context) { BlendedBlurPainter().apply {
        enableBlur()
        setBlurRadius(50f)
    } }
    val versionShaderBrush = remember { mutableStateOf<ShaderBrush?>(null) }
    val currentBrush: MutableState<ShaderBrush?> = remember { mutableStateOf(null) }

    var targetSize by remember { mutableStateOf(IntSize.Zero) }
    val versionSize = remember { mutableStateOf(IntSize.Zero) }
    val position = remember { mutableStateOf(Offset.Zero) }
    val layerList = remember(activity.isDarkMode) { getLayerList(activity.isDarkMode) }

    LaunchedEffect(targetSize, activity.isDarkMode, position.value) {
        if (targetSize.width > 0 && targetSize.height > 0) {
            painter.showRuntimeShader(
                context.applicationContext,
                targetSize.height.toFloat(),
                targetSize.width.toFloat(),
                activity.isDarkMode
            )
            painter.updateMode(activity.isDarkMode)
            blendedBlurPainter.setResolution(targetSize)
            blendedBlurPainter.setLayers(layerList)
            blendedBlurPainter.shader.apply {
                val matrix = Matrix()
                matrix.setTranslate((-(targetSize.width - versionSize.value.width)/2).toFloat(), (-position.value.y.toInt()).toFloat())
                setLocalMatrix(matrix)
            }

        }
    }

    LaunchedEffect(Unit) {
        val startTime = System.nanoTime()
        while (true) {
            val frameTime = System.nanoTime()
            val animTime = ((frameTime - startTime) / 1.0E9f) % 62.831852f
            if (targetSize.width > 0 && targetSize.height > 0) {
                painter.setAnimTime(animTime)
                painter.setResolution(floatArrayOf(targetSize.width.toFloat(), targetSize.height.toFloat()))
                blendedBlurPainter.setResolution(targetSize)
                painter.updateMaterials()
                painter.runtimeShader?.let { shader->
                    currentBrush.value = ShaderBrush(shader)
                    blendedBlurPainter.shader.apply {
                        setInputShader(
                            "inputImage",
                            shader
                        )
                        versionShaderBrush.value = ShaderBrush(this)
                    }
                }
                val elapsed = (System.nanoTime() - frameTime) / 1_000_000
                val delayTime = (16L - elapsed).coerceAtLeast(1L)
                delay(delayTime)
            }
        }
    }

    Box(
        modifier = modifier.onSizeChanged {
            targetSize = it
        }
    ) {
        currentBrush.value?.let { brush ->
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawRect(brush)
            }
            content(versionShaderBrush, versionSize, position)
        }
    }
}