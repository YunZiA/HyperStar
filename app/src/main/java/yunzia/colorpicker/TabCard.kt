package yunzia.colorpicker

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape

@Composable
fun TabCard(
    modifier: Modifier,
    tabList: List<String>,
    local : Int,
    titleColor: Color = Color(0xFF666666),
    titleSelectColor : Color = Color(0xFF606060),
    tabColor: Color = Color.White,
    tabBgColor : Color = Color(0xFFF0F0F0),
    onTabChange : (Int) -> Unit
) {

    val selectedTab = remember { mutableIntStateOf(local) }

    val x =  animateFloatAsState(selectedTab.intValue.toFloat(), label = "")


    Box(modifier = modifier
        .background(tabBgColor,SquircleShape(8.dp))
        .padding(5.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            val width = size.width/tabList.size

            drawRoundRect(
                color = tabColor,
                cornerRadius = CornerRadius(12f,12f),
                topLeft =  Offset(x.value*width,0f),
                size = Size(width,size.height)
            )

        }
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            tabList.forEachIndexed { index, text ->
                val textColor = remember {
                    Animatable(Color.White)
                }
                LaunchedEffect(selectedTab.intValue) {
                    textColor.animateTo(if (selectedTab.intValue == index) titleSelectColor else titleColor)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Transparent)
                        .padding(vertical = 8.dp)
                        .pointerInput(Unit){
                            detectTapGestures (
                                onTap = {
                                    selectedTab.intValue = index
                                    onTabChange(selectedTab.intValue)
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = textColor.value

                    )
                }
            }

        }
    }

}

class NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<Interaction> = emptyFlow()
    override suspend fun emit(interaction: Interaction) {}
    override fun tryEmit(interaction: Interaction) = true
}

