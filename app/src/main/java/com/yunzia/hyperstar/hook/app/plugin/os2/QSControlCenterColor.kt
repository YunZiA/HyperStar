package com.yunzia.hyperstar.hook.app.plugin.os2

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

    private fun initDeviceResources() {
        val detailIconColorInt = XSPUtils.getString("device_center_detail_icon_color", "null").parseColorInt()
        val itemBgColorInt = XSPUtils.getString("device_center_item_background_color", "null").parseColorInt()

        if (detailIconColorInt != null) {
            drawableReplaceByValue(plugin, "ic_device_center_detail_item") {
                colorFilter = PorterDuffColorFilter(detailIconColorInt, PorterDuff.Mode.SRC_IN)
            }
        }
        if (itemBgColorInt != null) {
            drawableReplaceByValue(plugin, "ic_device_center_item_background_default") {
                colorFilter = PorterDuffColorFilter(itemBgColorInt, PorterDuff.Mode.SRC_IN)
            }
        }
    }

    private fun initEditColor() {
        val editTitleColorInt = XSPUtils.getString("edit_title_color", "null").parseColorInt() ?: return
        val configUtilsClass = findClass("miui.systemui.controlcenter.ConfigUtils", pluginClassLoader)

        findClass(
            "miui.systemui.controlcenter.panel.main.qs.EditButtonController\$EditButtonViewHolder",
            pluginClassLoader
        ).apply {
            afterHookConstructor(View::class.java) { _, _ ->
                val itemView = thisObject.getObjectFieldAs<View>("itemView")
                val text = itemView.findViewByIdName("text") as TextView
                text.setTextColor(editTitleColorInt)
            }
            afterHookMethod("onConfigurationChanged", Int::class.java) { args, _ ->
                val instance = configUtilsClass.getStaticObjectField("INSTANCE")
                val changed = instance.callMethodAs<Boolean>("textAppearanceChanged", args[0])
                if (changed) {
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    val text = itemView.findViewByIdNameAs<TextView>("text")
                    text.setTextColor(editTitleColorInt)
                }
            }
        }
    }

    // PLACEHOLDER_DEVICE_COLOR

    private fun initDeviceColor() {
        val configUtils = ConfigUtils(pluginClassLoader)
        val iconColorInt = XSPUtils.getString("device_center_icon_color", "null").parseColorInt()
        val titleColorInt = XSPUtils.getString("device_center_title_color", "null").parseColorInt()

        findClass(
            "miui.systemui.controlcenter.panel.main.devicecenter.devices.EmptyDeviceViewHolder",
            pluginClassLoader
        ).apply {
            if (titleColorInt != null || iconColorInt != null) {
                afterHookConstructor(View::class.java) { _, _ ->
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    if (iconColorInt != null) {
                        val icon = itemView.findViewByIdNameAs<ImageView>("icon")
                        icon.colorFilter = PorterDuffColorFilter(iconColorInt, PorterDuff.Mode.SRC_IN)
                    }
                    if (titleColorInt != null) {
                        val title = itemView.findViewByIdNameAs<TextView>("title")
                        title.setTextColor(titleColorInt)
                    }
                }
            }
            if (titleColorInt != null) {
                afterHookMethod("onConfigurationChanged", Int::class.java) { args, _ ->
                    if (configUtils.textAppearanceChanged(args[0])) {
                        val itemView = thisObject.getObjectFieldAs<View>("itemView")
                        val title = itemView.findViewByIdNameAs<TextView>("title")
                        title.setTextColor(titleColorInt)
                    }
                }
            }
        }

        val controlIconColorInt = XSPUtils.getString("device_control_icon_color", "null").parseColorInt()
        val controlTitleColorInt = XSPUtils.getString("device_control_title_color", "null").parseColorInt()

        findClass(
            "miui.systemui.controlcenter.panel.main.devicecontrol.DeviceControlsEntryController\$DeviceControlEntryViewHolder",
            pluginClassLoader
        ).apply {
            if (controlTitleColorInt != null || controlIconColorInt != null) {
                afterHookConstructor(View::class.java) { _, _ ->
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    if (controlIconColorInt != null) {
                        val entryIcon = itemView.findViewByIdNameAs<ImageView>("entry_icon")
                        entryIcon.colorFilter = PorterDuffColorFilter(controlIconColorInt, PorterDuff.Mode.SRC_IN)
                    }
                    if (controlTitleColorInt != null) {
                        val entryTitle = itemView.findViewByIdNameAs<TextView>("entry_title")
                        entryTitle.setTextColor(controlTitleColorInt)
                    }
                }
            }
            if (controlTitleColorInt != null) {
                afterHookMethod("onConfigurationChanged", Int::class.java) { args, _ ->
                    if (configUtils.textAppearanceChanged(args[0])) {
                        val itemView = thisObject.getObjectFieldAs<View>("itemView")
                        val entryTitle = itemView.findViewByIdNameAs<TextView>("entry_title")
                        entryTitle.setTextColor(controlTitleColorInt)
                    }
                }
            }
        }
    }

    // PLACEHOLDER_TOGGLE_SLIDER

    private fun initToggleSliderResources() {
        val mainBlendColorInt = XSPUtils.getString("toggle_slider_progress_color_main", "null").parseColorInt()
        val secondaryBlendColorInt = XSPUtils.getString("toggle_slider_progress_color_secondary", "null").parseColorInt()
        val progressColor = XSPUtils.getString("toggle_slider_progress_color", "null")
        val valueColor = XSPUtils.getString("toggle_slider_value_color", "null")

        if (progressColor != "null") colorReplaceByValue("toggle_slider_progress_color", plugin, progressColor)
        if (valueColor != "null") colorReplaceByValue("toggle_slider_top_text_color", plugin, valueColor)

        intArrayReplaceByValue("toggle_slider_progress_blend_colors", plugin) {
            if (mainBlendColorInt != null) this[0] = mainBlendColorInt
            if (secondaryBlendColorInt != null) this[2] = secondaryBlendColorInt
        }
    }

    private fun initBackgroundResources() {
        val backgroundColor = XSPUtils.getString("background_color", "null")
        val editBackgroundColor = XSPUtils.getString("edit_background_color", "null")
        val editBackgroundMode = XSPUtils.getInt("edit_background_mode", 0)

        val mainBlendColorInt = XSPUtils.getString("background_blend_color_main", "null").parseColorInt()
        val secondaryBlendColorInt = XSPUtils.getString("background_blend_color_secondary", "null").parseColorInt()
        val mainEditBlendColorInt = XSPUtils.getString("edit_background_blend_color_main", "null").parseColorInt()
        val secondaryEditBlendColorInt = XSPUtils.getString("edit_background_blend_color_secondary", "null").parseColorInt()

        if (backgroundColor != "null") {
            colorReplaceByValue("qs_card_disabled_color", plugin, backgroundColor)
            colorReplaceByValue("external_entry_background_color", plugin, backgroundColor)
            colorReplaceByValue("toggle_slider_progress_background_color", plugin, backgroundColor)
            colorReplaceByValue("qs_disabled_color", plugin, backgroundColor)
        }

        if (mainBlendColorInt != null || secondaryBlendColorInt != null) {
            intArrayReplaceByValue("control_center_list_items_blend_colors", plugin) {
                if (mainBlendColorInt != null) this[0] = mainBlendColorInt
                if (secondaryBlendColorInt != null) this[2] = secondaryBlendColorInt
            }
        }

        if (editBackgroundMode == 0) {
            if (editBackgroundColor != "null") {
                colorReplaceByValue("qs_customize_entry_button_background_color", plugin, editBackgroundColor)
            }
            intArrayReplaceByValue("control_center_edit_button_blend_colors", plugin) {
                if (mainEditBlendColorInt != null) this[0] = mainEditBlendColorInt
                if (secondaryEditBlendColorInt != null) this[2] = secondaryEditBlendColorInt
            }
        } else {
            intArrayReplaceByIdName("array", "control_center_edit_button_blend_colors", plugin, "control_center_list_items_blend_colors")
            colorReplaceByIdName("qs_customize_entry_button_background_color", plugin, "external_entry_background_color")
        }
    }

    // PLACEHOLDER_CARD_RESOURCES

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

    // PLACEHOLDER_SLIDER_ICON

    private fun initToggleSliderIconColor() {
        val iconColorInt = XSPUtils.getString("toggle_slider_icon_color", "null").parseColorInt() ?: return
        val brightnessController = findClass("miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController", pluginClassLoader)
        val volumeController = findClass("miui.systemui.controlcenter.panel.main.volume.VolumeSliderController", pluginClassLoader)

        brightnessController.afterHookMethod("updateIcon") { _, _ ->
            val sliderHolder = thisObject.callMethod("getSliderHolder")
            val itemView = sliderHolder.getObjectFieldAs<View>("itemView")
            val icon = itemView.findViewByIdNameAs<ImageView>("icon")
            val drawable = icon.drawable
            if (drawable is AnimatedVectorDrawable) {
                drawable.colorFilter = PorterDuffColorFilter(iconColorInt, PorterDuff.Mode.SRC_IN)
            }
        }
        volumeController.afterHookMethod("updateIcon", Boolean::class.java) { _, _ ->
            val sliderHolder = thisObject.callMethod("getSliderHolder")
            val itemView = sliderHolder.getObjectFieldAs<View>("itemView")
            val icon = itemView.findViewByIdNameAs<ImageView>("icon")
            val drawable = icon.drawable
            if (drawable is AnimatedVectorDrawable) {
                drawable.colorFilter = PorterDuffColorFilter(iconColorInt, PorterDuff.Mode.SRC_IN)
            }
        }
    }

    // PLACEHOLDER_MEDIA

    private fun initMediaColors() {
        val configUtils = ConfigUtils(pluginClassLoader)
        val titleColorInt = XSPUtils.getString("media_title_color", "null").parseColorInt()
        val artistColorInt = XSPUtils.getString("media_artist_color", "null").parseColorInt()
        val emptyStateColorInt = XSPUtils.getString("media_empty_state_color", "null").parseColorInt()
        val disabledIconColorInt = XSPUtils.getString("media_icon_color_disabled", "null").parseColorInt()
        val enabledIconColorInt = XSPUtils.getString("media_icon_color_enabled", "null").parseColorInt()
        val deviceIconColorInt = XSPUtils.getString("media_device_icon_color", "null").parseColorInt()
        val mediaPlayerIconsInfo = findClass("miui.systemui.controlcenter.media.MediaPlayerIconsInfo", pluginClassLoader)

        findClass(
            "miui.systemui.controlcenter.panel.main.media.MediaPlayerController\$MediaPlayerViewHolder",
            pluginClassLoader
        ).apply {
            afterHookAllConstructors { _, _ ->
                val itemView = thisObject.getObjectFieldAs<View>("itemView")
                if (deviceIconColorInt != null) {
                    val deviceIcon = itemView.findViewByIdNameAs<ImageView>("device_icon")
                    deviceIcon.alpha = 1f
                    deviceIcon.colorFilter = PorterDuffColorFilter(deviceIconColorInt, PorterDuff.Mode.SRC_IN)
                }
                if (titleColorInt != null) {
                    val title = itemView.findViewByIdNameAs<TextView>("title")
                    title.setTextColor(titleColorInt)
                }
                if (artistColorInt != null) {
                    val artist = itemView.findViewByIdNameAs<TextView>("artist")
                    artist.setTextColor(artistColorInt)
                }
                if (emptyStateColorInt != null) {
                    val emptyState = itemView.findViewByIdNameAs<TextView>("empty_state")
                    emptyState.setTextColor(emptyStateColorInt)
                }
            }
            afterHookMethod("onConfigurationChanged", Int::class.java) { args, _ ->
                val itemView = thisObject.getObjectFieldAs<View>("itemView")
                if (configUtils.textAppearanceChanged(args[0])) {
                    if (titleColorInt != null) {
                        val title = itemView.findViewByIdNameAs<TextView>("title")
                        title.setTextColor(titleColorInt)
                    }
                    if (artistColorInt != null) {
                        val artist = itemView.findViewByIdNameAs<TextView>("artist")
                        artist.setTextColor(artistColorInt)
                    }
                    if (emptyStateColorInt != null) {
                        val emptyState = itemView.findViewByIdNameAs<TextView>("empty_state")
                        emptyState.setTextColor(emptyStateColorInt)
                    }
                }
            }
            afterHookMethod("disableMediaController") { _, _ ->
                val itemView = thisObject.getObjectFieldAs<View>("itemView")
                if (disabledIconColorInt != null) {
                    val prev = itemView.findViewByIdNameAs<ImageView>("prev")
                    val next = itemView.findViewByIdNameAs<ImageView>("next")
                    prev.colorFilter = PorterDuffColorFilter(disabledIconColorInt, PorterDuff.Mode.SRC_IN)
                    next.colorFilter = PorterDuffColorFilter(disabledIconColorInt, PorterDuff.Mode.SRC_IN)
                }
                if (enabledIconColorInt != null) {
                    val play = itemView.findViewByIdName("play") as ImageView
                    play.colorFilter = PorterDuffColorFilter(enabledIconColorInt, PorterDuff.Mode.SRC_IN)
                }
            }
            // PLACEHOLDER_MEDIA_CONT
            if (deviceIconColorInt != null) {
                afterHookMethod("updateIconsInfo", mediaPlayerIconsInfo, Boolean::class.java) { args, _ ->
                    val deviceRes = thisObject.getObjectField("deviceRes")
                    val iconsInfo = args[0]
                    val forceUpdate = args[1] as Boolean
                    val getDeviceRes = iconsInfo.callMethodAs<Int>("getDeviceRes")
                    if (deviceRes != getDeviceRes || forceUpdate) {
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
                    val prev = itemView.findViewByIdNameAs<ImageView>("prev")
                    val play = itemView.findViewByIdNameAs<ImageView>("play")
                    val next = itemView.findViewByIdNameAs<ImageView>("next")
                    prev.colorFilter = PorterDuffColorFilter(enabledIconColorInt, PorterDuff.Mode.SRC_IN)
                    play.colorFilter = PorterDuffColorFilter(enabledIconColorInt, PorterDuff.Mode.SRC_IN)
                    next.colorFilter = PorterDuffColorFilter(enabledIconColorInt, PorterDuff.Mode.SRC_IN)
                }
            }
        }
    }

    // PLACEHOLDER_CARD_TITLE

    private fun initCardTitleColors() {
        val disablePrimaryColorInt = XSPUtils.getString("card_primary_disabled_color", "null").parseColorInt()
        val enablePrimaryColorInt = XSPUtils.getString("card_primary_enabled_color", "null").parseColorInt()
        val restrictedPrimaryColorInt = XSPUtils.getString("card_primary_restricted_color", "null").parseColorInt()
        val unavailablePrimaryColorInt = XSPUtils.getString("card_primary_unavailable_color", "null").parseColorInt()
        val disableSecondaryColorInt = XSPUtils.getString("card_secondary_disabled_color", "null").parseColorInt()
        val enableSecondaryColorInt = XSPUtils.getString("card_secondary_enabled_color", "null").parseColorInt()
        val restrictedSecondaryColorInt = XSPUtils.getString("card_secondary_restricted_color", "null").parseColorInt()
        val unavailableSecondaryColorInt = XSPUtils.getString("card_secondary_unavailable_color", "null").parseColorInt()

        val qsItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSItemView", pluginClassLoader)
        val qsCardItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSCardItemView", pluginClassLoader)

        qsCardItemView.afterHookAllMethods("updateState") { _, _ ->
            (thisObject as LinearLayout).apply {
                val companion = qsItemView.getStaticObjectField("Companion")
                if (companion == null) {
                    logE("QSItemView Companion is null")
                    return@afterHookAllMethods
                }
                val sta = thisObject.getObjectField("state")
                val isRestricted = companion.callMethodAs<Boolean>("isRestrictedCompat", sta)
                val state = sta.getIntField("state")
                val title = this.findViewByIdNameAs<TextView>("title")
                val status = this.findViewByIdNameAs<TextView>("status")

                when (state) {
                    1 -> {
                        if (disablePrimaryColorInt != null) title.setTextColor(disablePrimaryColorInt)
                        if (disableSecondaryColorInt != null) status.setTextColor(disableSecondaryColorInt)
                    }
                    2 -> {
                        if (enablePrimaryColorInt != null) title.setTextColor(enablePrimaryColorInt)
                        if (enableSecondaryColorInt != null) status.setTextColor(enableSecondaryColorInt)
                    }
                    else -> {
                        if (isRestricted) {
                            if (restrictedPrimaryColorInt != null) title.setTextColor(restrictedPrimaryColorInt)
                            if (restrictedSecondaryColorInt != null) status.setTextColor(restrictedSecondaryColorInt)
                        } else {
                            if (unavailablePrimaryColorInt != null) title.setTextColor(unavailablePrimaryColorInt)
                            if (unavailableSecondaryColorInt != null) status.setTextColor(unavailableSecondaryColorInt)
                        }
                    }
                }
            }
        }
    }

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
}
