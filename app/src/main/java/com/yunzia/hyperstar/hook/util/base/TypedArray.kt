package com.yunzia.hyperstar.hook.util.base

import android.content.res.Configuration
import android.util.DisplayMetrics
import de.robv.android.xposed.XposedHelpers

class TypedArray(resources: Any?) {

    val mResourcesImpl = XposedHelpers.getObjectField(resources,"mResourcesImpl")
    val mConfiguration by lazy { XposedHelpers.getObjectField(mResourcesImpl,"mConfiguration") as Configuration }
    val displayMetrics by lazy { XposedHelpers.callMethod(mResourcesImpl,"getDisplayMetrics") as DisplayMetrics }

    fun getId(name: String, type: String, packageName: String):Int {

        val id =  XposedHelpers.callMethod(mResourcesImpl,"getIdentifier",name,type,packageName) as Int

        return id
    }

}