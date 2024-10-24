package com.yunzia.hyperstar.ui.base

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * 循环滚动跑马灯Text控件
 *
 * @param text 文字内容
 * @param modifier 控件修饰器
 * @param textModifier 文字修饰器
 * @param gradientEdgeColor 左右边界渐变透明色，默认白色渐变。
 * @param color 文字颜色
 * @param letterSpacing 字符间距
 * @param textDecoration 文字装饰
 */
@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    gradientEdgeColor: Color = Color.White,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    // 创建Text控件方法，相当于@Composable fun createText(localModifier: Modifier)
    val createText = @Composable { localModifier: Modifier ->
        Text(
            text,
            textAlign = textAlign,
            modifier = localModifier,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = 1,
            onTextLayout = onTextLayout,
            style = style,
        )
    }

    var offset by remember { mutableStateOf(0) }
    val textLayoutInfoState = remember { mutableStateOf<TextLayoutInfo?>(null) }
    LaunchedEffect(textLayoutInfoState.value) {
        val textLayoutInfo = textLayoutInfoState.value ?: return@LaunchedEffect
        if (textLayoutInfo.textWidth <= textLayoutInfo.containerWidth) return@LaunchedEffect
        if(textLayoutInfo.containerWidth == 0) return@LaunchedEffect
        // 计算播放一遍的总时间
        val duration = 7500 * textLayoutInfo.textWidth / textLayoutInfo.containerWidth// 父层不要有其他元素，不然这句很容易发生Error java.lang.ArithmeticException: divide by zero（除以零）
        // 动画间隔时间
        val delay = 1000L

        do {
            // 定义动画，文字偏移量从0到-文本宽度
            val animation = TargetBasedAnimation(
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = duration,
                        delayMillis = 1000,
                        easing = LinearEasing,
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                typeConverter = Int.VectorConverter,
                initialValue = 0,
                targetValue = -textLayoutInfo.textWidth
            )
            // 根据动画帧时间，获取偏移量值。
            // 起始帧时间
            val startTime = withFrameNanos { it }
            do {
                val playTime = withFrameNanos { it } - startTime
                offset = animation.getValueFromNanos(playTime)
            } while (!animation.isFinishedFromNanos(playTime))
            // 延迟重新播放
            delay(delay)
        } while (true)
    }

    SubcomposeLayout(
        modifier = modifier.clipToBounds()
    ) { constraints ->
        // 测量文本总宽度
        val infiniteWidthConstraints = constraints.copy(maxWidth = Int.MAX_VALUE)
        var mainText = subcompose(MarqueeLayers.MainText) {
            createText(textModifier)
        }.first().measure(infiniteWidthConstraints)

        var gradient: Placeable? = null

        var secondPlaceableWithOffset: Pair<Placeable, Int>? = null
        if (mainText.width <= constraints.maxWidth) {// 文本宽度小于容器最大宽度， 则无需跑马灯动画
            mainText = subcompose(MarqueeLayers.SecondaryText) {
                createText(textModifier.fillMaxWidth())
            }.first().measure(constraints)
            textLayoutInfoState.value = null
        } else {
            // 循环文本增加间隔
            val spacing = constraints.maxWidth * 2 / 3
            textLayoutInfoState.value = TextLayoutInfo(
                textWidth = mainText.width + spacing,
                containerWidth = constraints.maxWidth
            )
            // 第二遍文本偏移量
            val secondTextOffset = mainText.width + offset + spacing
            val secondTextSpace = constraints.maxWidth - secondTextOffset
            if (secondTextSpace > 0) {
                secondPlaceableWithOffset = subcompose(MarqueeLayers.SecondaryText) {
                    createText(textModifier)
                }.first().measure(infiniteWidthConstraints) to secondTextOffset
            }
            // 测量左右两边渐变控件
            gradient = subcompose(MarqueeLayers.EdgesGradient) {
                Row {
                    GradientEdge(gradientEdgeColor, Color.Transparent)
                    Spacer(Modifier.weight(1f))
                    GradientEdge(Color.Transparent, gradientEdgeColor)
                }
            }.first().measure(constraints.copy(maxHeight = mainText.height))
        }

        // 将文本、渐变控件 进行位置布局
        layout(
            width = constraints.maxWidth,
            height = mainText.height
        ) {
            mainText.place(offset, 0)
            secondPlaceableWithOffset?.let {
                it.first.place(it.second, 0)
            }
            gradient?.place(0, 0)
        }
    }
}

/**
 * 渐变侧边
 */
@Composable
private fun GradientEdge(
    startColor: Color, endColor: Color,
) {
    Box(
        modifier = Modifier
            .width(10.dp)
            .fillMaxHeight()
            .background(
                brush = Brush.horizontalGradient(
                    0f to startColor, 1f to endColor,
                )
            )
    )
}

private enum class MarqueeLayers { MainText, SecondaryText, EdgesGradient }

/**
 * 文字布局信息
 * @param textWidth 文本宽度
 * @param containerWidth 容器宽度
 */
private data class TextLayoutInfo(val textWidth: Int, val containerWidth: Int)
