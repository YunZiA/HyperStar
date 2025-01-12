package com.yunzia.hyperstar.ui.miuiStrongToast

import kotlinx.serialization.Serializable

@Serializable
data class TextParams(
    var text: String? = null,
    var textColor: Int = 0
)