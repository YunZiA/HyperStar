package com.yunzia.hyperstar.hook.app.plugin.os1

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.yunzia.hyperstar.hook.base.BaseHookHelper.setColorField
import com.yunzia.hyperstar.hook.base.findViewByIdNameAs
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.colorReplaceByIdName
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.colorReplaceByValue
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.drawableReplaceByValue
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.intArrayReplaceByIdName
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.intArrayReplaceByValue
import com.yunzia.hyperstar.hook.core.helper.afterHookConstructor
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.beforeHookMethod
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.callMethodAs
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.core.helper.getStaticObjectField
import com.yunzia.hyperstar.hook.util.plugin.ConfigUtils
import com.yunzia.hyperstar.prefs.XSPUtils

private fun String?.parseColorInt(): Int? {
    return if (this != null && this != "null") this.toColorInt() else null
}

private fun colorFilter(color: Int) = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)

private fun replaceBlendColors(name: String, plugin: String, main: Int?, secondary: Int?) {
    if (main == null && secondary == null) return
    intArrayReplaceByValue(name, plugin) {
        if (main != null) this[0] = main
        if (secondary != null) this[2] = secondary
    }
}

object QSControlCenterColor : BasePluginHook() {

    override fun init() {
        startCardTitle()
        startCardIcon()
        startMediaColors()
        startToggleSliderIconColor()
        startListIconColor()
        startDeviceColor()
        startEditColor()
        startBackgroundColors()
        startCardColors()
        startToggleSliderColors()
        startListColors()
        startDeviceColors()
    }

    private fun startDeviceColors() {
        val detailIconColor = XSPUtils.getString("device_center_detail_icon_color", "null").parseColorInt()
        val itemBgColor = XSPUtils.getString("device_center_item_background_color", "null").parseColorInt()

        if (detailIconColor != null) {
            drawableReplaceByValue(plugin, "ic_device_center_detail_item") {
                colorFilter = colorFilter(detailIconColor)
            }
        }
        if (itemBgColor != null) {
            drawableReplaceByValue(plugin, "ic_device_center_item_background_default") {
                colorFilter = colorFilter(itemBgColor)
            }
        }
    }

    private fun startEditColor() {
        val editTitleColorInt = XSPUtils.getString("edit_title_color", "null").parseColorInt() ?: return

        val ConfigUtils = findClass("miui.systemui.controlcenter.ConfigUtils", pluginClassLoader)
        val EditButtonViewHolder = findClass(
            "miui.systemui.controlcenter.panel.main.qs.EditButtonController\$EditButtonViewHolder",
            pluginClassLoader
        )

        EditButtonViewHolder.apply {
            afterHookConstructor(View::class.java) { args, result ->
                val itemView = thisObject.getObjectFieldAs<View>("itemView")
                val text = itemView.findViewByIdNameAs<TextView>("text")
                text.setTextColor(editTitleColorInt)
            }

            afterHookMethod("onConfigurationChanged", Int::class.java) { args, result ->
                val INSTANCE = ConfigUtils.getStaticObjectField("INSTANCE")
                if (INSTANCE.callMethodAs<Boolean>("textAppearanceChanged", args[0])!!) {
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    val text = itemView.findViewByIdNameAs<TextView>("text")
                    text.setTextColor(editTitleColorInt)
                }
            }
        }
    }

    private fun startDeviceColor() {
        val iconColor = XSPUtils.getString("device_center_icon_color", "null").parseColorInt()
        val titleColor = XSPUtils.getString("device_center_title_color", "null").parseColorInt()
        val configUtils = ConfigUtils(pluginClassLoader)

        if (iconColor != null || titleColor != null) {
            val EmptyDeviceViewHolder = findClass(
                "miui.systemui.controlcenter.panel.main.devicecenter.devices.EmptyDeviceViewHolder",
                pluginClassLoader
            )

            EmptyDeviceViewHolder.apply {
                afterHookConstructor(View::class.java) { args, result ->
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    if (iconColor != null) {
                        itemView.findViewByIdNameAs<ImageView>("icon").colorFilter = colorFilter(iconColor)
                    }
                    if (titleColor != null) {
                        itemView.findViewByIdNameAs<TextView>("title").setTextColor(titleColor)
                    }
                }

                if (titleColor != null) {
                    afterHookMethod("onConfigurationChanged", Int::class.java) { args, result ->
                        if (configUtils.textAppearanceChanged(args[0])) {
                            val itemView = thisObject.getObjectFieldAs<View>("itemView")
                            itemView.findViewByIdNameAs<TextView>("title").setTextColor(titleColor)
                        }
                    }
                }
            }
        }

        val controlIconColor = XSPUtils.getString("device_control_icon_color", "null").parseColorInt()
        val controlTitleColor = XSPUtils.getString("device_control_title_color", "null").parseColorInt()

        if (controlIconColor != null || controlTitleColor != null) {
            val DeviceControlEntryViewHolder = findClass(
                "miui.systemui.controlcenter.panel.main.devicecontrol.DeviceControlsEntryController\$DeviceControlEntryViewHolder",
                pluginClassLoader
            )

            DeviceControlEntryViewHolder.apply {
                afterHookConstructor(View::class.java) { args, result ->
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    if (controlIconColor != null) {
                        itemView.findViewByIdNameAs<ImageView>("entry_icon").colorFilter = colorFilter(controlIconColor)
                    }
                    if (controlTitleColor != null) {
                        itemView.findViewByIdNameAs<TextView>("entry_title").setTextColor(controlTitleColor)
                    }
                }

                if (controlTitleColor != null) {
                    afterHookMethod("onConfigurationChanged", Int::class.java) { args, result ->
                        if (configUtils.textAppearanceChanged(args[0])) {
                            val itemView = thisObject.getObjectFieldAs<View>("itemView")
                            itemView.findViewByIdNameAs<TextView>("entry_title").setTextColor(controlTitleColor)
                        }
                    }
                }
            }
        }
    }

