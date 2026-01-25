package com.yunzia.hyperstar.hook.util.base

import android.content.res.Configuration
import android.util.DisplayMetrics
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.getObjectField

class ResourcesImpl(resources: Any?) {

    val mResourcesImpl = resources.getObjectField("mResourcesImpl")
    val mConfiguration by lazy { mResourcesImpl.getObjectField("mConfiguration") as Configuration }
    val displayMetrics by lazy { mResourcesImpl.callMethod("getDisplayMetrics") as DisplayMetrics }

    fun getId(name: String, type: String, packageName: String):Int {
       val id =  mResourcesImpl.callMethod("getIdentifier",name,type,packageName) as Int

        return id
    }

}