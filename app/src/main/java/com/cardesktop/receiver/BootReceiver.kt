package com.cardesktop.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 开机自启接收器
 * 确保桌面在开机后自动启动
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("CarDesktop", "Boot completed, launcher ready")
            // Launcher 不需要主动启动，系统会自动使用默认桌面
            // 如果需要主动启动某个服务，可以在这里添加
        }
    }
}
