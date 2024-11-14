package com.yunzia.hyperstar.ui.base

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

@Composable
fun LinearImage(
    painter: Painter,
    colors: List<Color>,
    contentDescription: String,
    modifier: Modifier,
    alignment: Alignment = Alignment. Center,
    contentScale: ContentScale = ContentScale. Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {

    Image(
        painter = painter,
        contentDescription = contentDescription,
        alignment=alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        modifier = modifier.drawWithContent {
                drawIntoCanvas { canvas ->
                    canvas.saveLayer(Rect(Offset.Infinite,size), paint = Paint())
                    drawContent()
                    drawRect(
                        brush = Brush.linearGradient(
                            start = Offset(0f, size.height / 2f),
                            end = Offset(size.width, size.height / 2f),
                            colors = colors
                        ), blendMode = BlendMode.SrcIn
                    )
                    canvas.restore()
                }

            }
    )

}