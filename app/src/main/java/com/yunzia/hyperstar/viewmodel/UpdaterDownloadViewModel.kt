package com.yunzia.hyperstar.viewmodel

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunzia.hyperstar.utils.FileSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class UpdaterDownloadViewModel : ViewModel() {

    private val _noInit = MutableStateFlow(false)

    private val _isNeedUpdate = MutableStateFlow(false)
    val isNeedUpdate: StateFlow<Boolean> = _isNeedUpdate.asStateFlow()

    private val _showUpdater = MutableStateFlow(false)
    val showUpdater: StateFlow<Boolean> = _showUpdater.asStateFlow()

    private val _fileSize = MutableStateFlow("Loading...")
    val fileSize: StateFlow<String> = _fileSize.asStateFlow()

    private val _commitHistory = MutableStateFlow<List<CommitHistory>>(emptyList())
    val commitHistory: StateFlow<List<CommitHistory>> = _commitHistory.asStateFlow()

    private val _currentCommit = MutableStateFlow("")
    val currentCommit: StateFlow<String> = _currentCommit.asStateFlow()

    private val _lastCommit = MutableStateFlow("")
    val lastCommit: StateFlow<String> = _lastCommit.asStateFlow()

    private val _downloadStatus = MutableStateFlow(DownloadStatus.NONE)
    val downloadStatus: StateFlow<DownloadStatus> = _downloadStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var updateChecked = false
    private var fileSizeLoaded = false
    private var commitHistoryLoaded = false
    private var lastCommitLoaded = false

    fun init() {
        if (!_noInit.value) {
            _isNeedUpdate.value = false
            _showUpdater.value = false
            updateChecked = false
            fileSizeLoaded = false
            commitHistoryLoaded = false
            lastCommitLoaded = false
        }
    }

    fun showUpdater() {
        _showUpdater.value = true
    }

    fun noInit() {
        _noInit.value = true
    }

    fun clearInit() {
        _noInit.value = false
    }

    fun checkForUpdates(currentVersion: String, newVersion: String) {
        viewModelScope.launch {
            try {
                _isNeedUpdate.value = extractVersionNumber(currentVersion) < extractVersionNumber(newVersion)
            } catch (_: Exception) {
            } finally {
                updateChecked = true
                checkAllLoaded()
            }
        }
    }

    fun loadCommitHistory(targetVersion: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _commitHistory.value = fetchAndParseCommitHistory()
            _lastCommit.value = _commitHistory.value.firstOrNull()?.commitMessage ?: ""
            lastCommitLoaded = true
            loadCurrentCommit(targetVersion)
            commitHistoryLoaded = true
            checkAllLoaded()
        }
    }

    fun getFileTotalSize(fileUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _fileSize.value = formatSize(FileSize(fileUrl).getFileSize())
            fileSizeLoaded = true
            checkAllLoaded()
        }
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

    private fun loadCurrentCommit(targetVersion: String) {
        _currentCommit.value = try {
            _commitHistory.value
                .last { it.apkName == "HyperStar_v$targetVersion" }
                .commitMessage
        } catch (_: Exception) {
            "null"
        }
    }

    private fun checkAllLoaded() {
        _isLoading.value = !(updateChecked && fileSizeLoaded && commitHistoryLoaded && lastCommitLoaded)
    }

    private fun fetchAndParseCommitHistory(): List<CommitHistory> {
        return try {
            val jsonContent = fetchJsonFromUrl()
            val rawHistory: List<CommitHistory> = Json.decodeFromString(jsonContent)
            if (rawHistory.isEmpty()) return emptyList()
            rawHistory.map { commit ->
                commit.copy(
                    commitMessage = commit.commitMessage
                        .replace("--", "• ")
                        .trim()
                        .lines()
                        .joinToString("\n") { it }
                )
            }
        } catch (e: Exception) {
            Log.d("UpdaterDownload", "fetchAndParseCommitHistory: $e")
            emptyList()
        }
    }

    private fun fetchJsonFromUrl(): String {
        val connection = URL(COMMIT_HISTORY_URL).openConnection() as HttpURLConnection
        return connection.inputStream.bufferedReader().use { it.readText() }
    }

    private fun extractVersionNumber(input: String): Long =
        input.filter { it.isDigit() }.toLongOrNull() ?: 0L

    private fun formatSize(size: Long): String = when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "%.1f KB".format(size / 1024.0)
        size < 1024 * 1024 * 1024 -> "%.1f MB".format(size / (1024.0 * 1024.0))
        else -> "%.1f GB".format(size / (1024.0 * 1024.0 * 1024.0))
    }

    enum class DownloadStatus {
        NONE, DOWNLOAD, SUCCESS, FAIL
    }

    companion object {
        private const val COMMIT_HISTORY_URL =
            "https://gitee.com/dongdong-gc/hyper-star-updater/raw/main/dev/commit_history.json"
    }
}

@Serializable
@Parcelize
data class CommitHistory(
    @SerialName("apk_name")
    val apkName: String,
    @SerialName("commit_message")
    val commitMessage: String
) : Parcelable