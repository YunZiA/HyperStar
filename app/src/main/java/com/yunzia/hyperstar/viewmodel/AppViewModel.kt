package com.yunzia.hyperstar.viewmodel

import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunzia.hyperstar.utils.AppInfo
import com.yunzia.hyperstar.utils.ScopeManager
import com.yunzia.hyperstar.utils.XposedServiceInfo
import io.github.libxposed.service.XposedService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel: ViewModel()  {
    var isActive by mutableStateOf(false)
        private set
    private val _appInScope = MutableStateFlow<Map<String, AppInfo?>>(emptyMap())
    val appInScope: StateFlow<Map<String, AppInfo?>> = _appInScope.asStateFlow()

    private val _appNotInScope = MutableStateFlow<Map<String, String?>>(emptyMap())
    val appNotInScope: StateFlow<Map<String, String?>> = _appNotInScope.asStateFlow()
    private val _xposedServiceInfo = MutableStateFlow(XposedServiceInfo.unconnected())
    val xposedServiceInfo: StateFlow<XposedServiceInfo> = _xposedServiceInfo.asStateFlow()

    val scopeManager = ScopeManager()

    // 当 Xposed 服务绑定成功时调用
    fun onXposedServiceBound(service: XposedService) {
        isActive = service.apiVersion >= 101
        _xposedServiceInfo.value = XposedServiceInfo(
            apiVersion = service.apiVersion,
            frameworkName = service.frameworkName,
            frameworkVersion = service.frameworkVersion,
            frameworkVersionCode = service.frameworkVersionCode,
            scope = service.scope
        )
        Log.d("AppViewModel", "onXposedServiceBound: \n${_xposedServiceInfo.value}")
        scopeManager.attachService(service)
    }

    // 当 Xposed 服务断开连接或需要清理时调用
    fun onXposedServiceReleased() {
        Log.d("AppViewModel", "onXposedServiceReleased: ")
//        scopeManager.onServiceReleased()
    }

    fun loadAppInfo(
        packageManager: PackageManager,
        moduleScope: Array<String>,
        scopeManagerCurrentScope: Set<String>
    ) {
        if (scopeManagerCurrentScope.isEmpty()) {
            clearAppInfo()
            return
        }

        viewModelScope.launch {
            val loadedAppInfo = mutableMapOf<String, AppInfo?>()
            val loadedAppNo = mutableMapOf<String, String?>()

            try {
                coroutineScope {
                    val tasksToRun = moduleScope.filter { packageName ->
                        scopeManagerCurrentScope.contains(packageName)
                    }.map { packageName ->
                        packageName to async {
                            try {
                                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
                                AppInfo(
                                    appIcon = packageInfo.applicationInfo?.loadIcon(packageManager),
                                    appName = packageManager.getApplicationLabel(packageInfo.applicationInfo!!)
                                        .toString(),
                                    versionName = packageInfo.versionName,
                                    versionCode = packageInfo.longVersionCode
                                )
                            } catch (e: PackageManager.NameNotFoundException) {
                                Log.w("ggc", "Package not found: $packageName", e)
                                loadedAppNo[packageName] = e.message
                                null
                            }
                        }
                    }

                    tasksToRun.forEach { (packageName, deferred) ->
                        val appInfoItem = deferred.await()
                        if (appInfoItem != null) {
                            loadedAppInfo[packageName] = appInfoItem
                        }
                    }
                }
            } finally {
                // 更新 Flow 的值
                _appInScope.value = loadedAppInfo
                _appNotInScope.value = loadedAppNo
            }
        }
    }

    private fun clearAppInfo() {
        viewModelScope.launch {
            _appInScope.value = emptyMap()
            _appNotInScope.value = emptyMap()
        }
    }

}