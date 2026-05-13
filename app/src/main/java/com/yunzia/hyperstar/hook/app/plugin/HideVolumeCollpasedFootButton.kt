package com.yunzia.hyperstar.hook.app.plugin

import android.view.View
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.callMethodAs
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.hook.util.plugin.Util
import com.yunzia.hyperstar.prefs.XSPUtils

object HideVolumeCollpasedFootButton : BasePluginHook() {
    val isHideStandardView = XSPUtils.getBoolean("is_hide_StandardView",false)

    override fun init() {
        if (!isHideStandardView) return
        val util = Util(pluginClassLoader)

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeDialogView",
            pluginClassLoader
        ).replaceHookMethod(
            "updateFooterVisibility",
            Boolean::class.java
        ) { args ->
            val mRingerModeLayout = thisObject.getObjectFieldAs<View>("mRingerModeLayout")
            val mExpandButton = thisObject.getObjectFieldAs<View>("mExpandButton")
            val mExpanded = thisObject.callMethodAs<Boolean>("isExpanded")

            util.apply {
                setVisOrGone(mRingerModeLayout, mExpanded)
                setVisOrGone(mExpandButton, args[0] as Boolean && !mExpanded)
            }
        }
    }
}