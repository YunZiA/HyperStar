package com.yunzia.hyperstar.ui.miuiStrongToast

import kotlinx.serialization.Serializable

@Serializable
data class Right(
    var iconParams: IconParams? = null,
    var textParams: TextParams? = null
)