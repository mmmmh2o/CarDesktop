package com.cardesktop.data.repository

import android.content.Intent
import android.content.pm.PackageManager
import com.cardesktop.DesktopApp
import com.cardesktop.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 应用数据仓库 - 获取已安装应用列表
 */
object AppRepository {

    private val pm: PackageManager
        get() = DesktopApp.instance.packageManager

    /**
     * 获取所有可启动的应用
     */
    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))

        resolveInfos.map { info ->
            val appInfo = info.activityInfo.applicationInfo
            AppInfo(
                packageName = appInfo.packageName,
                label = info.loadLabel(pm).toString(),
                icon = info.loadIcon(pm),
                isSystemApp = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0,
                launchIntent = info.activityInfo.name
            )
        }.sortedBy { it.label.lowercase() }
    }

    /**
     * 获取用户安装的非系统应用
     */
    suspend fun getUserApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        getInstalledApps().filter { !it.isSystemApp }
    }

    /**
     * 搜索应用
     */
    suspend fun searchApps(query: String): List<AppInfo> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        getInstalledApps().filter {
            it.label.contains(query, ignoreCase = true) ||
                    it.packageName.contains(query, ignoreCase = true)
        }
    }
}
