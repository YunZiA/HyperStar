package com.yunzia.hyperstar.hook.app.plugin.os3

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.yunzia.hyperstar.hook.base.BaseHookHelper.setColorField
import com.yunzia.hyperstar.hook.base.findViewByIdNameAs
import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.colorReplaceByIdName
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.colorReplaceByValue
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.drawableReplaceByValue
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.intArrayReplaceByIdName
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.intArrayReplaceByValue
import com.yunzia.hyperstar.hook.core.helper.afterHookAllConstructors
import com.yunzia.hyperstar.hook.core.helper.afterHookAllMethods
import com.yunzia.hyperstar.hook.core.helper.afterHookConstructor
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.callMethodAs
import com.yunzia.hyperstar.hook.core.helper.getIntField
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.core.helper.getStaticObjectField
import com.yunzia.hyperstar.hook.util.android.findViewByIdName
import com.yunzia.hyperstar.hook.util.plugin.ConfigUtils
import com.yunzia.hyperstar.prefs.XSPUtils

private fun String?.parseColorInt(): Int? {
    return if (this != null && this != "null") Color.parseColor(this) else null
}

object QSControlCenterColor : BasePluginHook() {

    override fun init() {
        initCardTitleColors()
        initCardIconColors()
        initMediaColors()
        initToggleSliderIconColor()
        initListIconColors()
        initDeviceColor()
        initEditColor()

        initBackgroundResources()
        initCardResources()
        initToggleSliderResources()
        initListResources()
        initDeviceResources()
    }

    // ==================== Card Title Colors ====================

    private fun initCardTitleColors() {
        val disablePrimaryColorInt = XSPUtils.getString("card_primary_disabled_color", "null").parseColorInt()
        val enablePrimaryColorInt = XSPUtils.getString("card_primary_enabled_color", "null").parseColorInt()
        val restrictedPrimaryColorInt = XSPUtils.getString("card_primary_restricted_color", "null").parseColorInt()
        val unavailablePrimaryColorInt = XSPUtils.getString("card_primary_unavailable_color", "null").parseColorInt()
        val disableSecondaryColorInt = XSPUtils.getString("card_secondary_disabled_color", "null").parseColorInt()
        val enableSecondaryColorInt = XSPUtils.getString("card_secondary_enabled_color", "null").parseColorInt()
        val restrictedSecondaryColorInt = XSPUtils.getString("card_secondary_restricted_color", "null").parseColorInt()
        val unavailableSecondaryColorInt = XSPUtils.getString("card_secondary_unavailable_color", "null").parseColorInt()

        val QSItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSItemView", pluginClassLoader)
        val QSCardItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSCardItemView", pluginClassLoader)

        QSCardItemView.afterHookAllMethods("updateState") { _, _ ->
            (thisObject as LinearLayout).apply {
                val Companion = QSItemView.getStaticObjectField("Companion")
                if (Companion == null) {
                    logE("QSItemView Companion is null")
                    return@afterHookAllMethods
                }
                val sta = thisObject.getObjectField("state")
                val states = Companion.callMethodAs<Boolean>("isRestrictedCompat", sta)
                val state = sta.getIntField("state")
                val title = this.findViewByIdNameAs<TextView>("title")
                val status = this.findViewByIdNameAs<TextView>("status")

                when (state) {
                    1 -> {
                        disablePrimaryColorInt?.let { title.setTextColor(it) }
                        disableSecondaryColorInt?.let { status.setTextColor(it) }
                    }
                    2 -> {
                        enablePrimaryColorInt?.let { title.setTextColor(it) }
                        enableSecondaryColorInt?.let { status.setTextColor(it) }
                    }
                    else -> {
                        if (states) {
                            restrictedPrimaryColorInt?.let { title.setTextColor(it) }
                            restrictedSecondaryColorInt?.let { status.setTextColor(it) }
                        } else {
                            unavailablePrimaryColorInt?.let { title.setTextColor(it) }
                            unavailableSecondaryColorInt?.let { status.setTextColor(it) }
                        }
                    }
                }
            }
        }
    }

    // ==================== Card Icon Colors ====================

