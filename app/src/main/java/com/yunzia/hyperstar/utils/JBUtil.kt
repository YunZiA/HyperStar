package com.yunzia.hyperstar.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import org.json.JSONException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object JBUtil {

    private const val TAG = "ggc"

    fun openFile(
        activity: MainActivity,
        launcher: ManagedActivityResultLauncher<String, Uri?>
    ) {
        if (activity.goManagerFileAccess()) {
            launcher.launch("application/json")
        }
    }

    fun saveFile(
        activity: MainActivity,
        launcher: ManagedActivityResultLauncher<String, Uri?>
    ) {
        if (activity.goManagerFileAccess()) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            launcher.launch("HyperStar_backup_${dateFormat.format(Date())}.json")
        }
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
        try {
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
        } catch (e: JsonSyntaxException) {
            // 处理 JSON 语法错误
            // 例如：记录错误、返回错误消息等
            Toast.makeText(context,"文件错误！",Toast.LENGTH_SHORT).show()
            Log.e(TAG, "readGson: $e")
            return
        } catch (e: JsonIOException) {
            // 处理 I/O 错误，如读取文件失败
            // 例如：记录错误、返回错误消息等
            Toast.makeText(context,"读取文件失败！",Toast.LENGTH_SHORT).show()
            Log.e(TAG, "readGson: $e")
            return
        } catch (e: Exception) {
            // 处理其他可能的异常（虽然不太可能是 Gson 抛出的）
            // 例如：记录错误、返回错误消息等
            Toast.makeText(context,"未知错误！",Toast.LENGTH_SHORT).show()
            Log.e(TAG, "readGson: $e")
            return
        }

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

    fun clear(activity: MainActivity, context: Context) {
        val b1 = PreferencesUtil.clearPreferences()
        val b2 = SPUtils.clearPreferences()
        if ( b1 && b2 ){
            Toast.makeText(activity,
                context.getString(R.string.clear_success),Toast.LENGTH_SHORT).show()
            activity.recreate()
        }else{
            if (b1 || b2) {
                Toast.makeText(activity,
                    context.getString(R.string.partial_clear_successful),Toast.LENGTH_SHORT).show()
                activity.recreate()
            }else{
                Toast.makeText(activity,
                    context.getString(R.string.clear_fail),Toast.LENGTH_SHORT).show()

            }
        }
    }

}
