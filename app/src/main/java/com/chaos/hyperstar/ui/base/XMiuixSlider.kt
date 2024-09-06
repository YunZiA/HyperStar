package com.chaos.hyperstar.ui.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaos.hyperstar.utils.PreferencesUtil
import com.chaos.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.basic.MiuixSlider
import top.yukonga.miuix.kmp.basic.MiuixText

@Composable
fun XMiuixSlider(
    title : String,
    key : String,
    unit : String = "",
    minValue: Float = 0f,
    maxValue: Float = 1f,
    progress: Float = 0.5f

) {
    val effect = PreferencesUtil.getBoolean("is_progress_effect",false)
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp)
    ){
        var x_progress by remember { mutableStateOf(progress) }
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 28.dp)
        ) {
            MiuixText(
                modifier = Modifier.weight(1f),
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp)
            MiuixText(
                text = x_progress.toString()+unit,
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
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(top = 10.dp)
        )
    }
}