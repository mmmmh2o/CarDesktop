package com.cardesktop.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
 * 设置页面
 */
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsScreen(onBack = { finish() })
        }
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp)
    ) {
        // 标题
        Text(
            text = "⚙️ 设置",
            color = TextPrimary,
            fontSize = Dimens.FontTime,
            fontWeight = FontWeight.Light
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 设置项
        SettingItem("🎨 主题颜色", "科技蓝")
        SettingItem("🕐 时钟样式", "数字")
        SettingItem("📱 网格列数", "5列")
        SettingItem("🖼️ 壁纸", "默认深色")
        SettingItem("🔊 声音反馈", "开启")
        SettingItem("📊 车速显示", "关闭（非车机）")
        SettingItem("ℹ️ 关于", "CarDesktop v1.0.0")
    }
}

@Composable
private fun SettingItem(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark.copy(alpha = 0.85f))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = TextPrimary, fontSize = Dimens.FontBody)
        Text(text = value, color = TextSecondary, fontSize = Dimens.FontCaption)
    }
    Spacer(modifier = Modifier.height(8.dp))
}
