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
    data class CurrentLog(val currentAllLog: String) : MainRoutes

    @Parcelize
    @Serializable
    data object LogHistory : MainRoutes

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
