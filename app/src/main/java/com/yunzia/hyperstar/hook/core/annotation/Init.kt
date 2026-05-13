package com.yunzia.hyperstar.hook.core.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Init(
    val packageName: String,
    val versions: IntArray = []
)