    private fun initCardIconColors() {
        val offColor = XSPUtils.getString("card_icon_off_color", "null")
        val onColor = XSPUtils.getString("card_icon_on_color", "null")
        val restrictedColor = XSPUtils.getString("card_icon_restricted_color", "null")
        val unavailableColor = XSPUtils.getString("card_icon_unavailable_color", "null")

        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSCardItemIconView",
            pluginClassLoader
        ).afterHookMethod("updateResources") { _, _ ->
            if (onColor != "null") setColorField(thisObject, "iconColor", onColor)
            if (offColor != "null") setColorField(thisObject, "iconColorOff", offColor)
            if (restrictedColor != "null") setColorField(thisObject, "iconColorRestricted", restrictedColor)
            if (unavailableColor != "null") setColorField(thisObject, "iconColorUnavailable", unavailableColor)
        }
    }

    // ==================== Media Colors ====================

    private fun initMediaColors() {
        val configUtils = ConfigUtils(pluginClassLoader)
        val titleColorInt = XSPUtils.getString("media_title_color", "null").parseColorInt()
        val artistColorInt = XSPUtils.getString("media_artist_color", "null").parseColorInt()
        val emptyStateColorInt = XSPUtils.getString("media_empty_state_color", "null").parseColorInt()
        val disabledIconColorInt = XSPUtils.getString("media_icon_color_disabled", "null").parseColorInt()
        val enabledIconColorInt = XSPUtils.getString("media_icon_color_enabled", "null").parseColorInt()
        val deviceIconColorInt = XSPUtils.getString("media_device_icon_color", "null").parseColorInt()
        val MediaPlayerIconsInfo = findClass("miui.systemui.controlcenter.media.MediaPlayerIconsInfo", pluginClassLoader)

        findClass(
            "miui.systemui.controlcenter.panel.main.media.MediaPlayerController\$MediaPlayerViewHolder",
            pluginClassLoader
        ).apply {
            afterHookAllConstructors { _, _ ->
                val itemView = thisObject.getObjectFieldAs<View>("itemView")
                deviceIconColorInt?.let {
                    val deviceIcon = itemView.findViewByIdNameAs<ImageView>("device_icon")
                    deviceIcon.alpha = 1f
                    deviceIcon.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)
                }
                titleColorInt?.let {
                    itemView.findViewByIdNameAs<TextView>("title").setTextColor(it)
                }
                artistColorInt?.let {
                    itemView.findViewByIdNameAs<TextView>("artist").setTextColor(it)
                }
                emptyStateColorInt?.let {
                    itemView.findViewByIdNameAs<TextView>("empty_state").setTextColor(it)
                }
            }
            afterHookMethod("onConfigurationChanged", Int::class.java) { args, _ ->
                if (configUtils.textAppearanceChanged(args[0])) {
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    titleColorInt?.let { itemView.findViewByIdNameAs<TextView>("title").setTextColor(it) }
                    artistColorInt?.let { itemView.findViewByIdNameAs<TextView>("artist").setTextColor(it) }
                    emptyStateColorInt?.let { itemView.findViewByIdNameAs<TextView>("empty_state").setTextColor(it) }
                }
            }
            afterHookMethod("disableMediaController") { _, _ ->
                val itemView = thisObject.getObjectFieldAs<View>("itemView")
                disabledIconColorInt?.let { color ->
                    val filter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
                    itemView.findViewByIdNameAs<ImageView>("prev").colorFilter = filter
                    itemView.findViewByIdNameAs<ImageView>("next").colorFilter = filter
                }
                enabledIconColorInt?.let { color ->
                    (itemView.findViewByIdName("play") as ImageView).colorFilter =
                        PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
                }
            }
            if (deviceIconColorInt != null) {
                afterHookMethod("updateIconsInfo", MediaPlayerIconsInfo, Boolean::class.java) { args, _ ->
                    val deviceRes = thisObject.getObjectField("deviceRes")
                    val getDeviceRes = args[0].callMethodAs<Int>("getDeviceRes")
                    if (deviceRes != getDeviceRes || args[1] as Boolean) {
                        val itemView = thisObject.getObjectFieldAs<View>("itemView")
                        val deviceIcon = itemView.findViewByIdNameAs<ImageView>("device_icon")
                        deviceIcon.alpha = 1f
                        deviceIcon.colorFilter = PorterDuffColorFilter(deviceIconColorInt, PorterDuff.Mode.SRC_IN)
                    }
                }
            }
            if (enabledIconColorInt != null) {
                afterHookMethod("enableMediaController") { _, _ ->
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    val filter = PorterDuffColorFilter(enabledIconColorInt, PorterDuff.Mode.SRC_IN)
                    itemView.findViewByIdNameAs<ImageView>("prev").colorFilter = filter
                    itemView.findViewByIdNameAs<ImageView>("play").colorFilter = filter
                    itemView.findViewByIdNameAs<ImageView>("next").colorFilter = filter
                }
            }
        }
    }

    // ==================== Toggle Slider Icon Color ====================

    private fun initToggleSliderIconColor() {
        val iconColorInt = XSPUtils.getString("toggle_slider_icon_color", "null").parseColorInt() ?: return

        val BrightnessSliderController = findClass(
            "miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController", pluginClassLoader
        )
        val VolumeSliderController = findClass(
            "miui.systemui.controlcenter.panel.main.volume.VolumeSliderController", pluginClassLoader
        )
        val filter = PorterDuffColorFilter(iconColorInt, PorterDuff.Mode.SRC_IN)

        BrightnessSliderController.afterHookMethod("updateIcon") { _, _ ->
            val sliderHolder = thisObject.callMethod("getSliderHolder")
            val itemView = sliderHolder.getObjectFieldAs<View>("itemView")
            val icon = itemView.findViewByIdNameAs<ImageView>("icon")
            (icon.drawable as? AnimatedVectorDrawable)?.colorFilter = filter
        }
        VolumeSliderController.afterHookMethod("updateIcon", Boolean::class.java) { _, _ ->
            val sliderHolder = thisObject.callMethod("getSliderHolder")
            val itemView = sliderHolder.getObjectFieldAs<View>("itemView")
            val icon = itemView.findViewByIdNameAs<ImageView>("icon")
            (icon.drawable as? AnimatedVectorDrawable)?.colorFilter = filter
        }
    }

    // ==================== List Icon Colors ====================

    private fun initListIconColors() {
        val offColor = XSPUtils.getString("list_icon_off_color", "null")
        val onColor = XSPUtils.getString("list_icon_on_color", "null")
        val restrictedColor = XSPUtils.getString("list_icon_restricted_color", "null")
        val unavailableColor = XSPUtils.getString("list_icon_unavailable_color", "null")

        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            pluginClassLoader
        ).afterHookMethod("updateResources") { _, _ ->
            if (onColor != "null") setColorField(thisObject, "iconColor", onColor)
            if (offColor != "null") setColorField(thisObject, "iconColorOff", offColor)
            if (restrictedColor != "null") setColorField(thisObject, "iconColorRestrict", restrictedColor)
            if (unavailableColor != "null") setColorField(thisObject, "iconColorUnavailable", unavailableColor)
        }
    }

    // ==================== Device Center Colors ====================

    private fun initDeviceColor() {
        val configUtils = ConfigUtils(pluginClassLoader)
        val deviceCenterIconColorInt = XSPUtils.getString("device_center_icon_color", "null").parseColorInt()
        val deviceCenterTitleColorInt = XSPUtils.getString("device_center_title_color", "null").parseColorInt()

        findClass(
            "miui.systemui.controlcenter.panel.main.devicecenter.devices.EmptyDeviceViewHolder",
            pluginClassLoader
        ).apply {
            if (deviceCenterTitleColorInt != null || deviceCenterIconColorInt != null) {
                afterHookConstructor(View::class.java) { _, _ ->
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    deviceCenterIconColorInt?.let {
                        itemView.findViewByIdNameAs<ImageView>("icon").colorFilter =
                            PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)
                    }
                    deviceCenterTitleColorInt?.let {
                        itemView.findViewByIdNameAs<TextView>("title").setTextColor(it)
                    }
                }
            }
            if (deviceCenterTitleColorInt != null) {
                afterHookMethod("onConfigurationChanged", Int::class.java) { args, _ ->
                    if (configUtils.textAppearanceChanged(args[0])) {
                        val itemView = thisObject.getObjectFieldAs<View>("itemView")
                        itemView.findViewByIdNameAs<TextView>("title").setTextColor(deviceCenterTitleColorInt)
                    }
                }
            }
        }

        val deviceControlIconColorInt = XSPUtils.getString("device_control_icon_color", "null").parseColorInt()
        val deviceControlTitleColorInt = XSPUtils.getString("device_control_title_color", "null").parseColorInt()

        findClass(
            "miui.systemui.controlcenter.panel.main.devicecontrol.DeviceControlsEntryController\$DeviceControlEntryViewHolder",
            pluginClassLoader
        ).apply {
            if (deviceControlTitleColorInt != null || deviceControlIconColorInt != null) {
                afterHookConstructor(View::class.java) { _, _ ->
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    deviceControlIconColorInt?.let {
                        itemView.findViewByIdNameAs<ImageView>("entry_icon").colorFilter =
                            PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)
                    }
                    deviceControlTitleColorInt?.let {
                        itemView.findViewByIdNameAs<TextView>("entry_title").setTextColor(it)
                    }
                }
            }
            if (deviceControlTitleColorInt != null) {
                afterHookMethod("onConfigurationChanged", Int::class.java) { args, _ ->
                    if (configUtils.textAppearanceChanged(args[0])) {
                        val itemView = thisObject.getObjectFieldAs<View>("itemView")
                        itemView.findViewByIdNameAs<TextView>("entry_title").setTextColor(deviceControlTitleColorInt)
                    }
                }
            }
        }
    }

    // ==================== Edit Button Color ====================

    private fun initEditColor() {
        val editTitleColorInt = XSPUtils.getString("edit_title_color", "null").parseColorInt() ?: return
        val ConfigUtils = findClass("miui.systemui.controlcenter.ConfigUtils", pluginClassLoader)

        findClass(
            "miui.systemui.controlcenter.panel.main.qs.EditButtonController\$EditButtonViewHolder",
            pluginClassLoader
        ).apply {
            afterHookConstructor(View::class.java) { _, _ ->
                val itemView = thisObject.getObjectFieldAs<View>("itemView")
                (itemView.findViewByIdName("text") as TextView).setTextColor(editTitleColorInt)
            }
            afterHookMethod("onConfigurationChanged", Int::class.java) { args, _ ->
                val INSTANCE = ConfigUtils.getStaticObjectField("INSTANCE")
                if (INSTANCE.callMethodAs<Boolean>("textAppearanceChanged", args[0]) == true) {
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    itemView.findViewByIdNameAs<TextView>("text").setTextColor(editTitleColorInt)
                }
            }
        }
    }

    // ==================== Background Resources ====================

    private fun initBackgroundResources() {
        val backgroundColor = XSPUtils.getString("background_color", "null")
        val editBackgroundColor = XSPUtils.getString("edit_background_color", "null")
        val editBackgroundMode = XSPUtils.getInt("edit_background_mode", 0)

        val mainBackgroundBlendColorInt = XSPUtils.getString("background_blend_color_main", "null").parseColorInt()
        val secondaryBackgroundBlendColorInt = XSPUtils.getString("background_blend_color_secondary", "null").parseColorInt()
        val mainEditBackgroundBlendColorInt = XSPUtils.getString("edit_background_blend_color_main", "null").parseColorInt()
        val secondaryEditBackgroundBlendColorInt = XSPUtils.getString("edit_background_blend_color_secondary", "null").parseColorInt()

        if (backgroundColor != "null") {
            colorReplaceByValue("qs_card_disabled_color", plugin, backgroundColor)
            colorReplaceByValue("external_entry_background_color", plugin, backgroundColor)
            colorReplaceByValue("toggle_slider_progress_background_color", plugin, backgroundColor)
            colorReplaceByValue("qs_disabled_color", plugin, backgroundColor)
        }

        if (mainBackgroundBlendColorInt != null || secondaryBackgroundBlendColorInt != null) {
            intArrayReplaceByValue("control_center_list_items_blend_colors", plugin) {
                mainBackgroundBlendColorInt?.let { this[0] = it }
                secondaryBackgroundBlendColorInt?.let { this[2] = it }
            }
        }

        if (editBackgroundMode == 0) {
            if (editBackgroundColor != "null") {
                colorReplaceByValue("qs_customize_entry_button_background_color", plugin, editBackgroundColor)
            }
            intArrayReplaceByValue("control_center_edit_button_blend_colors", plugin) {
                mainEditBackgroundBlendColorInt?.let { this[0] = it }
                secondaryEditBackgroundBlendColorInt?.let { this[2] = it }
            }
        } else {
            intArrayReplaceByIdName("array", "control_center_edit_button_blend_colors", plugin, "control_center_list_items_blend_colors")
            colorReplaceByIdName("qs_customize_entry_button_background_color", plugin, "external_entry_background_color")
        }
    }

    // ==================== Card Resources ====================

    private fun initCardResources() {
        val enableColor = XSPUtils.getString("card_enabled_color", "null")
        val restrictedColor = XSPUtils.getString("card_restricted_color", "null")
        val unavailableColor = XSPUtils.getString("card_unavailable_color", "null")

        if (enableColor != "null") {
            colorReplaceByValue("qs_card_enabled_color", plugin, enableColor)
            colorReplaceByValue("qs_card_cellular_color", plugin, enableColor)
            colorReplaceByValue("qs_card_flashlight_color", plugin, enableColor)
        }
        if (restrictedColor != "null") {
            colorReplaceByValue("qs_card_unavailable_color", plugin, restrictedColor)
        }
        if (unavailableColor != "null") {
            colorReplaceByValue("qs_card_disabled_color", plugin, unavailableColor)
        }
    }

    // ==================== Toggle Slider Resources ====================

    private fun initToggleSliderResources() {
        val mainProgressBlendColorInt = XSPUtils.getString("toggle_slider_progress_color_main", "null").parseColorInt()
        val secondaryProgressBlendColorInt = XSPUtils.getString("toggle_slider_progress_color_secondary", "null").parseColorInt()
        val progressColor = XSPUtils.getString("toggle_slider_progress_color", "null")
        val valueColor = XSPUtils.getString("toggle_slider_value_color", "null")

        if (progressColor != "null") colorReplaceByValue("toggle_slider_progress_color", plugin, progressColor)
        if (valueColor != "null") colorReplaceByValue("toggle_slider_top_text_color", plugin, valueColor)

        intArrayReplaceByValue("toggle_slider_progress_blend_colors", plugin) {
            mainProgressBlendColorInt?.let { this[0] = it }
            secondaryProgressBlendColorInt?.let { this[2] = it }
        }
    }

    // ==================== List Resources ====================

    private fun initListResources() {
        val enableColor = XSPUtils.getString("list_enabled_color", "null")
        val restrictedColor = XSPUtils.getString("list_restricted_color", "null")
        val warningColor = XSPUtils.getString("list_warning_color", "null")
        val unavailableColor = XSPUtils.getString("list_unavailable_color", "null")
        val tileColorForState = XSPUtils.getInt("qs_list_tile_color_for_state", 0)

        if (tileColorForState == 0) {
            val titleColor = XSPUtils.getString("list_title_color", "null")
            if (titleColor != "null") colorReplaceByValue("qs_text_disabled_color", plugin, titleColor)
        }
        if (enableColor != "null") {
            colorReplaceByValue("qs_enabled_color", plugin, enableColor)
            colorReplaceByValue("qs_detail_enabled_color", plugin, enableColor)
        }
        if (warningColor != "null") {
            colorReplaceByValue("qs_warning_color", plugin, warningColor)
            colorReplaceByValue("qs_detail_warning_color", plugin, warningColor)
        }
        if (restrictedColor != "null") {
            colorReplaceByValue("qs_restrict_color", plugin, restrictedColor)
            colorReplaceByValue("qs_detail_restrict_color", plugin, restrictedColor)
        }
        if (unavailableColor != "null") {
            colorReplaceByValue("qs_unavailable_color", plugin, unavailableColor)
            colorReplaceByValue("qs_detail_unavailable_color", plugin, unavailableColor)
        }
    }

    // ==================== Device Resources ====================

    private fun initDeviceResources() {
        val deviceCenterDetailIconColorInt = XSPUtils.getString("device_center_detail_icon_color", "null").parseColorInt()
        val deviceCenterItemBackgroundColorInt = XSPUtils.getString("device_center_item_background_color", "null").parseColorInt()

        deviceCenterDetailIconColorInt?.let { color ->
            drawableReplaceByValue(plugin, "ic_device_center_detail_item") {
                colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        }
        deviceCenterItemBackgroundColorInt?.let { color ->
            drawableReplaceByValue(plugin, "ic_device_center_item_background_default") {
                colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        }
    }
}
