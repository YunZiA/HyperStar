package com.yunzia.hyperstar.ui.miuiStrongToast

import kotlinx.serialization.Serializable

@Serializable
data class Left(
    var iconParams: IconParams? = null,
    var textParams: TextParams? = null
)