package com.yunzia.hyperstar

import android.annotation.SuppressLint
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yunzia.hyperstar.ui.base.navtype.PagersModel
import com.yunzia.hyperstar.ui.base.navtype.pagersJson
import com.yunzia.hyperstar.ui.module.betahome.BetaHomePager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.list.QSListColorPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.list.QsListViewPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.media.MediaSettingsPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.media.app.MediaAppSettingsPager
import com.yunzia.hyperstar.ui.module.systemui.other.SystemUIOtherPager
import com.yunzia.hyperstar.ui.module.systemui.volume.VolumePager
import com.yunzia.hyperstar.ui.pagers.TranslatorPager
import com.yunzia.hyperstar.ui.base.theme.HyperStarTheme
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.ControlCenterColorPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.ControlCenterListPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.ControlCenterPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.card.QSCardColorPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.card.QSCardListPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.devicecenter.DeviceCenterColorPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.slider.ToggleSliderColorsPager
import com.yunzia.hyperstar.ui.module.systemui.other.powermenu.PowerMenuStylePager
import com.yunzia.hyperstar.ui.module.systemui.other.powermenu.SelectFunPager
import com.yunzia.hyperstar.ui.pagers.DonationPage
import com.yunzia.hyperstar.ui.pagers.ReferencesPager
import com.yunzia.hyperstar.ui.pagers.UITest

@SuppressLint("RestrictedApi", "StateFlowValueCalledInComposition")
@Composable
fun App(
    activity: MainActivity?,
    colorMode: MutableState<Int>,
) {


    var pager = "null"

    val navController = rememberNavController()
    //val nav = remember
    val easing = CubicBezierEasing(0.12f, 0.38f, 0.2f, 1f)
    activity?.let {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = PagerList.MAIN,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                )
            },
            exitTransition = {

                slideOutHorizontally(
                    targetOffsetX = { -it / 5 },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 5 },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                )
            }
        ) {

            composable(PagerList.MAIN) { UITest(navController, activity,colorMode) }

            composable(SystemUIPagerList.CONTROL_CENTER) { ControlCenterPager(navController) };

            composable(PagerList.TRANSLATOR) { TranslatorPager(navController) }

            composable(PagerList.DONATION) { DonationPage(navController)  }

            composable(PagerList.REFERENCES) { ReferencesPager(navController)  }

            composable(PagerList.BETA_HOME) { BetaHomePager(navController) }

            composable(SystemUIPagerList.COLOR_EDIT) { ControlCenterColorPager(navController) }

            composable(SystemUIPagerList.LAYOUT_ARRANGEMENT) { ControlCenterListPager(navController) }

            composable(SystemUIPagerList.MEDIA) { MediaSettingsPager(navController) }

            composable(SystemUIPagerList.CARD_LIST) { QSCardListPager(navController) }

            composable(SystemUIPagerList.TILE_LAYOUT) { QsListViewPager(navController) }

            composable(CenterColorList.CARD_TILE) { QSCardColorPager(navController) }

            composable(CenterColorList.TOGGLE_SLIDER) { ToggleSliderColorsPager(navController) }

            composable(CenterColorList.DEVICE_CENTER) { DeviceCenterColorPager(navController) }

            composable(CenterColorList.LIST_COLOR) { QSListColorPager(navController) }

            composable(SystemUIPagerList.VOLUME_DIALOG) { VolumePager(navController) }

            composable(SystemUIPagerList.MORE) { SystemUIOtherPager(navController) }

            composable(SystemUIPagerList.MEDIA_APP) { MediaAppSettingsPager(navController) }

            composable(SystemUIPagerList.POWERMENU){ PowerMenuStylePager(navController) }


            composable(
                FunList.SELECT_LIST+"/{pagersJson}",
                listOf(
                    navArgument("pagersJson") {
                        type = pagersJson<PagersModel>()
                    }
                )
            ){ SelectFunPager(navController,it) }


        }


    }


}



object PagerList {

    //主页
    const val MAIN = "main"
    //系统界面更多
    const val BETA_HOME = "beta_home"
    //翻译
    const val TRANSLATOR = "translator"
    //
    const val REFERENCES = "references"
    //投喂
    const val DONATION = "donation"
}

object SystemUIPagerList {

    //控制中心
    const val CONTROL_CENTER = "controlCenter"
    //颜色编辑
    const val COLOR_EDIT = "colorEdit"
    //颜色编辑
    const val LAYOUT_ARRANGEMENT = "layoutArrangement"
    //妙播
    const val MEDIA = "media"
    //妙播应用选择
    const val MEDIA_APP = "mediaApp"
    //卡片磁贴列表
    const val CARD_LIST = "cardList"
    //普通磁贴布局
    const val TILE_LAYOUT = "tileLayout"
    //音量条
    const val VOLUME_DIALOG = "volumeDialog"
    //系统界面更多
    const val MORE = "more"

    const val POWERMENU = "powermenu"
}


object FunList {

    //控制中心
    const val SELECT_LIST = "selectList"
}


object CenterColorList {

    //卡片磁贴
    const val CARD_TILE = "cardTileColor"
    //滑条
    const val TOGGLE_SLIDER = "toggleSliderColor"
    //滑条
    const val DEVICE_CENTER = "deviceCenterColor"
    //普通磁贴
    const val LIST_COLOR = "listColor"
}