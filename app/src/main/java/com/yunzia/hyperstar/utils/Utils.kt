package com.yunzia.hyperstar.utils

import android.content.Context
import android.widget.Toast
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader


object Utils {

    fun getRootPermission(): Int {
        var process: Process? = null
        val exitCode = -1
        try {
            process = Runtime.getRuntime().exec("su -c true")
            return process.waitFor()
        } catch (e: IOException) {
            return exitCode
        } catch (e: InterruptedException) {
            return exitCode
        } finally {
            process?.destroy()
        }
    }

    fun rootShell(cmd: String): String {
        val output = java.lang.StringBuilder()
        try {
            val process = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(process.outputStream)
            //Log.d("ggc", cmd);
            outputStream.writeBytes(cmd + "\n")
            outputStream.flush()
            outputStream.writeBytes("exit\n")
            outputStream.flush()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while ((reader.readLine().also { line = it }) != null) {
                output.append(line).append("")
            }
            process.waitFor()
        } catch (e: IOException) {
            e.printStackTrace()
            return e.message.toString()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return e.message.toString()
        }
        return "0"
//        if (output.toString().isEmpty()) {
//            return "0"
//        }
//        return output.toString()
    }

}