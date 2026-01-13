package com.yunzia.hyperstar.utils

import io.github.libxposed.service.XposedService
import java.io.FileWriter

fun XposedService.addFile(name: String){
    openRemoteFile(name).use { pfd ->
        FileWriter(pfd.fileDescriptor).use {
            it.append("Hello World!")
        }
    }
}