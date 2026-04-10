package com.cardesktop.data.model

import android.graphics.drawable.Drawable

/**
 * 已安装应用信息
 */
data class AppInfo(
    val packageName: String,
    val label: String,
    val icon: Drawable,
    val isSystemApp: Boolean = false,
    val launchIntent: String? = null
)

/**
 * 桌面快捷方式
 */
data class ShortcutItem(
    val id: String,
    val label: String,
    val iconRes: Int = 0,
    val packageName: String = "",
    val activityName: String = "",
    val isBuiltIn: Boolean = false
)

/**
 * 天气信息
 */
data class WeatherInfo(
    val temperature: Int = 0,
    val condition: String = "--",
    val icon: String = "☀️",
    val city: String = "--"
)

/**
 * 桌面布局配置
 */
data class DesktopConfig(
    val gridColumns: Int = 5,
    val gridRows: Int = 2,
    val showStatusBar: Boolean = true,
    val showDock: Boolean = true,
    val wallpaperIndex: Int = 0,
    val clockStyle: ClockStyle = ClockStyle.DIGITAL,
    val themeColor: Long = 0xFF00E5FF
)

enum class ClockStyle {
    DIGITAL, ANALOG
}
