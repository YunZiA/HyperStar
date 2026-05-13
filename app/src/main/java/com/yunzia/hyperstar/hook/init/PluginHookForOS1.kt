package com.yunzia.hyperstar.hook.init

import com.yunzia.hyperstar.hook.app.plugin.HideVolumeCollpasedFootButton
import com.yunzia.hyperstar.hook.app.plugin.QSCardAutoCollapse
import com.yunzia.hyperstar.hook.app.plugin.QSCardTileList
import com.yunzia.hyperstar.hook.app.plugin.QSControlCenterList
import com.yunzia.hyperstar.hook.app.plugin.QSEditText
import com.yunzia.hyperstar.hook.app.plugin.QSMediaDefaultApp
import com.yunzia.hyperstar.hook.app.plugin.QSMediaNoPlayTitle
import com.yunzia.hyperstar.hook.app.plugin.QSMiplayDetailVolumeBar
import com.yunzia.hyperstar.hook.app.plugin.QSToggleSliderRadius
import com.yunzia.hyperstar.hook.app.plugin.SuperBlurWidgetManager
import com.yunzia.hyperstar.hook.app.plugin.VolumeBarLayoutParams
import com.yunzia.hyperstar.hook.app.plugin.os1.DeviceCenterRow
import com.yunzia.hyperstar.hook.app.plugin.os1.FixTileIconSize
import com.yunzia.hyperstar.hook.app.plugin.os1.PadVolume
import com.yunzia.hyperstar.hook.app.plugin.os1.QSClockAnim
import com.yunzia.hyperstar.hook.app.plugin.os1.QSControlCenterColor
import com.yunzia.hyperstar.hook.app.plugin.os1.QSEditButton
import com.yunzia.hyperstar.hook.app.plugin.os1.QSHeaderMessage
import com.yunzia.hyperstar.hook.app.plugin.os1.QSHeaderView
import com.yunzia.hyperstar.hook.app.plugin.os1.QSListTileRadius
import com.yunzia.hyperstar.hook.app.plugin.os1.QSListView
import com.yunzia.hyperstar.hook.app.plugin.os1.QSMediaCoverBackground
import com.yunzia.hyperstar.hook.app.plugin.os1.QSMediaDeviceName
import com.yunzia.hyperstar.hook.app.plugin.os1.QSMediaView
import com.yunzia.hyperstar.hook.app.plugin.os1.QSMiplayAppIconRadius
import com.yunzia.hyperstar.hook.app.plugin.os1.QSTileAutoCollapse
import com.yunzia.hyperstar.hook.app.plugin.os1.SuperBlurVolumeManager
import com.yunzia.hyperstar.hook.app.plugin.os1.VolumeColumnProgressRadius
import com.yunzia.hyperstar.hook.app.plugin.os1.VolumeOrQSBrightnessValue
import com.yunzia.hyperstar.hook.app.plugin.powermenu.PowerMenuHook
import com.yunzia.hyperstar.hook.core.base.BasePluginHooks

object PluginHookForOS1 : BasePluginHooks() {

    override fun init() {
        hookPluginContext()
    }

    override fun onPluginLoaded() {
        initHooks(
            QSClockAnim,
            SuperBlurWidgetManager,
            SuperBlurVolumeManager,
            QSMediaCoverBackground,
            QSMediaDeviceName,
            QSMediaDefaultApp,
            QSMediaView,
            QSControlCenterColor,
            QSTileAutoCollapse,
            FixTileIconSize,
            QSListTileRadius,
            QSListView,
            VolumeOrQSBrightnessValue,
            QSCardTileList,
            QSCardAutoCollapse,
            QSToggleSliderRadius,
            QSHeaderMessage,
            QSHeaderView,
            QSEditButton,
            PadVolume,
            QSControlCenterList,
            VolumeColumnProgressRadius,
            PowerMenuHook,
            VolumeBarLayoutParams,
            HideVolumeCollpasedFootButton,
            DeviceCenterRow,
            QSMiplayDetailVolumeBar,
            QSMiplayAppIconRadius,
            QSMediaNoPlayTitle,
            QSEditText,
        )
    }
}
