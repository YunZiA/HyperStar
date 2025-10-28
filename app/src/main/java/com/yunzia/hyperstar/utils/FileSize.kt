package com.yunzia.hyperstar.utils

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class FileSize(
    private val fileUrl: String,
) {
    private val tag = "FileSize"
    private val TAG = "FileSize"

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    fun getFileSize(): Long {
        return try {
            // 首先尝试 HEAD 请求
            getFileSizeByHead() ?: run {
                // 如果 HEAD 请求失败，尝试完整下载
                getFileSizeByDownload()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting file size: ${e.message}", e)
            -1
        }
    }

    private fun getFileSizeByHead(): Long? {
        try {
            val request = Request.Builder()
                .url(fileUrl)
                .head()
                .header("Accept-Encoding", "") // 防止压缩
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w(TAG, "HEAD request failed: ${response.code}")
                    return null
                }

                // 尝试从头信息获取文件大小
                val contentLength = response.header("Content-Length")?.toLongOrNull()
                if (contentLength == null) {
                    Log.w(TAG, "No Content-Length header found")
                    return null
                }

                return contentLength
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in HEAD request: ${e.message}", e)
            return null
        }
    }

    private fun getFileSizeByDownload(): Long {
        try {
            val request = Request.Builder()
                .url(fileUrl)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w(TAG, "GET request failed: ${response.code}")
                    return -1
                }

                return response.body?.bytes()?.size?.toLong() ?: -1
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading file: ${e.message}", e)
            return -1
        }
    }
}