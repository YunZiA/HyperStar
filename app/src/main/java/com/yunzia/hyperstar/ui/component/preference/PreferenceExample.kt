package com.yunzia.hyperstar.ui.component.preference

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.ui.component.preference.core.*
import com.yunzia.hyperstar.ui.navigation.Navigator

/**
 * 简单的 Preference 屏幕示例
 */
@Composable
fun PreferenceScreenSimpleExample() {
    PreferenceScreen(title = "设置") { _, _ ->
        preferenceGroup("基础设置") {
            Preference(
                title = "关于应用",
                summary = "查看应用信息",
//                icon = R.drawable.ic_info,
                onClick = {
                    // 处理点击事件
                }
            )

            var enableNotifications by remember { mutableStateOf(true) }
            SwitchPreference(
                title = "通知",
                summary = "接收应用通知",
//                icon = R.drawable.ic_notification,
                checked = enableNotifications,
                onCheckedChange = { isChecked ->
                    enableNotifications = isChecked
                    SPUtils.putBoolean("enable_notifications", isChecked)
                }
            )

            var selectedTheme by remember { mutableStateOf("浅色") }
            PreferenceWithValue(
                title = "主题",
                summary = "选择应用主题",
//                icon = R.drawable.ic_theme,
                value = selectedTheme,
                onClick = {
                    // 处理点击事件
                }
            )
        }

        preferenceGroup("高级设置") {
            var selectedLanguage by remember {
                mutableStateOf(SPUtils.getString("language", "zh"))
            }
            ListPreference(
                title = "语言",
                summary = "选择应用语言",
//                icon = R.drawable.ic_language,
                entries = listOf("中文", "English", "Español"),
                entryValues = listOf("zh", "en", "es"),
                value = selectedLanguage,
                onValueChange = { newValue ->
                    selectedLanguage = newValue
                    SPUtils.putString("language", newValue)
                }
            )

            var brightness by remember { mutableStateOf(50f) }
            SliderPreference(
                title = "亮度",
                summary = "调整屏幕亮度",
                value = brightness,
                valueRange = 0f..100f,
                steps = 10,
                valueFormatter = { value -> "${value.toInt()}%" },
                onValueChange = { newValue ->
                    brightness = newValue
                    SPUtils.putFloat("brightness", newValue)
                }
            )

            PreferenceWithContent(
                title = "自定义选项",
                summary = "包含自定义内容"
            ) {

            }
        }
    }
}

/**
 * 完整的 Preference 屏幕示例 - 包含顶部栏
 */
@Composable
fun PreferenceScreenFullExample(
    navController: Navigator,
) {
    PreferenceScreen(
        title = "设置",
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        endClick = {
            // 处理右侧按钮点击
        },
        endIcon = {

        }
    ) { scrollBehavior, padding ->
        preferenceGroup("显示") {
            var darkMode by remember { mutableStateOf(false) }
            SwitchPreference(
                title = "深色模式",
                summary = "使用深色主题",
                checked = darkMode,
                onCheckedChange = { isDark ->
                    darkMode = isDark
                    SPUtils.putBoolean("dark_mode", isDark)
                }
            )

            var textSize by remember { mutableStateOf(14f) }
            SliderPreference(
                title = "文字大小",
                summary = "调整应用中的文字大小",
                value = textSize,
                valueRange = 12f..20f,
                steps = 4,
                valueFormatter = { "${it.toInt()}sp" },
                onValueChange = { newSize ->
                    textSize = newSize
                    SPUtils.putFloat("text_size", newSize)
                }
            )
        }

        preferenceGroup("功能") {
            var autoBackup by remember { mutableStateOf(true) }
            SwitchPreference(
                title = "自动备份",
                summary = "自动备份应用数据",
                checked = autoBackup,
                onCheckedChange = { isEnabled ->
                    autoBackup = isEnabled
                    SPUtils.putBoolean("auto_backup", isEnabled)
                }
            )

            var backupFrequency by remember {
                mutableStateOf(SPUtils.getString("backup_frequency", "daily"))
            }
            ListPreference(
                title = "备份频率",
                summary = "设置备份频率",
                entries = listOf("每天", "每周", "每月", "手动"),
                entryValues = listOf("daily", "weekly", "monthly", "manual"),
                value = backupFrequency,
                onValueChange = { frequency ->
                    backupFrequency = frequency
                    SPUtils.putString("backup_frequency", frequency)
                }
            )
        }

        preferenceGroup("关于") {
            Preference(
                title = "版本信息",
                summary = "版本 1.0.0",
                onClick = {
                    // 查看详细版本信息
                }
            )

            Preference(
                title = "隐私政策",
                summary = "查看隐私政策",
                onClick = {
                    // 打开隐私政策链接
                }
            )

            Preference(
                title = "开源许可",
                summary = "查看开源许可",
                onClick = {
                    // 打开开源许可信息
                }
            )
        }
    }
}

/**
 * 禁用状态示例
 */
@Composable
fun PreferenceScreenDisabledExample() {
    PreferenceScreen(title = "实验功能") { _, _ ->
        preferenceGroup("实验功能") {
            Preference(
                title = "实验性功能",
                summary = "此功能已禁用",
                enabled = false,
                onClick = {
                    // 这个回调不会被触发
                }
            )

            SwitchPreference(
                title = "Beta 测试",
                summary = "参与 Beta 测试",
                checked = false,
                enabled = false
            )

            ListPreference(
                title = "实验选项",
                summary = "选择实验选项",
                entries = listOf("选项 A", "选项 B"),
                entryValues = listOf("a", "b"),
                value = "a",
                enabled = false
            )
        }
    }
}
