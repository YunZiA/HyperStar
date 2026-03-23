package com.yunzia.hyperstar.utils

import android.os.Parcelable
import android.util.Log
import io.github.libxposed.service.XposedService
import kotlinx.parcelize.Parcelize
import java.io.FileWriter

@Parcelize
data class XposedServiceInfo(
    val apiVersion: Int,
    val frameworkName: String,
    val frameworkVersion: String,
    val frameworkVersionCode: Long,
    val scope: List<String> // 通常 scope 是一个集合
) : Parcelable {
    companion object {
        fun unconnected() = XposedServiceInfo(
            apiVersion = -1, // 使用特殊值表示未连接
            frameworkName = "Not Connected",
            frameworkVersion = "N/A",
            frameworkVersionCode = -1L,
            scope = emptyList()
        )
    }

    override fun toString(): String {
        return """
            API $apiVersion
            Framework $frameworkName
            Framework version $frameworkVersion
            Framework version code $frameworkVersionCode
            Scope: $scope
        """.trimIndent()
    }
}

fun XposedService.addFile(name: String){
    openRemoteFile(name).use { pfd ->
        FileWriter(pfd.fileDescriptor).use {
            it.append("Hello World!")
        }
    }
}