package com.cardesktop.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cardesktop.ui.theme.*

/**
 * 顶部状态栏
 */
@Composable
fun StatusBar(
    time: String,
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧：日期
        Text(
            text = date,
            color = TextSecondary,
            fontSize = Dimens.FontCaption
        )

        // 右侧：系统状态图标
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "📶", fontSize = Dimens.FontBody)
            Text(text = "📡", fontSize = Dimens.FontBody)
            Text(text = "🔋", fontSize = Dimens.FontBody)
            Text(
                text = time,
                color = TextPrimary,
                fontSize = Dimens.FontBody,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 时钟 Widget
 */
@Composable
fun ClockWidget(
    time: String,
    date: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark.copy(alpha = 0.85f))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = time,
            color = TextPrimary,
            fontSize = Dimens.FontTime,
            fontWeight = FontWeight.Light
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = date,
            color = TextSecondary,
            fontSize = Dimens.FontBody
        )
    }
}

/**
 * 天气 Widget
 */
@Composable
fun WeatherWidget(
    temperature: String = "26°",
    condition: String = "晴",
    city: String = "北京",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark.copy(alpha = 0.85f))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "☀️", fontSize = Dimens.FontTime)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = temperature,
            color = TextPrimary,
            fontSize = Dimens.FontTime,
            fontWeight = FontWeight.Light
        )
        Text(
            text = "$city · $condition",
            color = TextSecondary,
            fontSize = Dimens.FontCaption
        )
    }
}

/**
 * 快捷功能卡片
 */
@Composable
fun QuickActionCard(
    icon: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark.copy(alpha = 0.85f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = Dimens.FontTime)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = Dimens.FontCaption
        )
    }
}
