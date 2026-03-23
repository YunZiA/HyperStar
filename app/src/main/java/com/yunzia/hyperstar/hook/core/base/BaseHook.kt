package com.yunzia.hyperstar.hook.core.base

import com.yunzia.hyperstar.hook.core.annotation.Init

abstract class BaseHook {
    private val init: Init? = this::class.java.getAnnotation(Init::class.java)
    val className by lazy { this.javaClass.simpleName }
    val mPackageName by lazy { init?.packageName ?: "" }
    val supportedVersions by lazy { init?.versions ?: intArrayOf(-1) }
    val minVersion by lazy { init?.versions?.minOrNull() ?: -1 }
    val maxVersion by lazy { init?.versions?.maxOrNull() ?: -1 }
    var isInit: Boolean = false
    abstract fun init()
}