package com.yunzia.hyperstar.hook.util

import androidx.constraintlayout.widget.ConstraintSet
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.tool.starLog
import de.robv.android.xposed.XposedHelpers


class ConstraintSet(classLoader: ClassLoader?) {
    val constraintSet: Any? by lazy { getConstraintSet(classLoader) }

    private fun getConstraintSet(classLoader: ClassLoader?):Any? {
        var constraintSet : Any? = null
        try {
            findClass("androidx.constraintlayout.widget.ConstraintSet", classLoader)?.apply {
                constraintSet = getConstructor().newInstance()
            }
        } catch (e: Throwable) {
            starLog.logE(e.message)
            e.printStackTrace()
        }
        return constraintSet

    }

    fun constrainWidth(viewId: Int, width: Int) {
        callSafeMethod("constrainWidth", viewId, width)
    }

    fun constrainHeight(viewId: Int, height: Int) {
        callSafeMethod("constrainHeight", viewId, height)
    }

    fun connect(startID: Int, startSide: Int, endID: Int, endSide: Int) {
        callSafeMethod("connect", startID, startSide, endID, endSide)
    }
    fun connect(startID: Int, startSide: Int, endID: Int, endSide: Int, margin: Int) {
        callSafeMethod("connect", startID, startSide, endID, endSide, margin)
    }


    fun createHorizontalChainRtl(
        startId: Int,
        startSide: Int,
        endId: Int,
        endSide: Int,
        chainIds: IntArray,
        weights: FloatArray?,
        style: Int
    ) {
        callSafeMethod("createHorizontalChainRtl",
            startId, startSide, endId, endSide,
            chainIds, weights, style, ConstraintSet.START, ConstraintSet.END
        )
    }

    fun applyTo(constraintLayout: Any) {
        callSafeMethod("applyTo", constraintLayout)
    }

    private fun callSafeMethod(methodName: String, vararg args: Any?) {
        try {
            constraintSet?.let {
                XposedHelpers.callMethod(it, methodName, *args)
            }
        } catch (e: Exception) {
            // Log 或打印异常信息
            println("Error calling method $methodName: ${e.message}")
        }
    }

}