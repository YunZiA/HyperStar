package com.chaos.hyperstar.ui.module.controlcenter.list

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.chaos.hyperstar.ui.base.BaseActivity
import com.chaos.hyperstar.ui.module.controlcenter.list.ui.QsListViewPager
import com.chaos.hyperstar.ui.module.controlcenter.media.MediaSettingsPager
import com.chaos.hyperstar.ui.module.controlcenter.ui.ControlCenterPager
import com.chaos.hyperstar.ui.module.ui.theme.HyperStarTheme
import com.chaos.hyperstar.utils.PreferencesUtil

class QsListViewSettings : BaseActivity() {
    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        QsListViewPager(this)
    }

    override fun initData() {

    }


}
