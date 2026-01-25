package com.yunzia.hyperstar.hook.init

import android.content.Context
import android.content.ContextWrapper
import com.yunzia.hyperstar.hook.app.plugin.HideVolumeCollpasedFootButton
import com.yunzia.hyperstar.hook.app.plugin.QSCardAutoCollapse
import com.yunzia.hyperstar.hook.app.plugin.QSCardTileList
import com.yunzia.hyperstar.hook.app.plugin.QSControlCenterList
import com.yunzia.hyperstar.hook.app.plugin.QSEditText
import com.yunzia.hyperstar.hook.app.plugin.QSMediaDefaultApp
import com.yunzia.hyperstar.hook.app.plugin.QSMediaNoPlayTitle
import com.yunzia.hyperstar.hook.app.plugin.QSMiplayDetailVolumeBar
import com.yunzia.hyperstar.hook.app.plugin.QSToggleSliderRadius
import com.yunzia.hyperstar.hook.app.plugin.QSVolumeMute
import com.yunzia.hyperstar.hook.app.plugin.SuperBlurWidgetManager
import com.yunzia.hyperstar.hook.app.plugin.VolumeBarLayoutParams
import com.yunzia.hyperstar.hook.app.plugin.os2.DeviceCenterRow
import com.yunzia.hyperstar.hook.app.plugin.os2.LongPressVolumeBarToExpand
import com.yunzia.hyperstar.hook.app.plugin.os2.QSControlCenterColor
import com.yunzia.hyperstar.hook.app.plugin.os2.QSEditButton
import com.yunzia.hyperstar.hook.app.plugin.os2.QSHeaderViewListener
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMediaCoverBackground
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMediaDeviceName
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMiplayAppIconRadius
import com.yunzia.hyperstar.hook.app.plugin.os2.SuperBlurVolumeManager
import com.yunzia.hyperstar.hook.app.plugin.os2.VolumeColumnProgressRadius
import com.yunzia.hyperstar.hook.app.plugin.os2.VolumeOrQSBrightnessValue
import com.yunzia.hyperstar.hook.app.plugin.os3.FixTileIconSize
import com.yunzia.hyperstar.hook.app.plugin.os3.QSLabelFollowExpandAnim
import com.yunzia.hyperstar.hook.app.plugin.os3.QSListTileLabelMarquee
import com.yunzia.hyperstar.hook.app.plugin.os3.QSListTileRadius
import com.yunzia.hyperstar.hook.app.plugin.os3.QSListView
import com.yunzia.hyperstar.hook.app.plugin.os3.QSTileAutoCollapse
import com.yunzia.hyperstar.hook.app.plugin.powermenu.PowerMenuHook
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.hook.core.BasePluginHooks
import com.yunzia.hyperstar.hook.core.Log.logD
import com.yunzia.hyperstar.hook.core.provider.PluginClassLoaderProvider
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.finder.loadClass
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod


object PluginHooksForOS3 : BasePluginHooks() {

    override fun init() {
        "com.android.systemui.shared.plugins.PluginInstance\$PluginFactory".loadClass().afterHookMethod("createPluginContext"){
            val mPluginContext = it.result as ContextWrapper
            if (mPluginContext.packageName != plugin){
                logD("检测到非目标应用包名: 当前包名为 " + mPluginContext.packageName + ", 目标插件包名为 " + plugin)
                return@afterHookMethod
            }
            initPlugin(mPluginContext)
        }
    }


//    override fun initResources() {
//
//        if (resparam!!.packageName != plugin) return
//        log("PluginHookForOS3 initResources")
//
//        //initResource(qsControlCenterColor)
//        initResource(QSMediaNoPlayTitle,
//        initResource(QSEditText,
//
//
//
//    }



    override fun onPluginLoaded() {

        initHooks(
            QSHeaderViewListener,
            SuperBlurWidgetManager,
            SuperBlurVolumeManager,
            QSMediaCoverBackground,
            QSMediaDeviceName,
            QSMediaDefaultApp,
            QSMiplayAppIconRadius,
            //QSMediaView,
            QSListTileRadius,
            FixTileIconSize,
            QSTileAutoCollapse,
            QSControlCenterColor,
            QSListTileLabelMarquee,
            QSLabelFollowExpandAnim,
            QSListView,
            VolumeOrQSBrightnessValue,
            QSCardTileList,
            QSVolumeMute,
            QSCardAutoCollapse,
            QSToggleSliderRadius,
            QSEditButton,
            QSControlCenterList,
            VolumeColumnProgressRadius,
            PowerMenuHook,
            VolumeBarLayoutParams,
            LongPressVolumeBarToExpand,
            HideVolumeCollpasedFootButton,
            DeviceCenterRow,
            QSMiplayDetailVolumeBar,
            QSEditText,
            QSMediaNoPlayTitle
        )

    }

    fun flipCard(){
        findClass(
            "miui.systemui.util.CommonUtils",
            PluginClassLoaderProvider.classLoader
        ).apply {
            replaceHookMethod(
                "isFlipDevice"
            ){
                return@replaceHookMethod true
            }
            replaceHookMethod(
                "isTinyScreen",
                Context::class.java
            ){
                return@replaceHookMethod true
            }
        }

        //折叠屏卡片
        findClass(
            "miui.systemui.controlcenter.panel.main.qs.CompactQSCardController",
            PluginClassLoaderProvider.classLoader
        ).apply {
            replaceHookMethod(
                "onCreate",
            ) {
                return@replaceHookMethod null
            }
        }
    }




}