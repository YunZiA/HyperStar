package com.yunzia.hyperstar.ui.screen.welcome

import android.view.HapticFeedbackConstants
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.Button
import com.yunzia.hyperstar.ui.component.enums.EventState
import com.yunzia.hyperstar.ui.component.modifier.bounceClick
import com.yunzia.hyperstar.ui.component.modifier.bounceScale
import com.yunzia.hyperstar.ui.miuiStrongToast.MiuiStrongToast
import com.yunzia.hyperstar.utils.JBUtil
import com.yunzia.hyperstar.utils.JBUtil.openFile
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun ProviderPage(
    pagerState: PagerState
) {

    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalActivity.current as MainActivity
    val context = LocalContext.current
    val success = remember { mutableStateOf(false) }
    val eventState = remember { mutableStateOf(EventState.Idle) }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result ->
        if (result == null) return@rememberLauncherForActivityResult
        success.value = JBUtil.readGson(context, result)

        //activity.recreate()

    }
    LaunchedEffect(
        success.value
    ) {
        if (success.value){
            MiuiStrongToast.showStrongToast(context, context.getString(R.string.import_success))
        }
    }



    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
        ) {
            Icon(
                ImageVector.vectorResource(R.drawable.provision_perview_view),
                contentDescription = "language",
                tint = Color(0xFF3482FF)
            )

        }
        Text(
            text = stringResource(R.string.data_import),
            modifier = Modifier.padding(vertical = 10.dp),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp)
                .padding(bottom = 10.dp)
        ) {

            item{
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 10.dp)
                        .bounceScale(eventState),
                    colors = CardDefaults.defaultColors(colorScheme.surfaceVariant)
                ) {

                    Row(

                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .bounceClick(eventState)
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                openFile(activity, launcher)
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp, end = 12.dp)
                                .padding(vertical = 12.dp)
                        ){
                            Image(
                                modifier = Modifier.size(40.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.ic_provision_mover),
                                //MiuixIcons.ArrowRight,
                                contentDescription = null,
                                //colorFilter = ColorFilter.tint( colorScheme.onBackground),
                            )
                        }
                        Text(
                            text = stringResource(R.string.import_from_local),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp),
                            color = colorScheme.onBackground
                        )
                        Image(
                            modifier = Modifier
                                .width(40.dp)
                                .padding(vertical = 12.dp)
                                .padding(end = 12.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.arrow_right),
                            //MiuixIcons.ArrowRight,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint( colorScheme.onBackground),
                        )

                    }


                }

            }

        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 28.dp),
            colors = Color(0xFF3482FF),
            onClick = {

                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                coroutineScope.launch {
                    pagerState.animateScrollToPage(4)
                }

            }
        ) {
            Text(
                text = stringResource(R.string.next),
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

    }

}


