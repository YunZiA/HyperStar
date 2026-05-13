package com.yunzia.hyperstar.viewmodel

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchDebouncer<T>(
    private val scope: CoroutineScope,
    private val delayMs: Long = 300,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private var job: Job? = null
    private var latestQuery = ""

    fun submit(
        query: String,
        onEmpty: () -> Unit = {},
        onLoading: () -> Unit = {},
        onResult: (List<T>) -> Unit,
        search: suspend (String) -> List<T>
    ) {
        val normalizedQuery = query.trim()
        latestQuery = normalizedQuery
        if (normalizedQuery.isEmpty()) {
            job?.cancel()
            onEmpty()
            return
        }
        job?.cancel()
        job = scope.launch(dispatcher) {
            delay(delayMs)
            withContext(Dispatchers.Main) { onLoading() }
            val results = search(normalizedQuery)
            withContext(Dispatchers.Main) {
                if (normalizedQuery == latestQuery) {
                    onResult(results)
                }
            }
        }
    }
}
