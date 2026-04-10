package com.cardesktop

import android.app.Application

class DesktopApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: DesktopApp
            private set
    }
}
