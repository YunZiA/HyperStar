package com.yunzia.hyperstar.ui.component.nav

import android.os.Bundle
import android.os.Parcelable
import android.util.Base64
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
@Parcelize
data class CommitHistory(
    @SerialName("apk_name")
    val apkName: String,
    @SerialName("commit_message")
    val commitMessage: String
) : Parcelable

// 3. 修改导航参数类型定义
val commitHistoryType = object : NavType<Array<CommitHistory>>(
    isNullableAllowed = false
) {
    @Suppress("DEPRECATION")
    override fun get(bundle: Bundle, key: String): Array<CommitHistory>? {
        return bundle.getParcelableArray(key, CommitHistory::class.java)
    }

    override fun parseValue(value: String): Array<CommitHistory> {
        return Gson().fromJson(value, Array<CommitHistory>::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: Array<CommitHistory>) {
        bundle.putParcelableArray(key, value)
    }
}

// 1. 添加编解码工具类
object NavigationHelper {
    fun encodeCommitHistory(commitList: List<CommitHistory>): String {
        val json = Json.encodeToString(commitList)
        return Base64.encodeToString(json.toByteArray(), Base64.URL_SAFE)
    }

    fun decodeCommitHistory(encoded: String): List<CommitHistory> {
        return try {
            val json = String(Base64.decode(encoded, Base64.URL_SAFE))
            Json.decodeFromString<List<CommitHistory>>(json)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}