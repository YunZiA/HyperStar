package com.yunzia.hyperstar.ui.navigation

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed interface Route : NavKey, Parcelable {
    val parent: NavKey
}

sealed interface MainRoutes : Route {

    @Parcelize
    @Serializable
    data object Key: Route {
        override val parent: NavKey get() = Key
    }

    override val parent: NavKey get() = Key

    @Parcelize
    @Serializable
    data object SystemUI : MainRoutes

    @Parcelize
    @Serializable
    data object Home : MainRoutes

    @Parcelize
    @Serializable
    data object Screenshot : MainRoutes

    @Parcelize
    @Serializable
    data object Barrage : MainRoutes

    @Parcelize
    @Serializable
    data object ThemeManager : MainRoutes

    @Parcelize
    @Serializable
    data object MMS : MainRoutes

    @Parcelize
    @Serializable
    data object NotDeveloper : MainRoutes

    @Parcelize
    @Serializable
    data object Language : MainRoutes

    @Parcelize
    @Serializable
    data object GoRoot : MainRoutes

    @Parcelize
    @Serializable
    data object Updater : MainRoutes

    @Parcelize
    @Serializable
    data class CurrentLog(val currentAllLog: String) : MainRoutes {
        override val parent: NavKey get() = Updater
    }

    @Parcelize
    @Serializable
    data object LogHistory : MainRoutes {
        override val parent: NavKey get() = Updater
    }

    @Parcelize
    @Serializable
    data object Translator : MainRoutes

    @Parcelize
    @Serializable
    data object Message : MainRoutes

    @Parcelize
    @Serializable
    data object References : MainRoutes

    @Parcelize
    @Serializable
    data object Donation : MainRoutes

    @Parcelize
    @Serializable
    data object Show : MainRoutes

    @Parcelize
    @Serializable
    data object PlaceHolder : MainRoutes
}


sealed interface SystemUIRoutes : Route {

    override val parent: NavKey get() = MainRoutes.SystemUI

    //颜色编辑
    @Parcelize
    @Serializable
    data object ColorEdit : SystemUIRoutes

    //控制中心布局
    @Parcelize
    @Serializable
    data object LayoutArrangement : SystemUIRoutes

    //妙播
    @Parcelize
    @Serializable
    data object Media : SystemUIRoutes

    //卡片磁贴列表
    @Parcelize
    @Serializable
    data object CardList : SystemUIRoutes

    //普通磁贴布局
    @Parcelize
    @Serializable
    data object TileLayout : SystemUIRoutes

    @Serializable
    @Parcelize
    data object PowerMenu : SystemUIRoutes

    @Parcelize
    @Serializable
    data object NotificationOfIm : SystemUIRoutes

    @Parcelize
    @Serializable
    data object NotificationImAppDetail : SystemUIRoutes
}


sealed interface PowerMenuRoutes : Route {

    override val parent: NavKey get() = SystemUIRoutes.PowerMenu

    @Parcelize
    @Serializable
    data class FunSelect(val index : Int, val key : String) : PowerMenuRoutes
}

sealed interface MediaRoutes : Route {

    override val parent: NavKey
        get() = SystemUIRoutes.Media

    //妙播应用选择
    @Parcelize
    @Serializable
    data object MediaApp : MediaRoutes
}



sealed interface ColorEditRoutes : Route {

    override val parent: NavKey get() = SystemUIRoutes.ColorEdit

    //卡片磁贴
    @Parcelize
    @Serializable
    data object CardTileColor : ColorEditRoutes

    //滑条
    @Serializable
    @Parcelize
    data object ToggleSliderColor : ColorEditRoutes

    @Parcelize
    @Serializable
    data object DeviceCenterColor : ColorEditRoutes

    //普通磁贴
    @Parcelize
    @Serializable
    data object ListColor : ColorEditRoutes
}

fun Route.displayName(): String = when (this) {
    MainRoutes.SystemUI -> "系统界面"
    MainRoutes.Home -> "首页"
    MainRoutes.Screenshot -> "截图"
    MainRoutes.Barrage -> "弹幕通知"
    MainRoutes.ThemeManager -> "主题壁纸"
    MainRoutes.MMS -> "短信"
    MainRoutes.NotDeveloper -> "非开发者"
    MainRoutes.Language -> "语言"
    MainRoutes.GoRoot -> "获取Root"
    MainRoutes.Updater -> "更新"
    MainRoutes.LogHistory -> "日志历史"
    MainRoutes.Translator -> "翻译人员"
    MainRoutes.Message -> "消息"
    MainRoutes.References -> "引用"
    MainRoutes.Donation -> "捐赠"
    MainRoutes.Show -> "显示"
    SystemUIRoutes.ColorEdit -> "颜色编辑"
    SystemUIRoutes.LayoutArrangement -> "控制中心布局"
    SystemUIRoutes.Media -> "妙播"
    SystemUIRoutes.CardList -> "卡片磁贴列表"
    SystemUIRoutes.TileLayout -> "磁贴布局"
    SystemUIRoutes.PowerMenu -> "电源菜单"
    SystemUIRoutes.NotificationOfIm -> "通知应用选择"
    SystemUIRoutes.NotificationImAppDetail -> "通知应用详情"
    ColorEditRoutes.CardTileColor -> "卡片磁贴颜色"
    ColorEditRoutes.ToggleSliderColor -> "亮度条&音量条颜色"
    ColorEditRoutes.DeviceCenterColor -> "融合设备中心改色"
    ColorEditRoutes.ListColor -> "普通磁贴颜色"
    MediaRoutes.MediaApp -> "默认播放应用选择"
    is PowerMenuRoutes.FunSelect -> "功能选择"
    is MainRoutes.CurrentLog -> "当前日志"
    MainRoutes.PlaceHolder -> "占位"
    MainRoutes.Key -> ""
    else -> this::class.simpleName ?: "未知"
}

/** Returns the top-level MainRoute ancestor (e.g., SystemUI, Home, Barrage). */
fun Route.topLevelRoute(): Route {
    var current: Route = this
    while (true) {
        val p = current.parent
        if (p is Route && p != current && p != MainRoutes.Key) {
            current = p
        } else {
            break
        }
    }
    return current
}

fun Route.path(): String {
    val parts = mutableListOf<String>()
    var current: Route = this
    while (true) {
        val name = current.displayName()
        if (name.isNotEmpty()) {
            parts.add(name)
        }
        val parent = current.parent
        if (parent is Route && parent != current) {
            current = parent
        } else {
            break
        }
    }
    return parts.reversed().joinToString(" → ")
}
