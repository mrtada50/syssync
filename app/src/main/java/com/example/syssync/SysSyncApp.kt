package com.example.syssync

import android.app.Application

class SysSyncApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        AlarmScheduler.schedule(this)
    }
}
