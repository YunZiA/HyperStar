package com.yunzia.hyperstar.hook.app.plugin

import android.view.View
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.replaceHookMethod
import com.yunzia.hyperstar.hook.util.plugin.Util
import com.yunzia.hyperstar.utils.XSPUtils

class HideVolumeCollpasedFootButton : Hooker() {
    val isHideStandardView = XSPUtils.getBoolean("is_hide_StandardView",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (!isHideStandardView) return

        val util = Util(classLoader)

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeDialogView",
            classLoader
        ).replaceHookMethod(
            "updateFooterVisibility",
            Boolean::class.java
        ){
            val mRingerModeLayout = this.getObjectFieldAs<View>("mRingerModeLayout")
            val mExpandButton = this.getObjectFieldAs<View>("mExpandButton")
            val mExpanded = this.callMethodAs<Boolean>("isExpanded")

            util.apply {
                setVisOrGone(mRingerModeLayout,mExpanded)
                setVisOrGone(mExpandButton, it.args[0] as Boolean && !mExpanded)
            }

        }


    }

}