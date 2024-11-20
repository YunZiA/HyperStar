package com.yunzia.hyperstar.ui.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.R
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text

fun LazyListScope.emptyClasses(
    top : Dp = 0.dp,
    bottom : Dp = 0.dp,
    content: @Composable (() -> Unit),
){
    item{
        Card(
            Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = top, bottom = bottom)
        ) {
            content()
        }
    }
}

fun LazyListScope.firstClasses(
    title : Any ? = null,
    summary : Any ? = null,
    top : Dp = 0.dp,
    bottom : Dp = 0.dp,
    content: @Composable (() -> Unit),
){
    classes(
        title = title,
        summary = summary,
        top = top,
        bottom = bottom,
        content = content
    )
}


fun LazyListScope.classes(
    title : Any ? = null,
    summary : Any ? = null,
    top : Dp = 12.dp,
    bottom : Dp = 0.dp,
    content: @Composable (() -> Unit),
){



    item{
        Classes(
            title =  when (title){
                is String -> title
                is Int -> stringResource(id = title)
                else -> null
            },
            summary = when (summary){
                is String -> summary
                is Int -> stringResource(id = summary)
                else -> null
            },
            top = top,
            bottom = bottom,
            content = content
        )
    }

}

@Composable
fun Classes(
    title : String? = null,
    summary : String? = null,
    top : Dp = 0.dp,
    bottom : Dp = 0.dp,
    content: @Composable (() -> Unit),
){

    val insideMargin = if (title != null || summary != null ) PaddingValues(0.dp,14.dp)  else PaddingValues(0.dp,0.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = top, bottom = bottom),
        insideMargin = insideMargin,
        cornerRadius = 21.dp
    ) {
        if (title != null || summary != null ){

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 10.dp, bottom = 7.dp),
            ) {
                title?.let {
                    Text(
                        text = it,
                        modifier = Modifier
                            .padding(bottom = 1.dp),
                        fontSize = 15.sp,
                        color = colorResource(R.color.class_name_color),
                        fontWeight = FontWeight.Medium
                    )
                }
                summary?.let {
                    Text(
                        text = it,
                        modifier = Modifier
                            .padding(bottom = 1.dp),
                        fontSize = 11.sp,
                        lineHeight =  1.5.em,
                        color = colorResource(R.color.class_name_color),
                        fontWeight = FontWeight.Medium
                    )
                }

            }

        }

        content()


    }
}