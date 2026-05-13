package com.yunzia.hyperstar.viewmodel

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunzia.hyperstar.ui.component.card.TiltAnimationState
import com.yunzia.hyperstar.ui.component.helper.getSystemCornerRadius
import com.yunzia.hyperstar.ui.component.helper.getWindowSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

class UpdaterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateDetailUiState())
    val uiState: StateFlow<UpdateDetailUiState> = _uiState.asStateFlow()

    data class UpdateDetailUiState(
        val newPageState: NewPageState = NewPageState(),
        val animationState: UpdatePageAnimationState? = null,
        val isScrollEnabled: Boolean = false,
        val isBlur: Boolean = false,
        val needCancel: MutableState<Boolean> = mutableStateOf(true)
    )

    sealed class UpdateDetailEvent {
        data class PageChanged(val page: Int) : UpdateDetailEvent()
        data class ScrollOffsetChanged(val offset: Float) : UpdateDetailEvent()
        data class AnimationCompleted(val radius: Dp, val finalRadius: Dp) : UpdateDetailEvent()
        data class SetScrollEnabled(val isScrollEnabled: Boolean) : UpdateDetailEvent()
        data object NavigateToDetailPage : UpdateDetailEvent()
        data object NavigateBack : UpdateDetailEvent()
        data object CancelTiltEffect : UpdateDetailEvent()
    }

    fun handleEvent(event: UpdateDetailEvent) {
        viewModelScope.launch {
            when (event) {
                is UpdateDetailEvent.PageChanged -> {
                    _uiState.update {
                        it.copy(
                            newPageState = it.newPageState.copy(
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
                        _uiState.update { it.copy(isBlur = event.offset < -20f) }
                    }
                }

                is UpdateDetailEvent.AnimationCompleted -> {
                    val pageState = _uiState.value.newPageState
                    if (!pageState.expand) {
                        _uiState.update { it.copy(newPageState = it.newPageState.copy(complete = false)) }
                        return@launch
                    }
                    val complete = event.radius == event.finalRadius
                    if (complete) delay(300L)
                    _uiState.update { it.copy(newPageState = it.newPageState.copy(complete = complete)) }
                }

                is UpdateDetailEvent.NavigateToDetailPage -> {
                    _uiState.update {
                        it.copy(
                            newPageState = it.newPageState.copy(currentPage = 1, expand = true),
                            isBlur = false
                        )
                    }
                }

                is UpdateDetailEvent.NavigateBack -> {
                    _uiState.update {
                        it.copy(
                            newPageState = it.newPageState.copy(currentPage = 0, expand = false),
                            isBlur = false
                        )
                    }
                }

                is UpdateDetailEvent.SetScrollEnabled -> {
                    _uiState.update { it.copy(isScrollEnabled = event.isScrollEnabled) }
                }

                is UpdateDetailEvent.CancelTiltEffect -> {
                    _uiState.value.needCancel.value = false
                    delay(150)
                    _uiState.value.needCancel.value = true
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
    val easing = LinearOutSlowInEasing
    val coroutineScope = rememberCoroutineScope()
    val headerTop = padding.calculateTopPadding() + 65.dp

    LaunchedEffect(newPageState.expand) {
        if (newPageState.expand) {
            tiltState.noAnim = true
            tiltState.recovery(coroutineScope, tween(durationMillis = durationMillis, easing = easing))
        } else {
            tiltState.noAnim = false
        }
    }

    val expand = newPageState.expand
    val spec = tween<Dp>(durationMillis = durationMillis, easing = easing)
    val floatSpec = tween<Float>(durationMillis = durationMillis, easing = easing)
    val colorSpec = tween<Color>(durationMillis = durationMillis, easing = easing)

    val alpha by animateFloatAsState(
        targetValue = if (expand) 1f else 0f,
        animationSpec = floatSpec
    )
    val paddingTop by animateDpAsState(
        targetValue = if (expand) 0.dp else padding.calculateTopPadding() + 2.dp,
        animationSpec = spec
    )
    val paddingBottom by animateDpAsState(
        targetValue = if (expand) 0.dp else padding.calculateBottomPadding() + 28.dp,
        animationSpec = spec
    )

    val myRadius = getSystemCornerRadius()
    val windowHeight = getWindowSize().height

    val animatedValues = AnimatedValues(
        color = animateColorAsState(
            targetValue = if (expand) colorScheme.surface else colorScheme.surfaceContainer,
            animationSpec = colorSpec
        ).value,
        radius = animateDpAsState(
            targetValue = if (expand) myRadius else 20.dp,
            animationSpec = spec
        ).value,
        cardHeight = animateDpAsState(
            targetValue = if (expand) windowHeight else headerTop + 500.dp,
            animationSpec = spec
        ).value,
        top = animateDpAsState(
            targetValue = if (expand) 0.dp else headerTop,
            animationSpec = spec
        ).value,
        horizontal = animateDpAsState(
            targetValue = if (expand) 0.dp else 28.dp,
            animationSpec = spec
        ).value,
        titleTop = animateDpAsState(
            targetValue = if (expand) padding.calculateTopPadding() + 28.dp else 45.dp,
            animationSpec = spec
        ).value
    )

    return UpdatePageAnimationState(
        alpha = alpha,
        paddings = PaddingValues(top = paddingTop, bottom = paddingBottom),
        values = animatedValues
    )
}

@Stable
data class NewPageState(
    val currentPage: Int = 0,
    val expand: Boolean = false,
    val complete: Boolean = false
)

@Stable
data class AnimatedValues(
    val color: Color,
    val radius: Dp,
    val cardHeight: Dp,
    val top: Dp,
    val horizontal: Dp,
    val titleTop: Dp
)

@Stable
data class UpdatePageAnimationState(
    val alpha: Float,
    val paddings: PaddingValues,
    val values: AnimatedValues
)
