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
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
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

    fun saveToLocal(context: Context,result: Uri):Boolean {

        try {

            val saveFile = context.contentResolver.openOutputStream(result)
            BufferedWriter(OutputStreamWriter(saveFile)).apply {
                val spList = ArrayList<SP>()
                PreferencesUtil.getAllPreferences(spList)
                SPUtils.getAllPreferences(spList)

                // 使用 Gson 将 ArrayList<SP> 转换为 JSON 字符串
                val gson = Gson()
                val json = gson.toJson(spList, object : TypeToken<ArrayList<SP?>?>() {}.type)
                write(json.toString())
                close()
            }

            Toast.makeText(context,context.getString(R.string.save_success),Toast.LENGTH_SHORT).show()
            return true
        } catch (e: JsonSyntaxException) {
            Toast.makeText(context,"文件错误！",Toast.LENGTH_SHORT).show()
            Log.e(TAG, "saveGson: $e")
        } catch (e: JsonIOException) {
            Toast.makeText(context,"读取文件失败！",Toast.LENGTH_SHORT).show()
            Log.e(TAG, "saveGson: $e")
        } catch (e: Exception) {
            Toast.makeText(context,"未知错误！",Toast.LENGTH_SHORT).show()
            Log.e(TAG, "saveGson: $e")
        }
        Toast.makeText(context,context.getString(R.string.save_fail),Toast.LENGTH_SHORT).show()

        return false
    }


    fun readGson(context: Context,result: Uri):Boolean {

        try {
            val saveFile = context.contentResolver.openInputStream(result)
            BufferedReader(InputStreamReader(saveFile)).apply {
                val sb = StringBuffer()
                var line = readLine()
                do {
                    sb.append(line)
                    line = readLine()
                } while (line != null)
                val gson = Gson()
                val spArrayList = gson.fromJson<ArrayList<SP>>(
                    sb.toString(),
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

            }


            Toast.makeText(context, context.getString(R.string.restore_success),Toast.LENGTH_SHORT).show()
            return true
        } catch (e: JsonSyntaxException) {
            Toast.makeText(context,"文件错误！",Toast.LENGTH_SHORT).show()
            Log.e(TAG, "readGson: $e")
        } catch (e: JsonIOException) {
            Toast.makeText(context,"读取文件失败！",Toast.LENGTH_SHORT).show()
            Log.e(TAG, "readGson: $e")
        } catch (e: Exception) {
            Toast.makeText(context,"未知错误！",Toast.LENGTH_SHORT).show()
            Log.e(TAG, "readGson: $e")
        }
        return false

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
