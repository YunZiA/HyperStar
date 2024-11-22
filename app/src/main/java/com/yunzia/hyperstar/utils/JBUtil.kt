package com.yunzia.hyperstar.utils

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yunzia.hyperstar.R
import org.json.JSONException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object JBUtil {

    private const val TAG = "ggc"

    fun openFile(launcher: ManagedActivityResultLauncher<String, Uri?>) {
        launcher.launch("application/json")
    }

    fun saveFile(launcher: ManagedActivityResultLauncher<String, Uri?>, fileName: String) {
        launcher.launch(fileName)
    }

    fun saveToLocal(context: Context,result: Uri) {
        val filePath = getPathFromDocumentUri(context, result) ?: return

        val dir = File(filePath)

        if (!dir.exists()) {
            val created = dir.mkdirs()
            if (!created) {
                Toast.makeText(context, context.getString(R.string.file_not_exist),Toast.LENGTH_SHORT).show()
                return
            }
        }

        try {
            // 获取所有偏好设置
            val spList = ArrayList<SP>()
            PreferencesUtil.getAllPreferences(spList)
            SPUtils.getAllPreferences(spList)

            // 使用 Gson 将 ArrayList<SP> 转换为 JSON 字符串
            val gson = Gson()
            val json = gson.toJson(spList, object : TypeToken<ArrayList<SP?>?>() {}.type)

            // 创建文件
            val file = File(filePath)
            val out = FileOutputStream(file)
            out.write(json.toByteArray())
            out.flush()
            out.close()

            Toast.makeText(context,context.getString(R.string.save_success),Toast.LENGTH_SHORT).show()
            return
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context,context.getString(R.string.save_fail),Toast.LENGTH_SHORT).show()
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }

        return
    }


    fun readJsonFile(filePath: String?): String {
        val sb = StringBuilder()
        try {
            val file = File(filePath)
            var `in`: InputStream? = null
            `in` = FileInputStream(file)
            var tempbyte: Int
            while ((`in`.read().also { tempbyte = it }) != -1) {
                sb.append(tempbyte.toChar())
            }
            `in`.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    fun readGson(context: Context,result: Uri) {
        val filePath = getPathFromDocumentUri(context, result)
        if (filePath == null) {
            Log.d("ggc", "filePath == null")
            return
        }
        Log.d("ggc", "filePath:$filePath")

        val readJsonFile = readJsonFile(filePath)

        val gson = Gson()
        val spArrayList = gson.fromJson<ArrayList<SP>>(
            readJsonFile,
            object : TypeToken<ArrayList<SP?>?>() {}.type
        )
        for (sp in spArrayList) {
            val key = sp.key
            val value = sp.value
            when (sp.type) {
                SP.type_boolean -> when (sp.spType) {
                    "SPUtils" -> SPUtils.setBoolean(
                        key,
                        (value as Boolean)
                    )

                    "PreferencesUtil" -> PreferencesUtil.putBoolean(
                        key,
                        (value as Boolean)
                    )

                    else -> Log.d(TAG, "readGson: not getSpType " + sp.key)
                }

                SP.type_float -> when (sp.spType) {
                    "SPUtils" -> if (value is Double) {
                        SPUtils.setFloat(key, value.toFloat())
                    } else {
                        SPUtils.setFloat(key, (value as Float))
                    }

                    "PreferencesUtil" -> if (value is Double) {
                        PreferencesUtil.putFloat(key, value.toFloat())
                    } else {
                        PreferencesUtil.putFloat(key, (value as Float))
                    }

                    else -> Log.d(TAG, "readGson: not getSpType " + sp.key)
                }

                SP.type_int -> when (sp.spType) {
                    "SPUtils" -> if (value is Double) {
                        SPUtils.setInt(key, value.toInt())
                    } else {
                        SPUtils.setInt(key, value as Int)
                    }

                    "PreferencesUtil" -> if (value is Double) {
                        PreferencesUtil.putInt(key, value.toInt())
                    } else {
                        PreferencesUtil.putInt(key, value as Int)
                    }

                    else -> Log.d(TAG, "readGson: not getSpType " + sp.key)
                }

                SP.type_long -> when (sp.spType) {
                    "SPUtils" -> if (value is Double) {
                        SPUtils.setLong(key, value.toLong())
                    } else {
                        SPUtils.setLong(key, (value as Long))
                    }

                    "PreferencesUtil" -> if (value is Double) {
                        PreferencesUtil.putLong(key, value.toLong())
                    } else {
                        PreferencesUtil.putLong(key, (value as Long))
                    }

                    else -> Log.d(TAG, "readGson: not getSpType " + sp.key)
                }

                else -> when (sp.spType) {
                    "SPUtils" -> SPUtils.setString(key, value as String)
                    "PreferencesUtil" -> PreferencesUtil.putString(key, value as String)
                    else -> Log.d(TAG, "readGson: not getSpType " + sp.key)
                }

            }
        }

        Toast.makeText(context, context.getString(R.string.restore_success),Toast.LENGTH_SHORT).show()
    }

    
    private fun getPathFromDocumentUri(context: Context, uri: Uri): String? {
        val documentId = DocumentsContract.getDocumentId(uri)
        val split = documentId.split(":").toTypedArray()
        val type = split[0]

        return when {
            isExternalStorageDocument(uri) -> {
                if ("primary".equals(type, ignoreCase = true)) {
                    "${Environment.getExternalStorageDirectory()}/${split[1]}"
                } else {
                    null
                }
            }
            isDownloadsDocument(uri) -> {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    split[1].toLong()
                )

                getDataColumn(context, contentUri, null, null)
                //getPathFromDownloadsDocumentUri(context,split)
            }
            isMediaDocument(uri) -> {
                val contentUri: Uri? = when (type) {
                    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> null
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                getDataColumn(context, contentUri, selection, selectionArgs)
            }
            else -> null
        }
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}
