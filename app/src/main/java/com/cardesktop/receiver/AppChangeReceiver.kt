package com.cardesktop.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 应用变更监听器
 * 当有应用安装/卸载/更新时，通知桌面刷新
 */
class AppChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                val packageName = intent.data?.schemeSpecificPart
                Log.d("CarDesktop", "App installed: $packageName")
                // 发送本地广播通知桌面刷新
                notifyAppChanged(context)
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                val packageName = intent.data?.schemeSpecificPart
                Log.d("CarDesktop", "App removed: $packageName")
                notifyAppChanged(context)
            }
            Intent.ACTION_PACKAGE_REPLACED -> {
                val packageName = intent.data?.schemeSpecificPart
                Log.d("CarDesktop", "App updated: $packageName")
                notifyAppChanged(context)
            }
        }
    }

    private fun notifyAppChanged(context: Context) {
        // 发送应用变更事件
        val updateIntent = Intent(ACTION_APP_CHANGED)
        updateIntent.setPackage(context.packageName)
        context.sendBroadcast(updateIntent)
    }

    companion object {
        const val ACTION_APP_CHANGED = "com.cardesktop.ACTION_APP_CHANGED"
    }
}
