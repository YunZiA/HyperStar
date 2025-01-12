package com.yunzia.hyperstar.ui.miuiStrongToast

import kotlinx.serialization.Serializable

@Serializable
data class StrongToastBean(
    var left: Left? = null,
    var right: Right? = null
)