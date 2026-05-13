package com.yunzia.hyperstar.utils

import androidx.compose.runtime.staticCompositionLocalOf
import io.github.libxposed.service.XposedService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.CopyOnWriteArraySet

val LocalScopeManager = staticCompositionLocalOf<ScopeManager> {
    error("LocalScopeManager not provided")
}

class ScopeManager {
    private lateinit var service: XposedService
    private val scopeSet = CopyOnWriteArraySet<String>()
    private val _scopeFlow = MutableStateFlow<Set<String>>(emptySet())
    val scopeFlow: StateFlow<Set<String>> = _scopeFlow.asStateFlow()

    fun attachService(service: XposedService) {
        this.service = service
        scopeSet.clear()
        scopeSet.addAll(service.scope)
        emitScope()
    }

    operator fun contains(packageName: String): Boolean = packageName in scopeSet

    fun getScope(): Set<String> = scopeSet.toSet()

    fun addScope(
        packageName: String,
        callback: Result<List<String>>.() -> Unit = {}
    ) = addScope(listOf(packageName), callback)

    fun addScope(
        packages: List<String>,
        callback: Result<List<String>>.() -> Unit = {}
    ) {
        service.requestScope(
            packages,
            object : XposedService.OnScopeEventListener {
                override fun onScopeRequestApproved(approved: List<String?>) {
                    val result = approved.filterNotNull()
                    if (result.isEmpty()) {
                        Result.success(emptyList<String>()).callback()
                        return
                    }
                    if (scopeSet.addAll(result)) emitScope()
                    Result.success(result).callback()
                }

                override fun onScopeRequestFailed(message: String) {
                    Result.failure<List<String>>(IllegalStateException(message)).callback()
                }
            }
        )
    }

    fun removeScope(packageName: String) = removeScope(listOf(packageName))

    fun removeScope(packages: List<String>) {
        service.removeScope(packages)
        if (scopeSet.removeAll(packages.toSet())) emitScope()
    }

    private fun emitScope() {
        _scopeFlow.value = scopeSet.toSet()
    }
}