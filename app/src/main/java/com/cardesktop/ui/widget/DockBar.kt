package com.cardesktop.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cardesktop.data.model.AppInfo
import com.cardesktop.ui.theme.*

/**
 * 底部 Dock 栏
 */
@Composable
fun DockBar(
    dockApps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onAppDrawerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark.copy(alpha = 0.9f))
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧：应用抽屉按钮
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onAppDrawerClick)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "📱", fontSize = Dimens.FontBody)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "全部应用",
                color = Primary,
                fontSize = Dimens.FontSmall,
                fontWeight = FontWeight.Medium
            )
        }

        // 中间：固定应用
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            dockApps.forEach { app ->
                DockAppIcon(app = app, onClick = { onAppClick(app) })
            }
        }

        // 右侧：设置入口
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { /* 打开设置 */ }
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "⚙️", fontSize = Dimens.FontBody)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "设置",
                color = TextSecondary,
                fontSize = Dimens.FontSmall
            )
        }
    }
}