    private fun startToggleSliderColors() {
        val mainBlend = XSPUtils.getString("toggle_slider_progress_color_main", "null").parseColorInt()
        val secondaryBlend = XSPUtils.getString("toggle_slider_progress_color_secondary", "null").parseColorInt()
        val progressColor = XSPUtils.getString("toggle_slider_progress_color", "null")
        val valueColor = XSPUtils.getString("toggle_slider_value_color", "null")

        if (progressColor != "null") colorReplaceByValue("toggle_slider_progress_color", plugin, progressColor)
        if (valueColor != "null") colorReplaceByValue("toggle_slider_top_text_color", plugin, valueColor)

        replaceBlendColors("toggle_slider_progress_blend_colors", plugin, mainBlend, secondaryBlend)
    }

    private fun startBackgroundColors() {
        val backgroundColor = XSPUtils.getString("background_color", "null")
        val editBackgroundColor = XSPUtils.getString("edit_background_color", "null")
        val editBackgroundMode = XSPUtils.getInt("edit_background_mode", 0)

        val mainBgBlend = XSPUtils.getString("background_blend_color_main", "null").parseColorInt()
        val secondaryBgBlend = XSPUtils.getString("background_blend_color_secondary", "null").parseColorInt()
        val mainEditBlend = XSPUtils.getString("edit_background_blend_color_main", "null").parseColorInt()
        val secondaryEditBlend = XSPUtils.getString("edit_background_blend_color_secondary", "null").parseColorInt()

        if (backgroundColor != "null") {
            colorReplaceByValue("qs_card_disabled_color", plugin, backgroundColor)
            colorReplaceByValue("external_entry_background_color", plugin, backgroundColor)
            colorReplaceByValue("toggle_slider_progress_background_color", plugin, backgroundColor)
            colorReplaceByValue("qs_disabled_color", plugin, backgroundColor)
        }

        if (editBackgroundMode == 0) {
            if (editBackgroundColor != "null") {
                colorReplaceByValue("qs_customize_entry_button_background_color", plugin, editBackgroundColor)
            }
            replaceBlendColors("control_center_edit_button_blend_colors", plugin, mainEditBlend, secondaryEditBlend)
        } else {
            intArrayReplaceByIdName("control_center_edit_button_blend_colors", plugin, "control_center_list_items_blend_colors")
            colorReplaceByIdName("qs_customize_entry_button_background_color", plugin, "external_entry_background_color")
        }

        if (mainBgBlend != null || secondaryBgBlend != null) {
            replaceBlendColors("control_center_qs_items_blend_colors", plugin, mainBgBlend, secondaryBgBlend)
            replaceBlendColors("control_center_list_items_blend_colors", plugin, mainBgBlend, secondaryBgBlend)
        }
    }

    private fun startCardColors() {
        val enableColor = XSPUtils.getString("card_enabled_color", "null")
        val restrictedColor = XSPUtils.getString("card_restricted_color", "null")
        val unavailableColor = XSPUtils.getString("card_unavailable_color", "null")

        if (enableColor != "null") {
            colorReplaceByValue("qs_card_enabled_color", plugin, enableColor)
            colorReplaceByValue("qs_card_cellular_color", plugin, enableColor)
            colorReplaceByValue("qs_card_flashlight_color", plugin, enableColor)
        }
        if (restrictedColor != "null") colorReplaceByValue("qs_card_unavailable_color", plugin, restrictedColor)
        if (unavailableColor != "null") colorReplaceByValue("qs_card_disabled_color", plugin, unavailableColor)
    }

