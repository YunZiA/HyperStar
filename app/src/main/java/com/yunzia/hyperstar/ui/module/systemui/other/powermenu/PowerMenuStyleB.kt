package com.yunzia.hyperstar.ui.module.systemui.other.powermenu

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.base.SuperBottomSheetDialog
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape


@Composable
fun PowerMenuStyleB() {

    val selectItem = remember {
        mutableIntStateOf(-1)
    }
    val showS =  remember {
        derivedStateOf {
        selectItem.intValue != -1
    }}
    val mContext = LocalContext.current
    Toast.makeText(mContext,"${selectItem.intValue}",Toast.LENGTH_SHORT).show()

    if (selectItem.intValue != -1){

//        showDialog{
//            SuperBottomSheetDialog(
//                show = selectItem.intValue.equals(-1),
//                onDismissRequest = {
//
//                }
//            ){
//
//            }
//
//        }

    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val items=(1..100).toList()
        LazyVerticalGrid(
            modifier = Modifier.padding(horizontal = 20.dp),
            columns = GridCells.Fixed(4),
            userScrollEnabled = false,
            contentPadding =  PaddingValues(10.dp),

            ) {
            items(4){
                Box(modifier = Modifier
                    .size(70.dp),
                    contentAlignment = Alignment.Center
                ){
                    Box(modifier = Modifier
                        .size(70.dp) // 设置为正方形大小
                        .border(2.dp ,if (selectItem.intValue == it)  Color.Blue else Color.Transparent ,RoundedCornerShape(70.dp))
                        .padding(if (selectItem.intValue == it)  5.dp else 0.dp)
                        .clip(RoundedCornerShape(70.dp))
                        .background(colorScheme.secondary)
                        .clickable {
                            selectItem.intValue = it
                        },
                        contentAlignment = Alignment.Center
                    ){
                        Text(text = it.toString())
                    }
                }
            }

        }
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(35.dp)
                .padding(bottom = 8.dp)
                .clip(SmoothRoundedCornerShape(6.dp, 0.5f))
                .background(colorScheme.secondary),
            contentAlignment = Alignment.Center
        ){}
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(300.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "样式2")

        }
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(35.dp)
                .padding(top = 8.dp)
                .clip(SmoothRoundedCornerShape(6.dp, 0.5f))
                .background(colorScheme.secondary),
            contentAlignment = Alignment.Center
        ){}
        LazyVerticalGrid(
            modifier = Modifier.padding(horizontal = 20.dp),
            columns = GridCells.Fixed(4),
            userScrollEnabled = false,
            contentPadding =  PaddingValues(10.dp),

            ) {
            items(4){
                val local = it+4
                Box(modifier = Modifier
                    .size(70.dp),
                    contentAlignment = Alignment.Center
                ){
                    Box(modifier = Modifier
                        .size(70.dp) // 设置为正方形大小
                        .border(2.dp ,if (selectItem.intValue == local)  Color.Blue else Color.Transparent ,RoundedCornerShape(70.dp))
                        .padding(if (selectItem.intValue == local)  5.dp else 0.dp)
                        .clip(RoundedCornerShape(70.dp))
                        .background(colorScheme.secondary)
                        .clickable {
                            selectItem.intValue = local
                        },
                        contentAlignment = Alignment.Center
                    ){
                        Text(text = local.toString())
                    }
                }
            }

        }

    }

}