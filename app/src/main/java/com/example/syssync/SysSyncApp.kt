package com.example.syssync

import android.app.Application

class SysSyncApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        // جدولة أول فحص بعد 15 دقيقة من أول تشغيل
        // AlarmScheduler يتجاهل الطلب لو التنبيه مجدول بالفعل (FLAG_UPDATE_CURRENT)
        AlarmScheduler.schedule(this)
    }
}