    private fun startListColors() {
        val enableColor = XSPUtils.getString("list_enabled_color", "null")
        val restrictedColor = XSPUtils.getString("list_restricted_color", "null")
        val warningColor = XSPUtils.getString("list_warning_color", "null")
        val unavailableColor = XSPUtils.getString("list_unavailable_color", "null")
        val tileColorForState = XSPUtils.getInt("qs_list_tile_color_for_state", 0)

        if (tileColorForState == 0) {
            val titleColor = XSPUtils.getString("list_title_color", "null")
            if (titleColor != "null") colorReplaceByValue("qs_text_disabled_color", plugin, titleColor)
        }

        if (enableColor != "null") colorReplaceByValue("qs_enabled_color", plugin, enableColor)
        if (warningColor != "null") colorReplaceByValue("qs_warning_color", plugin, warningColor)
        if (restrictedColor != "null") colorReplaceByValue("qs_restrict_color", plugin, restrictedColor)
        if (unavailableColor != "null") colorReplaceByValue("qs_unavailable_color", plugin, unavailableColor)
    }

    private fun startToggleSliderIconColor() {
        val iconColorInt = XSPUtils.getString("toggle_slider_icon_color", "null").parseColorInt() ?: return

        val filter = colorFilter(iconColorInt)

        findClass("miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController", pluginClassLoader)
            .afterHookConstructor("updateIconB") { args, result ->
                val itemView = thisObject.callMethod("getSliderHolder").getObjectFieldAs<View>("itemView")
                val drawable = itemView.findViewByIdNameAs<ImageView>("icon").drawable
                if (drawable is AnimatedVectorDrawable) drawable.colorFilter = filter
            }

        findClass("miui.systemui.controlcenter.panel.main.volume.VolumeSliderController", pluginClassLoader)
            .afterHookMethod("updateIconB") { args, result ->
                val itemView = thisObject.callMethod("getSlider").getObjectFieldAs<View>("itemView")
                val drawable = itemView.findViewByIdNameAs<ImageView>("icon").drawable
                if (drawable is AnimatedVectorDrawable) drawable.colorFilter = filter
            }
    }

    private fun startMediaColors() {
        val configUtils = ConfigUtils(pluginClassLoader)
        val titleColorInt = XSPUtils.getString("media_title_color", "null").parseColorInt()
        val artistColorInt = XSPUtils.getString("media_artist_color", "null").parseColorInt()
        val emptyStateColorInt = XSPUtils.getString("media_empty_state_color", "null").parseColorInt()
        val disabledIconColorInt = XSPUtils.getString("media_icon_color_disabled", "null").parseColorInt()
        val enabledIconColorInt = XSPUtils.getString("media_icon_color_enabled", "null").parseColorInt()
        val deviceIconColorInt = XSPUtils.getString("media_device_icon_color", "null").parseColorInt()

        val MediaPlayerIconsInfo = findClass("miui.systemui.controlcenter.media.MediaPlayerIconsInfo", pluginClassLoader)
        val MediaPlayerViewHolder = findClass(
            "miui.systemui.controlcenter.panel.main.media.MediaPlayerController\$MediaPlayerViewHolder",
            pluginClassLoader
        )

        MediaPlayerViewHolder.apply {
            afterHookConstructor(View::class.java) { args, result ->
                val itemView = thisObject.getObjectFieldAs<View>("itemView")
                if (deviceIconColorInt != null) {
                    val deviceIcon = itemView.findViewByIdNameAs<ImageView>("device_icon")
                    deviceIcon.alpha = 1f
                    deviceIcon.colorFilter = colorFilter(deviceIconColorInt)
                }
                if (titleColorInt != null) itemView.findViewByIdNameAs<TextView>("title").setTextColor(titleColorInt)
                if (artistColorInt != null) itemView.findViewByIdNameAs<TextView>("artist").setTextColor(artistColorInt)
                if (emptyStateColorInt != null) itemView.findViewByIdNameAs<TextView>("empty_state").setTextColor(emptyStateColorInt)
            }

            if (deviceIconColorInt != null) {
                afterHookMethod("updateIconsInfo", MediaPlayerIconsInfo, Boolean::class.java) { args, result ->
                    val deviceRes = thisObject.getObjectField("deviceRes")
                    val getDeviceRes = args[0].callMethodAs<Int>("getDeviceRes")
                    if (deviceRes != getDeviceRes || args[1] as Boolean) {
                        val deviceIcon = thisObject.getObjectFieldAs<View>("itemView")
                            .findViewByIdNameAs<ImageView>("device_icon")
                        deviceIcon.alpha = 1f
                        deviceIcon.colorFilter = colorFilter(deviceIconColorInt)
                    }
                }
            }

            afterHookMethod("onConfigurationChanged", Int::class.java) { args, result ->
                if (configUtils.textAppearanceChanged(args[0])) {
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    if (titleColorInt != null) itemView.findViewByIdNameAs<TextView>("title").setTextColor(titleColorInt)
                    if (artistColorInt != null) itemView.findViewByIdNameAs<TextView>("artist").setTextColor(artistColorInt)
                    if (emptyStateColorInt != null) itemView.findViewByIdNameAs<TextView>("empty_state").setTextColor(emptyStateColorInt)
                }
            }

            afterHookMethod("disableMediaController") { args, result ->
                val itemView = thisObject.getObjectFieldAs<View>("itemView")
                if (disabledIconColorInt != null) {
                    val filter = colorFilter(disabledIconColorInt)
                    itemView.findViewByIdNameAs<ImageView>("prev").colorFilter = filter
                    itemView.findViewByIdNameAs<ImageView>("next").colorFilter = filter
                }
                if (enabledIconColorInt != null) {
                    itemView.findViewByIdNameAs<ImageView>("play").colorFilter = colorFilter(enabledIconColorInt)
                }
            }

            if (enabledIconColorInt != null) {
                afterHookMethod("enableMediaController") { args, result ->
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    val filter = colorFilter(enabledIconColorInt)
                    itemView.findViewByIdNameAs<ImageView>("prev").colorFilter = filter
                    itemView.findViewByIdNameAs<ImageView>("play").colorFilter = filter
                    itemView.findViewByIdNameAs<ImageView>("next").colorFilter = filter
                }
            }
        }
    }

