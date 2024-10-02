package com.chaos.hyperstar.ui.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaos.hyperstar.R
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun XMiuixClass(
    title : String,
    bottom : Dp = 0.dp,
    useLine : Boolean
){
    if (useLine){
        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 27.dp)
                .padding(top = 18.dp, bottom = 12.dp),
            thickness = 0.8.dp,
            color = colorResource(R.color.sec_color)
        )

    }
    Text(
        text = title,
        modifier = Modifier
            .padding(horizontal = 28.dp)
            .padding(top = 14.dp, bottom = 6.dp + bottom),
        fontSize = 13.sp,
        color = colorResource(R.color.class_name_color),
        fontWeight = FontWeight.Medium
    )
}

fun LazyListScope.firstClasses(
    title : String,
    top : Dp = 0.dp,
    bottom : Dp = 0.dp,
    content: @Composable (() -> Unit),
){
    classes(
        title = title,
        top = top,
        bottom = bottom,
        content = content
    )
}

fun LazyListScope.classes(
    title : String,
    top : Dp = 12.dp,
    bottom : Dp = 0.dp,
    content: @Composable (() -> Unit),
){

    item{
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = top, bottom = bottom),
            insideMargin = DpSize(0.dp,14.dp)
        ) {

            Text(
                text = title,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 10.dp, bottom = 8.dp),
                fontSize = 15.sp,
                color = colorResource(R.color.class_name_color),
                fontWeight = FontWeight.Medium
            )
            content()


        }
    }

}

@Composable
fun XMiuixClasser(
    title : String,
    top : Dp = 0.dp,
    bottom : Dp = 0.dp,
    content: @Composable (() -> Unit),
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = top, bottom = bottom),
        insideMargin = DpSize(0.dp,14.dp)
    ) {

        Text(
            text = title,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 10.dp, bottom = 8.dp),
            fontSize = 15.sp,
            color = colorResource(R.color.class_name_color),
            fontWeight = FontWeight.Medium
        )
        content()


    }
}

@Composable
fun XMiuixClasser(
    title : String,
    summary : String ?= null,
    top : Dp = 0.dp,
    bottom : Dp = 0.dp,
    content: @Composable (() -> Unit),
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = top, bottom = bottom),
        insideMargin = DpSize(0.dp,14.dp)
    ) {

        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
                .padding(top = 10.dp, bottom = 7.dp),
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(bottom = 1.dp),
                fontSize = 15.sp,
                color = colorResource(R.color.class_name_color),
                fontWeight = FontWeight.Medium
            )
            summary?.let {
                Text(
                    text = it,
                    modifier = Modifier
                        .padding(bottom = 1.dp),
                    fontSize = 11.sp,
                    color = colorResource(R.color.class_name_color),
                    fontWeight = FontWeight.Medium
                )
            }

        }

        content()


    }
}