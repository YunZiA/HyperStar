package com.yunzia.hyperstar


object PagerList {

    //主页
    const val MAIN = "root"

    const val SYSTEMUI = "${MAIN}/systemui"

    const val HOME = "${MAIN}/home"

    const val SCREENSHOT = "${MAIN}/screenshot"

    const val BARRAGE = "${MAIN}/barrage"

    const val UPDATER = "${MAIN}/updater"

    const val CURRENTLOG = "${UPDATER}/currentlog"

    const val LOGHISTORY = "${UPDATER}/loghistory"

    const val THEMEMANAGER = "${MAIN}/thememanager"

    const val MMS = "${MAIN}/mms"

    const val LANGUAGE = "${MAIN}/language"

    const val GO_ROOT = "${MAIN}/go_root"
    //翻译
    const val TRANSLATOR = "${MAIN}/translator"
    //
    const val REFERENCES = "${MAIN}/references"
    //投喂
    const val DONATION = "${MAIN}/donation"
    //显示设置
    const val SHOW = "${MAIN}/show"

    const val MESSAGE = "${MAIN}/message"

    const val NOTDEVELOP = "${MAIN}/notdeveloper"
}

object SystemUIMoreList {

    const val POWERMENU = "${PagerList.SYSTEMUI}/powermenu"

    const val NOTIFICATIONOFIM = "${PagerList.SYSTEMUI}/notificationofim"

    const val NOTIFICATION_APP_DETAIL = "$NOTIFICATIONOFIM/notificationoappdetail"
}


object FunList {

    const val SELECT_LIST = "${SystemUIMoreList.POWERMENU}/selectList"
}

object ControlCenterList{
    //颜色编辑
    const val COLOR_EDIT = "${PagerList.SYSTEMUI}/colorEdit"
    //颜色编辑
    const val LAYOUT_ARRANGEMENT = "${PagerList.SYSTEMUI}/layoutArrangement"
    //妙播
    const val MEDIA = "${PagerList.SYSTEMUI}/media"
    //妙播应用选择
    const val MEDIA_APP = "${MEDIA}/mediaApp"
    //卡片磁贴列表
    const val CARD_LIST = "${PagerList.SYSTEMUI}/cardList"
    //普通磁贴布局
    const val TILE_LAYOUT = "${PagerList.SYSTEMUI}/tileLayout"

}


object CenterColorList {

    //卡片磁贴
    const val CARD_TILE = "${ControlCenterList.COLOR_EDIT}/cardTileColor"
    //滑条
    const val TOGGLE_SLIDER = "${ControlCenterList.COLOR_EDIT}/toggleSliderColor"
    //滑条
    const val DEVICE_CENTER = "${ControlCenterList.COLOR_EDIT}/deviceCenterColor"
    //普通磁贴
    const val LIST_COLOR = "${ControlCenterList.COLOR_EDIT}/listColor"
}