    private fun startCardTitle() {
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

        QSCardItemView.beforeHookMethod("updateBackground") { args, result ->
            (thisObject as LinearLayout).apply {
                val Companion = QSItemView.getStaticObjectField("Companion")
                val sta = thisObject.getObjectField("state")
                val states = Companion.callMethodAs<Boolean>("isRestrictedCompat", sta)!!
                val state = sta.getObjectField("state")
                val title = this.findViewByIdNameAs<TextView>("title")
                val status = this.findViewByIdNameAs<TextView>("status")

                when {
                    state == 0 -> {
                        if (disablePrimaryColorInt != null) title.setTextColor(disablePrimaryColorInt)
                        if (disableSecondaryColorInt != null) status.setTextColor(disableSecondaryColorInt)
                    }
                    state == 1 && states -> {
                        if (restrictedPrimaryColorInt != null) title.setTextColor(restrictedPrimaryColorInt)
                        if (restrictedSecondaryColorInt != null) status.setTextColor(restrictedSecondaryColorInt)
                    }
                    state == 2 -> {
                        if (enablePrimaryColorInt != null) title.setTextColor(enablePrimaryColorInt)
                        if (enableSecondaryColorInt != null) status.setTextColor(enableSecondaryColorInt)
                    }
                    else -> {
                        if (unavailablePrimaryColorInt != null) title.setTextColor(unavailablePrimaryColorInt)
                        if (unavailableSecondaryColorInt != null) status.setTextColor(unavailableSecondaryColorInt)
                    }
                }
            }
        }
    }

    private fun startListIconColor() {
        val offColor = XSPUtils.getString("list_icon_off_color", "null")
        val onColor = XSPUtils.getString("list_icon_on_color", "null")
        val restrictedColor = XSPUtils.getString("list_icon_restricted_color", "null")
        val unavailableColor = XSPUtils.getString("list_icon_unavailable_color", "null")

        findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemIconView", pluginClassLoader)
            .afterHookMethod("updateResources") { args, result ->
                if (onColor != "null") setColorField(thisObject, "iconColor", onColor)
                if (offColor != "null") setColorField(thisObject, "iconColorOff", offColor)
                if (restrictedColor != "null") setColorField(thisObject, "iconColorRestrict", restrictedColor)
                if (unavailableColor != "null") setColorField(thisObject, "iconColorUnavailable", unavailableColor)
            }
    }

    private fun startCardIcon() {
        val offColor = XSPUtils.getString("card_icon_off_color", "null")
        val onColor = XSPUtils.getString("card_icon_on_color", "null")
        val restrictedColor = XSPUtils.getString("card_icon_restricted_color", "null")
        val unavailableColor = XSPUtils.getString("card_icon_unavailable_color", "null")

        findClass("miui.systemui.controlcenter.qs.tileview.QSCardItemIconView", pluginClassLoader)
            .afterHookMethod("updateResources") { args, result ->
                if (onColor != "null") setColorField(thisObject, "iconColor", onColor)
                if (offColor != "null") setColorField(thisObject, "iconColorOff", offColor)
                if (restrictedColor != "null") setColorField(thisObject, "iconColorRestricted", restrictedColor)
                if (unavailableColor != "null") setColorField(thisObject, "iconColorUnavailable", unavailableColor)
            }
    }
}
