package com.yunzia.hyperstar.utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.libxposed.service.XposedService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex


val LocalScopeManager = staticCompositionLocalOf<ScopeManager> {
    error("LocalNavigator not provided")
}

@Composable
fun rememberScopeManager(): ScopeManager {
    val scopeManager = rememberSaveable(saver = ScopeManager.Saver) {
        ScopeManager()
    }
    DisposableEffect(Unit) {
        onDispose {
            scopeManager.onServiceReleased()
        }
    }
    return scopeManager
}
class ScopeManager {
    val scopeList: SnapshotStateList<String> = mutableStateListOf()

    val currentScope: List<String> get() = scopeList.toList()

    // 正在请求中的包名
    private val pendingRequests = mutableStateMapOf<String, ScopeRequestResult>()

    // 待移除的包名
    private val pendingRemovals = mutableStateMapOf<String, String?>()

    private var serviceBoundScope: CoroutineScope? = null
    private var serviceJob: Job? = null
    private val requestMutex = Mutex()

    fun init(scopeList: List<String>) {
        this.scopeList.apply {
            Log.d("ScopeManager", "init: $this")
            clear()
            addAll(scopeList)
            Log.d("ScopeManager", "init: $this")
        }
    }

    /**
     * 当 Xposed 服务绑定成功时调用此方法
     * @param service 已绑定的 XposedService 实例
     */
    fun onServiceBound(service: XposedService) {
        serviceJob?.cancel()
        val parentJob = Job()
        serviceJob = parentJob
        serviceBoundScope = CoroutineScope(parentJob + Dispatchers.Main.immediate)
        serviceBoundScope?.launch {
            snapshotFlow { pendingRequests.entries.filter { it.value == ScopeRequestResult.Request } }
                .distinctUntilChanged()
                .filter { it.isNotEmpty() }
                .map { list -> list.filterNot { it.key in scopeList } }
                .collectLatest { packagesToRequest ->
                    for (pkg in packagesToRequest) {
                        val requestCompleted = CompletableDeferred<Unit>()
                        service.requestScope(pkg.key) {
                            onApproved { pkgName ->
                                scopeList.add(pkgName)
                                pendingRequests[pkgName] = ScopeRequestResult.Approved
                                Log.d("ScopeManager", "Approved for: $pkgName")
                                if (!requestCompleted.isCompleted) {
                                    requestCompleted.complete(Unit)
                                }
                            }
                            onDenied { pkgName ->
                                pendingRequests[pkgName] = ScopeRequestResult.Denied
                                Log.w("ScopeManager", "Denied for: $pkgName")
                                if (!requestCompleted.isCompleted) {
                                    requestCompleted.complete(Unit)
                                }
                            }
                            onTimeout { pkgName ->
                                pendingRequests[pkgName] = ScopeRequestResult.Timeout
                                Log.w("ScopeManager", "Timeout for: $pkgName")
                                if (!requestCompleted.isCompleted) {
                                    requestCompleted.complete(Unit)
                                }
                            }
                            onFailed { pkgName, message ->
                                pendingRequests[pkgName] = ScopeRequestResult.Failed
                                if (!requestCompleted.isCompleted) {
                                    requestCompleted.complete(Unit)
                                }
                            }
                            onPrompted { pkgName ->
                                Log.d("ScopeManager", "Prompted for: $pkgName")
                            }
                        }
                        requestCompleted.await()
                    }
                }
        }

        serviceBoundScope?.launch {
            snapshotFlow { pendingRemovals.entries.filter { it.value == it.key } }
                .distinctUntilChanged()
                .filter { it.isNotEmpty() }
                .map { list -> list.filter { it.key in scopeList } }
                .collectLatest { packagesToRemove ->
                    for (pkg in packagesToRemove) {
                        try {
                            val error = service.removeScope(pkg.key)
                            if (error == null) {
                                // 移除成功，从 scopeList 中移除
                                scopeList.remove(pkg.key)
                                Log.d("ScopeManager", "Successfully removed scope for:  $pkg")
                                pendingRemovals[pkg.key] = null
                            } else {
                                Log.e("ScopeManager", "Error removing scope for  $ pkg:  $error")
                                pendingRemovals[pkg.key] = error
                            }
                        } catch (e: Exception) {
                            Log.e("ScopeManager", "Exception while removing scope for  $pkg", e)
                            pendingRemovals[pkg.key] = e.message
                        }
                        pendingRemovals[pkg.key] = service.removeScope(pkg.key)
                        Log.d("ScopeManager", "Removed scope for: $pkg, result = ${pendingRemovals[pkg.key]}")
                    }
                }
        }
    }

