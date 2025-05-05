package com.yunzia.hyperstar.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.nav.CommitHistory
import com.yunzia.hyperstar.utils.FileSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class UpdaterDownloadViewModel: ViewModel() {


    private val _noInit = MutableStateFlow(false)

    private val _isNeedUpdate = MutableStateFlow(false)
    val isNeedUpdate: StateFlow<Boolean> = _isNeedUpdate

    private val _showUpdater = MutableStateFlow(false)
    val showUpdater: StateFlow<Boolean> = _showUpdater

    // 文件信息状态
    private val _fileSize = MutableStateFlow<String>("Loading...")
    val fileSize: StateFlow<String> = _fileSize.asStateFlow()

    private val _commitHistory = MutableStateFlow<List<CommitHistory>>(emptyList())
    val commitHistory: StateFlow<List<CommitHistory>> = _commitHistory

    private val _currentCommit = MutableStateFlow("")
    val currentCommit: StateFlow<String> = _currentCommit

    private val _lastCommit = MutableStateFlow("")
    val lastCommit: StateFlow<String> = _lastCommit

    private val _downloadStatus = MutableStateFlow(DownloadStatus.NONE)
    val downloadStatus: StateFlow<DownloadStatus> = _downloadStatus


    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 跟踪各个加载状态
    private var updateChecked = false
    private var fileSizeLoaded = false
    private var commitHistoryLoaded = false
    private var lastCommitLoaded = false


    fun init() {
        if (!_noInit.value){
            _isNeedUpdate.value = false
            _showUpdater.value = false
            updateChecked = false
            fileSizeLoaded = false
            commitHistoryLoaded = false
            lastCommitLoaded = false
        }
    }

    private fun checkAllLoaded() {
        val allLoaded = updateChecked && fileSizeLoaded && commitHistoryLoaded && lastCommitLoaded
        _isLoading.value = !allLoaded
    }

    fun showUpdater(){
        _showUpdater.value = true
    }


    fun noInit(){
        _noInit.value = true
    }


    fun clearInit(){
        _noInit.value = false
    }

    fun checkForUpdates(currentVersion: String, newVersion: String) {
        viewModelScope.launch {
            try {
                val currentVersions = extractOnlyNumbers(currentVersion)
                val newVersions = extractOnlyNumbers(newVersion)
                _isNeedUpdate.value = currentVersions < newVersions

            } catch (e: Exception) {
                //_state.update { it.copy(error = e.message) }
            } finally {
                updateChecked = true
                checkAllLoaded()
            }
        }
    }

    fun loadCommitHistory(targetVersion: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _commitHistory.value = fetchAndParseCommitHistory()
            _lastCommit.value = _commitHistory.value[0].commitMessage
            lastCommitLoaded = true
            loadCurrentCommit(targetVersion)
            commitHistoryLoaded = true
            checkAllLoaded()
        }
    }

    fun loadCurrentCommit(targetVersion: String) {
        try {
            val filteredHistory = _commitHistory.value.filter { it.apkName == "HyperStar_v$targetVersion" }
            _currentCommit.value = filteredHistory.last().commitMessage

        } catch (e: Exception) {
            _currentCommit.value = "null"
        }
        Log.d("ggc", "loadCurrentCommit: ${currentCommit.value}")

    }


    sealed class DownloadInfo {
        object Idle : DownloadInfo()
        data class Progress(val downloadedSize: Long, val totalSize: Long, val formattedTotal: String) : DownloadInfo()
        data class Error(val message: String) : DownloadInfo()
        data class Complete(val totalSize: Long, val formattedSize: String) : DownloadInfo()
    }
    fun getFileTotalSize(fileUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _fileSize.value = formatSize(FileSize(fileUrl).getFileSize())
            fileSizeLoaded = true
            checkAllLoaded()
        }
    }

    private fun formatSize(size: Long): String = when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "%.1f KB".format(size / 1024.0)
        size < 1024 * 1024 * 1024 -> "%.1f MB".format(size / (1024.0 * 1024.0))
        else -> "%.1f GB".format(size / (1024.0 * 1024.0 * 1024.0))
    }

    fun downloadUpdate(fileUrl: String, outputFile: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _downloadStatus.value = DownloadStatus.DOWNLOAD
                val connection = URL(fileUrl).openConnection() as HttpURLConnection
                connection.inputStream.use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                }
                _downloadStatus.value = DownloadStatus.SUCCESS
            } catch (e: Exception) {
                e.printStackTrace()
                _downloadStatus.value = DownloadStatus.FAIL
            }
        }
    }

    private fun fetchAndParseCommitHistory(): List<CommitHistory> {
        return try {
            val jsonContent = fetchJsonFromUrl()
            val rawCommitHistory: List<CommitHistory> = Json.decodeFromString(jsonContent)
            if (rawCommitHistory.isEmpty()) {
                // 返回一个默认的提交历史，或者空列表
                return emptyList()
            }
            // 处理每个提交信息的格式
            rawCommitHistory.map { commit ->
                commit.copy(
                    commitMessage = commit.commitMessage
                        .replace("--", "• ")  // 移除 --
                        .trim()  // 移除首尾空白
                        .lines()  // 分割成行
                        //.filter { it.isNotBlank() }  // 移除空行
                        .joinToString("\n") { line ->  // 添加项目符号并重新组合
                            line  // 确保每行开头有项目符号，并移除行首尾空白
                        }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("ggc", "fetchAndParseCommitHistory: $e")
            emptyList()
        }
    }


    private fun fetchHeadCommitContent(context: Context): String {
        return try {
            val connection = URL("https://gitee.com/dongdong-gc/hyper-star-updater/raw/main/dev/head_commit.txt").openConnection() as HttpURLConnection
            connection.inputStream
                .bufferedReader()
                .use { it.readText() }
                .replace("--", "• ")
                .trim()
                .lines()
                .joinToString("\n") { line -> line }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("ggc", "fetchHeadCommitContent: $e")
            context.getString(R.string.UpdateFetchError)
        }
    }

    private fun fetchJsonFromUrl(): String {
        val connection = URL("https://gitee.com/dongdong-gc/hyper-star-updater/raw/main/dev/commit_history.json").openConnection() as HttpURLConnection
        return connection.inputStream.bufferedReader().use { it.readText() }
    }

    fun extractOnlyNumbers(input: String): Long {
        val numberString = input.filter { it.isDigit() }
        return numberString.toLongOrNull() ?: 0L // 安全转换为 Long 类型
    }


    data class FileInfo(
        val size: Long,
        val fileName: String?,
        val mimeType: String?
    )



    enum class DownloadStatus {
        NONE, DOWNLOAD, SUCCESS, FAIL
    }
}



