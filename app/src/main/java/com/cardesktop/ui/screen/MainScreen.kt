package com.cardesktop.ui.screen

import android.content.ComponentName
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cardesktop.data.model.AppInfo
import com.cardesktop.ui.theme.*
import com.cardesktop.ui.widget.*

/**
 * 主桌面屏幕
 *
 * 布局结构：
 * ┌──────────────────────────────────────┐
 * │  状态栏 (日期 + 时间 + 系统图标)      │
 * ├────────┬─────────────────┬───────────┤
 * │        │                 │           │
 * │ 时钟   │   应用网格       │  天气     │
 * │ Widget │                 │  Widget   │
 * │        │                 │           │
 * ├────────┴─────────────────┴───────────┤
 * │  快捷操作栏 (导航/音乐/电话/相机)     │
 * ├──────────────────────────────────────┤
 * │  Dock 栏 (全部应用 ... 固定应用 设置) │
 * └──────────────────────────────────────┘
 */
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val time by viewModel.currentTime.collectAsState()
    val date by viewModel.currentDate.collectAsState()
    val apps by viewModel.apps.collectAsState()
    val shortcuts by viewModel.shortcuts.collectAsState()
    val dockApps by viewModel.dockApps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    fun launchApp(app: AppInfo) {
        val intent = app.launchIntent?.let {
            Intent().apply {
                component = ComponentName(app.packageName, it)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } ?: context.packageManager.getLaunchIntentForPackage(app.packageName)

        intent?.let { context.startActivity(it) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ========== 1. 顶部状态栏 ==========
            StatusBar(time = time, date = date)

            // ========== 2. 主内容区 ==========
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 左侧：时钟
                ClockWidget(
                    time = time,
                    date = date,
                    modifier = Modifier
                        .width(200.dp)
                        .fillMaxHeight()
                )

                // 中间：应用网格
                if (!isLoading && shortcuts.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(shortcuts, key = { it.packageName }) { app ->
                            DesktopAppIcon(
                                app = app,
                                onClick = { launchApp(app) },
                                iconSize = 64.dp
                            )
                        }
                    }
                }

                // 右侧：天气
                WeatherWidget(
                    modifier = Modifier
                        .width(200.dp)
                        .fillMaxHeight()
                )
            }

            // ========== 3. 快捷操作栏 ==========
            QuickActionBar()

            // ========== 4. 底部 Dock 栏 ==========
            DockBar(
                dockApps = dockApps,
                onAppClick = ::launchApp,
                onAppDrawerClick = {
                    context.startActivity(
                        Intent(context, AppDrawerActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            )
        }
    }
}

/**
 * 快捷操作栏
 */
@Composable
private fun QuickActionBar() {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickActionCard(
            icon = "🗺️",
            label = "导航",
            onClick = {
                // 尝试打开高德/百度地图
                val intent = context.packageManager
                    .getLaunchIntentForPackage("com.autonavi.minimap")
                    ?: context.packageManager.getLaunchIntentForPackage("com.baidu.BaiduMap")
                intent?.let { context.startActivity(it) }
            },
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        )
        QuickActionCard(
            icon = "🎵",
            label = "音乐",
            onClick = { /* 打开音乐 */ },
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        )
        QuickActionCard(
            icon = "📞",
            label = "电话",
            onClick = { /* 打开电话 */ },
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        )
        QuickActionCard(
            icon = "📷",
            label = "相机",
            onClick = {
                val intent = context.packageManager
                    .getLaunchIntentForPackage("com.android.camera2")
                intent?.let { context.startActivity(it) }
            },
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        )
    }
}