    suspend fun addScope(packageName: String,scopeRequestResult:(ScopeRequestResult)->Unit) = coroutineScope {
        if (packageName in scopeList || packageName in pendingRequests || packageName in pendingRemovals) {
            return@coroutineScope
        }
        Log.d("ScopeManager", "Request for  $packageName has been added to the queue.")
        pendingRequests[packageName] = ScopeRequestResult.Request
        // 使用 async 启动一个并发任务来等待结果
        val resultDeferred = async {
            snapshotFlow { pendingRequests[packageName] }
                .filterNotNull() // 过滤掉 null 值
                .distinctUntilChanged()
                .filter { result -> result != ScopeRequestResult.Prompted && result != ScopeRequestResult.Request }
                .map { result -> result }
                .first()
        }
        resultDeferred.await().let {
            Log.d("ScopeManager", "Request for  $packageName $it.")
            scopeRequestResult(it)
            pendingRequests.remove(packageName)
        }
    }

    suspend fun removeScope(packageName: String, scopeRemoveResult:(String?) -> Unit) = coroutineScope {
        if (packageName !in scopeList || packageName in pendingRemovals) return@coroutineScope
        pendingRemovals[packageName] = packageName
        val resultDeferred = async {
            snapshotFlow { pendingRemovals.getValue(packageName) }
                .distinctUntilChanged()
                .filter { result -> result != packageName }
                .map { result -> result }
                .first()
        }
        resultDeferred.await().let {
            Log.d("ScopeManager", "Request for  $packageName $it.")
            scopeRemoveResult(it)
            pendingRemovals.remove(packageName)
            if (it == null){
                scopeList.remove(packageName)
            }
        }
    }

    fun onServiceReleased() {
        serviceJob?.cancel()
        serviceBoundScope = null
        pendingRequests.clear()
        pendingRemovals.clear()
    }

    enum class ScopeRequestResult{
        Request,Prompted,Approved,Denied,Timeout,Failed
    }

    companion object {
        // 定义 RememberSaver
        private fun <K> saveEnumMap(map: Map<K, ScopeRequestResult>): Map<K, String> {
            return map.mapValues { entry -> entry.value.name }
        }
        private fun <K> restoreEnumMap(savedMap: Map<K, String>?): Map<K, ScopeRequestResult> {
            return savedMap?.mapValues { entry ->
                runCatching { enumValueOf<ScopeRequestResult>(entry.value) }
                    .getOrElse { ScopeRequestResult.Failed }
            } ?: emptyMap()
        }
        val Saver: Saver<ScopeManager, Any> = mapSaver(
            save = { manager ->
                mapOf(
                    "scope" to ArrayList(manager.scopeList.toList()),
                    "pendingRequests" to saveEnumMap(manager.pendingRequests),
                    "pendingRemovals" to manager.pendingRemovals.toMap().mapValues { entry ->
                        entry.value
                    }
                )
            },
            restore = { map ->
                val scopeManager = ScopeManager()
                @Suppress("UNCHECKED_CAST")
                (map["scope"] as? ArrayList<String>)?.let { scopeManager.scopeList.addAll(it) }
                @Suppress("UNCHECKED_CAST")
                (map["pendingRequests"] as? Map<String,String>)?.let { savedPendingRequestsStrings ->
                    val restoredPendingRequests = restoreEnumMap(savedPendingRequestsStrings)
                    scopeManager.pendingRequests.putAll(restoredPendingRequests)
                }
                @Suppress("UNCHECKED_CAST")
                (map["pendingRemovals"] as? Map<String, String?>)?.let { scopeManager.pendingRemovals.putAll(it) }
                scopeManager
            }
        )
    }

}