package com.yunzia.hyperstar


object PagerList {

    //主页
    const val MAIN = "root"

    const val HOME = "${MAIN}/home"

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

object SystemUIList {
    //控制中心
    const val CONTROL_CENTER = "${PagerList.MAIN}/control_center"

    //音量条
    const val VOLUME_DIALOG = "${PagerList.MAIN}/volumeDialog"
    //系统界面更多
    const val MORE = "${PagerList.MAIN}/systemUI_more"
}


object SystemUIMoreList {

    const val POWERMENU = "${SystemUIList.MORE}/powermenu"

    const val NOTIFICATIONOFIM = "${SystemUIList.MORE}/notificationofim"
}


object FunList {

    const val SELECT_LIST = "${SystemUIMoreList.POWERMENU}/selectList"
}

object ControlCenterList{
    //控制中心
    const val CONTROL_CENTER = "${SystemUIList.CONTROL_CENTER}/controlCenter"
    //颜色编辑
    const val COLOR_EDIT = "${SystemUIList.CONTROL_CENTER}/colorEdit"
    //颜色编辑
    const val LAYOUT_ARRANGEMENT = "${SystemUIList.CONTROL_CENTER}/layoutArrangement"
    //妙播
    const val MEDIA = "${SystemUIList.CONTROL_CENTER}/media"
    //妙播应用选择
    const val MEDIA_APP = "${MEDIA}/mediaApp"
    //卡片磁贴列表
    const val CARD_LIST = "${SystemUIList.CONTROL_CENTER}/cardList"
    //普通磁贴布局
    const val TILE_LAYOUT = "${SystemUIList.CONTROL_CENTER}/tileLayout"

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