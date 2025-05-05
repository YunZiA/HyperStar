package com.yunzia.hyperstar.viewmodel

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunzia.hyperstar.ui.base.card.TiltAnimationState
import com.yunzia.hyperstar.ui.base.helper.getSystemCornerRadius
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.getWindowSize

class UpdaterViewModel : ViewModel() {


    private val _uiState = MutableStateFlow(UpdateDetailUiState())
    val uiState: StateFlow<UpdateDetailUiState> = _uiState.asStateFlow()

    // 新增的UI状态
    data class UpdateDetailUiState(
        val newPageState: NewPageState = NewPageState(),
        val animationState: UpdatePageAnimationState? = null,
        val isScrollEnabled: Boolean = false,
        val isBlur: Boolean = false,
        val needCancel: MutableState<Boolean> = mutableStateOf(true)
    )

    // 页面事件
    sealed class UpdateDetailEvent {
        data class PageChanged(val page: Int) : UpdateDetailEvent()
        data class ScrollOffsetChanged(val offset: Float) : UpdateDetailEvent()
        data class AnimationCompleted(val radius: Dp,val finalRadius: Dp) : UpdateDetailEvent()
        data class SetScrollEnabled(val isScrollEnabled: Boolean) : UpdateDetailEvent()
        object NavigateToDetailPage : UpdateDetailEvent()
        object NavigateBack : UpdateDetailEvent()
        object CancelTiltEffect : UpdateDetailEvent()
    }

    fun handleEvent(event: UpdateDetailEvent) {
        viewModelScope.launch {
            when (event) {
                is UpdateDetailEvent.PageChanged -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            newPageState = currentState.newPageState.copy(
                                currentPage = event.page,
                                expand = event.page == 1
                            ),
                            isScrollEnabled = event.page != 1,
                            isBlur = false
                        )
                    }
                }

                is UpdateDetailEvent.ScrollOffsetChanged -> {
                    if (_uiState.value.newPageState.currentPage == 1) {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isBlur = event.offset < -20f
                            )
                        }
                    }
                }

                is UpdateDetailEvent.AnimationCompleted -> {
                    val currentState = _uiState.value
                    if (!currentState.newPageState.expand) {
                        _uiState.update {
                            it.copy(
                                newPageState = it.newPageState.copy(complete = false)
                            )
                        }
                        return@launch
                    }

                    if (event.radius == event.finalRadius) {
                        delay(300L)
                        _uiState.update {
                            it.copy(
                                newPageState = it.newPageState.copy(complete = true)
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                newPageState = it.newPageState.copy(complete = false)
                            )
                        }
                    }
                }
                is UpdateDetailEvent.NavigateToDetailPage -> {
                    viewModelScope.launch {
                        _uiState.update {
                            it.copy(
                                newPageState = it.newPageState.copy(
                                    currentPage = 1,
                                    expand = true,

                                ),
                                isBlur = false

                            )
                        }
                    }
                }
                is UpdateDetailEvent.NavigateBack -> {
                    viewModelScope.launch {
                        _uiState.update {
                            it.copy(
                                newPageState = it.newPageState.copy(
                                    currentPage = 0,
                                    expand = false
                                ),
                                isBlur = false
                            )
                        }
                    }
                }
                is UpdateDetailEvent.SetScrollEnabled -> {
                    _uiState.update {
                        it.copy(isScrollEnabled = event.isScrollEnabled)
                    }
                }

                UpdateDetailEvent.CancelTiltEffect -> {
                    _uiState.update { it.apply {
                        needCancel.value = false
                    } }
                    delay(150)
                    _uiState.update { it.apply {
                        needCancel.value = true
                    } }
                }
            }
        }
    }

    @Composable
    fun calculateAnimationState(
        padding: PaddingValues,
        tiltState: TiltAnimationState,
        newPageState: NewPageState
    ): UpdatePageAnimationState {
        return rememberUpdateDetailAnimations(
            newPageState = newPageState,
            padding = padding,
            tiltState = tiltState
        )
    }


}

