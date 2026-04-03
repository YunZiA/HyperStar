package com.yunzia.hyperstar.viewmodel

import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunzia.hyperstar.ui.screen.pagers.main.home.AppEntryList
import com.yunzia.hyperstar.utils.AppInfo
import com.yunzia.hyperstar.utils.ScopeManager
import com.yunzia.hyperstar.utils.XposedServiceInfo
import io.github.libxposed.service.XposedService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    var isActive = mutableStateOf(false)
        private set

    val scopeManager = ScopeManager()

    /**
     * 入口UI状态表（核心状态）
     * key = packageName
     */
    /**
     * 可见入口
     */
    val visibleEntryMap =
        mutableStateMapOf<String, AppInfo>()

    /**
     * 不可见入口
     */
    val invisibleEntryMap =
        mutableStateMapOf<String, EntryUiState>()

    /**
     * Xposed服务信息
     */
    var xposedServiceInfo =
        mutableStateOf(XposedServiceInfo.unconnected())
        private set


    /**
     * Xposed连接成功
     */
    fun onXposedServiceBound(service: XposedService) {

        isActive.value =
            service.apiVersion >= 101

        xposedServiceInfo.value =
            XposedServiceInfo(
                apiVersion = service.apiVersion,
                frameworkName = service.frameworkName,
                frameworkVersion = service.frameworkVersion,
                frameworkVersionCode = service.frameworkVersionCode,
                scope = service.scope
            )

        scopeManager.attachService(service)

        Log.d("AppViewModel", "Xposed connected")
    }


    fun onXposedServiceReleased() {

        Log.d("AppViewModel", "Xposed released")
    }


    /**
     * 加载入口状态（核心函数）
     */
    fun loadEntries(
        packageManager: PackageManager,
        moduleScope: Array<String>,
        currentScope: Set<String>
    ) {

        viewModelScope.launch {
            coroutineScope {
                AppEntryList.entries.forEach { entry ->
                    launch  {

                        try {
                            val packageInfo =
                                packageManager.getPackageInfo(
                                    entry.packageName,
                                    PackageManager.GET_META_DATA
                                )

                            val appInfo =
                                AppInfo(
                                    appIcon =
                                        packageInfo.applicationInfo
                                            ?.loadIcon(packageManager),

                                    appName =
                                        packageManager.getApplicationLabel(
                                            packageInfo.applicationInfo!!
                                        ).toString(),

                                    versionName =
                                        packageInfo.versionName,

                                    versionCode =
                                        packageInfo.longVersionCode
                                )

                            if (!currentScope.contains(entry.packageName)) {

                                invisibleEntryMap[entry.packageName] = EntryUiState.NotInScope(appInfo)
                                visibleEntryMap.remove(entry.packageName)

                                return@launch
                            }
                            val visible = entry.isVisible(appInfo)

                            if (visible){
                                visibleEntryMap[entry.packageName] = appInfo
                                invisibleEntryMap.remove(entry.packageName)
                            }
                            else{
                                invisibleEntryMap[entry.packageName] = EntryUiState.Hidden(appInfo)
                                visibleEntryMap.remove(entry.packageName)

                            }

                        } catch (e: Exception) {

                            invisibleEntryMap[entry.packageName] =
                                EntryUiState.NotInstalled(
                                    e.message
                                )
                        }
                    }

                }
            }
        }
    }


    /**
     * 清空入口状态
     */
    fun clearEntries() {

        visibleEntryMap.clear()
        invisibleEntryMap.clear()
    }
}


/**
 * Entry UI状态模型（核心设计）
 */
sealed class EntryUiState(
    open val appInfo: AppInfo?
) {

    data class Hidden(
        override val appInfo: AppInfo
    ) : EntryUiState(appInfo)

    data class NotInScope(
        override val appInfo: AppInfo
    ) : EntryUiState(appInfo)
    data class NotInstalled(
        val reason: String?
    ) : EntryUiState(null)

}