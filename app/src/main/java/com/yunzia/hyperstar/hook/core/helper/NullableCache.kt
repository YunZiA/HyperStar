package com.yunzia.hyperstar.hook.core.helper

import java.util.concurrent.ConcurrentHashMap

internal class NullableCache<K : Any, V : Any> {
    private val sentinel = Any()
    private val map = ConcurrentHashMap<K, Any>()

    @Suppress("UNCHECKED_CAST")
    inline fun getOrPut(key: K, loader: () -> V?): V? {
        map[key]?.let {
            return if (it === sentinel) null else it as V
        }
        val result = loader()
        map.putIfAbsent(key, result ?: sentinel)
        return result
    }

    fun size(): Int = map.size
}
