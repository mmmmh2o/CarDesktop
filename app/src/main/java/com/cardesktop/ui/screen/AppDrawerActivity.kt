package com.cardesktop.ui.screen

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.cardesktop.data.model.AppInfo
import com.cardesktop.data.repository.AppRepository
import com.cardesktop.ui.theme.*
import com.cardesktop.ui.widget.DesktopAppIcon
import kotlinx.coroutines.launch

/**
 * 应用抽屉 - 显示所有已安装应用
 */
class AppDrawerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var allApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
            var searchQuery by remember { mutableStateOf("") }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                allApps = AppRepository.getInstalledApps()
                isLoading = false
            }

            val filteredApps = remember(allApps, searchQuery) {
                if (searchQuery.isBlank()) allApps
                else allApps.filter { it.label.contains(searchQuery, ignoreCase = true) }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark)
                    .padding(24.dp)
            ) {
                // 标题 + 搜索
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "全部应用 (${allApps.size})",
                        color = TextPrimary,
                        fontSize = Dimens.FontTitle,
                        fontWeight = FontWeight.Bold
                    )

                    // 返回按钮
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { finish() }
                            .background(SurfaceDark)
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(text = "✕ 返回", color = TextSecondary, fontSize = Dimens.FontBody)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 搜索栏
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("搜索应用...", color = TextHint) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 应用网格
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "加载中...", color = TextSecondary)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 100.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(filteredApps, key = { it.packageName }) { app ->
                            DesktopAppIcon(
                                app = app,
                                onClick = {
                                    val intent = app.launchIntent?.let {
                                        Intent().apply {
                                            component = ComponentName(app.packageName, it)
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                    } ?: packageManager.getLaunchIntentForPackage(app.packageName)
                                    intent?.let { startActivity(it) }
                                    finish()
                                },
                                iconSize = 56.dp
                            )
                        }
                    }
                }
            }
        }
    }
}
