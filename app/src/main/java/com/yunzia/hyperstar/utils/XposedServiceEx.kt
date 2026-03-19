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



class ScopeRequestHandler(private val packageName: String) {
    //请求
    private var onPrompted: ((String) -> Unit)? = null
    //允许
    private var onApproved: ((String) -> Unit)? = null
    //不在访问/拒绝
    private var onDenied: ((String) -> Unit)? = null
    //超时
    private var onTimeout: ((String) -> Unit)? = null
    //失败
    private var onFailed: ((String, String) -> Unit)? = null

    fun onApproved(block: (String) -> Unit) {
        onApproved = block
    }

    fun onDenied(block: (String) -> Unit) {
        onDenied = block
    }

    fun onTimeout(block: (String) -> Unit) {
        onTimeout = block
    }

    fun onFailed(block: (String, String) -> Unit) {
        onFailed = block
    }

    fun onPrompted(block: (String) -> Unit) {
        onPrompted = block
    }

    // 内部的监听器实现
    internal fun buildListener(): XposedService.OnScopeEventListener = object : XposedService.OnScopeEventListener {
        override fun onScopeRequestApproved(pn: String) {
            if (pn == packageName) {
                onApproved?.invoke(pn)
            }
        }

        override fun onScopeRequestDenied(pn: String) {
            if (pn == packageName) {
                onDenied?.invoke(pn)
            }
        }

        override fun onScopeRequestTimeout(pn: String) {
            if (pn == packageName) {
                onTimeout?.invoke(pn)
            }
        }

        override fun onScopeRequestFailed(pn: String, message: String) {
            if (pn == packageName) {
                onFailed?.invoke(pn, message)
            }
        }

        override fun onScopeRequestPrompted(pn: String) {

            Log.d("ScopeManager", "Prompted for: $pn")
            if (pn == packageName) {
                onPrompted?.invoke(pn)
            }
        }
    }
}

// 扩展函数，提供 DSL 入口点
fun XposedService.requestScope(
    packageName: String,
    configure: ScopeRequestHandler.() -> Unit
) {
    val handler = ScopeRequestHandler(packageName).apply(configure)
    val listener = handler.buildListener()
    requestScope(packageName, listener)
}