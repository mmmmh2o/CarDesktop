package com.cardesktop.ui.widget

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.cardesktop.data.model.AppInfo
import com.cardesktop.ui.theme.*

/**
 * Drawable 转 Compose Painter
 */
@Composable
fun drawablePainter(drawable: Drawable): Painter {
    val density = LocalDensity.current
    val bitmap = remember(drawable) {
        val size = with(density) { 64.dp.roundToPx() }
        drawable.toBitmap(width = size, height = size)
    }
    return androidx.compose.ui.graphics.painter.BitmapPainter(bitmap.asImageBitmap())
}

/**
 * 桌面应用图标 - 大尺寸
 */
@Composable
fun DesktopAppIcon(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 64.dp
) {
    Column(
        modifier = modifier
            .width(iconSize + 32.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = drawablePainter(app.icon),
            contentDescription = app.label,
            modifier = Modifier.size(iconSize)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = app.label,
            color = TextPrimary,
            fontSize = Dimens.FontCaption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 底栏 Dock 图标 - 较小尺寸
 */
@Composable
fun DockAppIcon(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = drawablePainter(app.icon),
            contentDescription = app.label,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = app.label,
            color = TextSecondary,
            fontSize = Dimens.FontSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
