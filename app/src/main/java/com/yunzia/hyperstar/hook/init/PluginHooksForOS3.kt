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
import com.yunzia.hyperstar.hook.app.plugin.os2.DeviceCenterRow
import com.yunzia.hyperstar.hook.app.plugin.os2.LongPressVolumeBarToExpand
import com.yunzia.hyperstar.hook.app.plugin.os2.QSEditButton
import com.yunzia.hyperstar.hook.app.plugin.os2.QSHeaderViewListener
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMediaCoverBackground
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMediaDeviceName
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMiplayAppIconRadius
import com.yunzia.hyperstar.hook.app.plugin.os2.SuperBlurVolumeManager
import com.yunzia.hyperstar.hook.app.plugin.os2.VolumeColumnProgressRadius
import com.yunzia.hyperstar.hook.app.plugin.os2.VolumeOrQSBrightnessValue
import com.yunzia.hyperstar.hook.app.plugin.os3.FixTileIconSize
import com.yunzia.hyperstar.hook.app.plugin.os3.QSControlCenterColor
import com.yunzia.hyperstar.hook.app.plugin.os3.QSLabelFollowExpandAnim
import com.yunzia.hyperstar.hook.app.plugin.os3.QSListTileLabelMarquee
import com.yunzia.hyperstar.hook.app.plugin.os3.QSListTileRadius
import com.yunzia.hyperstar.hook.app.plugin.os3.QSListView
import com.yunzia.hyperstar.hook.app.plugin.os3.QSTileAutoCollapse
import com.yunzia.hyperstar.hook.app.plugin.powermenu.PowerMenuHook
import com.yunzia.hyperstar.hook.core.base.BasePluginHooks

object PluginHooksForOS3 : BasePluginHooks() {

    override fun init() {
        hookPluginContext()
    }

    override fun onPluginLoaded() {
        initHooks(
            QSHeaderViewListener,
            SuperBlurWidgetManager,
            SuperBlurVolumeManager,
            QSMediaCoverBackground,
            QSMediaDeviceName,
            QSMediaDefaultApp,
            QSMiplayAppIconRadius,
            QSListTileRadius,
            FixTileIconSize,
            QSTileAutoCollapse,
            QSControlCenterColor,
            QSListTileLabelMarquee,
            QSLabelFollowExpandAnim,
            QSListView,
            VolumeOrQSBrightnessValue,
            QSCardTileList,
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
            QSMediaNoPlayTitle,
        )
    }
}
