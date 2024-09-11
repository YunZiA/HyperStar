package com.chaos.hyperstar.ui.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaos.hyperstar.utils.PreferencesUtil
import com.chaos.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.MiuixSlider
import top.yukonga.miuix.kmp.basic.MiuixText
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.rememberOverscrollFlingBehavior

@Composable
fun XMiuixSlider(
    title : String,
    key : String,
    unit : String = "",
    minValue: Float = 0f,
    maxValue: Float = 1f,
    progress: Float = 0.5f,
    decimalPlaces : Int = 0

) {
    val effect = PreferencesUtil.getBoolean("is_progress_effect",false)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp)
    ){
        var x_progress by remember { mutableStateOf(SPUtils.getFloat(key,progress)) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
        ) {
            MiuixText(
                modifier = Modifier.weight(1f),
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp)
            MiuixText(
                text = if (decimalPlaces == 0) x_progress.toInt().toString()+unit else x_progress.toString()+unit,
                textAlign = TextAlign.End,
                fontSize = 14.sp)
        }
        MiuixSlider(
            progress = x_progress,
            onProgressChange = {
                    newProgress -> x_progress = newProgress
                SPUtils.setFloat(key,x_progress)
            },
            effect = effect,
            maxValue = maxValue,
            minValue = minValue,
            //dragShow = true,
            decimalPlaces = decimalPlaces,
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(top = 10.dp)
        )
    }
}