@Composable
private fun rememberUpdateDetailAnimations(
    newPageState: NewPageState,
    padding: PaddingValues,
    tiltState: TiltAnimationState
): UpdatePageAnimationState {
    val durationMillis = 400
    val delayMillis = 0
    val SlowEasing = LinearOutSlowInEasing
    val easing = LinearOutSlowInEasing
    val headerTop = padding.calculateTopPadding() + 65.dp
    //CubicBezierEasing(0.2f, 0.0f, 0.2f, 1.0f)
    val alpha by animateFloatAsState(
        targetValue = if (newPageState.expand) 1f else 0f,
        animationSpec = tween(durationMillis = durationMillis, easing = easing)
    )

    val paddingTop by animateDpAsState(
        targetValue = if (newPageState.expand) 0.dp else padding.calculateTopPadding() + 2.dp,
        animationSpec = tween(durationMillis = durationMillis, easing = easing)
    )

    val paddingBottom by animateDpAsState(
        targetValue = if (newPageState.expand) 0.dp else padding.calculateBottomPadding() + 28.dp,
        animationSpec = tween(durationMillis = durationMillis, easing = easing)
    )

    val myRadius = getSystemCornerRadius()
    val windowHeight = with(LocalDensity.current) { getWindowSize().height.toDp() }

    val animatedValues = AnimatedValues(
        color = animateColorAsState(
            if (newPageState.expand) colorScheme.background else colorScheme.surface,
            animationSpec = tween(durationMillis = durationMillis, easing = easing)
        ).value,
        radius = animateDpAsState(
            if (newPageState.expand) myRadius else 16.dp,
            animationSpec = tween(durationMillis = durationMillis, easing = easing)
        ).value,
        cardHeight = animateDpAsState(
            if (newPageState.expand) windowHeight else headerTop + 500.dp,
            animationSpec = tween(durationMillis = durationMillis, easing = easing)
        ).value,
        top = animateDpAsState(
            targetValue = if (newPageState.expand) 0.dp else headerTop,
            animationSpec = tween(durationMillis = durationMillis, easing = easing)
        ).value,
        horizontal = animateDpAsState(
            targetValue = if (newPageState.expand) 0.dp else 28.dp,
            animationSpec = tween(durationMillis = durationMillis, easing = easing)
        ).value,
        titleTop = animateDpAsState(
            targetValue = if (newPageState.expand) padding.calculateTopPadding() + 28.dp else 80.dp,
            animationSpec = tween(durationMillis = durationMillis, easing = easing)
        ).value
    )

    // 倾斜动画值
    val rotationX by animateFloatAsState(
        targetValue = if (newPageState.expand) 0f else tiltState.rotationX.value,
        animationSpec = tween(durationMillis = durationMillis + delayMillis, delayMillis = delayMillis, easing = easing)
    )
    val rotationY by animateFloatAsState(
        targetValue = if (newPageState.expand) 0f else tiltState.rotationY.value,
        animationSpec = tween(durationMillis = durationMillis + delayMillis, delayMillis = delayMillis, easing = easing)
    )
    val scaleX by animateFloatAsState(
        targetValue = if (newPageState.expand) 1f else tiltState.scale.value,
        animationSpec = tween(durationMillis = durationMillis + delayMillis, delayMillis = delayMillis, easing = easing)
    )
    val scaleY by animateFloatAsState(
        targetValue = if (newPageState.expand) 1f else tiltState.scale.value,
        animationSpec = tween(durationMillis = durationMillis + delayMillis, delayMillis = delayMillis, easing = easing)
    )
    val pivotX by animateFloatAsState(
        targetValue = if (newPageState.expand) 0f else tiltState.pivotX.value,
        animationSpec = tween(durationMillis = durationMillis + delayMillis, delayMillis = delayMillis, easing = easing)
    )
    val pivotY by animateFloatAsState(
        targetValue = if (newPageState.expand) 0f else tiltState.pivotY.value,
        animationSpec = tween(durationMillis = durationMillis + delayMillis, delayMillis = delayMillis, easing = easing)
    )

    return UpdatePageAnimationState(
        alpha = alpha,
        paddings = PaddingValues(top = paddingTop, bottom = paddingBottom),
        values = animatedValues,
        rotationX = rotationX,
        rotationY = rotationY,
        scaleX = scaleX,
        scaleY = scaleY,
        pivotX = pivotX,
        pivotY = pivotY
    )
}
// 状态相关的数据类
data class NewPageState(
    val currentPage: Int = 0,
    val expand: Boolean = false,
    val complete: Boolean = false
)

data class AnimatedValues(
    val color: Color,
    val radius: Dp,
    val cardHeight: Dp,
    val top: Dp,
    val horizontal: Dp,
    val titleTop: Dp
)


// 包装动画状态
data class UpdatePageAnimationState(
    val alpha: Float,
    val paddings: PaddingValues,
    val values: AnimatedValues,
    // 倾斜动画相关值
    val rotationX: Float,
    val rotationY: Float,
    val scaleX: Float,
    val scaleY: Float,
    val pivotX: Float,
    val pivotY: Float,
)
