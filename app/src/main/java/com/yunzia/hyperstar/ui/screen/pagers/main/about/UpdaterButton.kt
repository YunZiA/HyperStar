package com.yunzia.hyperstar.ui.screen.pagers.main.about

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.BaseActivity
import com.yunzia.hyperstar.ui.component.Button
import com.yunzia.hyperstar.ui.navigation.MainRoutes
import com.yunzia.hyperstar.ui.navigation.Navigator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme


@Composable
fun UpdaterButton(
    modifier: Modifier = Modifier,
    navController: Navigator
){
    val activity = LocalActivity.current as BaseActivity

    val isDark = activity.isDarkMode

    val shadowColor = if (isDark){
        Color(0x4d000000)
    }else{
        Color(0x40000000)
    }

    val backgroundColor = if (isDark){
        Color(0x1fffffff)
    }else{
        Color(0x99ffffff)
    }

    val borderColor = if (isDark){
        integerArrayResource(R.array.my_card_stroke_gradient_colors_dark)
    }else{
        integerArrayResource(R.array.my_card_stroke_gradient_colors_light)
    }

    Button(
        modifier = Modifier
            .wrapContentHeight()
            .drawBehind {
                val gradientBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(borderColor[1]),
                        Color(borderColor[0]),
                    ),
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, Float.POSITIVE_INFINITY)
                )

                val strokeWidth = 1.5.dp.toPx()
                val inset = strokeWidth / 2

                drawRoundRect(
                    brush = gradientBrush,
                    topLeft = Offset(inset, inset),
                    size = Size(
                        size.width - strokeWidth,
                        size.height - strokeWidth
                    ),
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = Stroke(width = strokeWidth)
                )

            }
            .shadow(
                elevation = 1.5.dp,
                shape = SmoothRoundedCornerShape(16.dp),
                clip = true,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .then(modifier)
        ,
        colors = backgroundColor,
        minHeight = 52.dp,
        minWidth = 250.dp,
        onClick = {
            navController.navigate(MainRoutes.Updater)
        }
    ){
        Text(
            text = stringResource(R.string.update_has),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )

    }

}