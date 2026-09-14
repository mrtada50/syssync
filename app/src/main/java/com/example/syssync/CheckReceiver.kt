package com.example.syssync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class CheckReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()
        WorkManager.getInstance(context).enqueue(request)

        // أعد جدولة التنبيه التالي (15 دقيقة)
        AlarmScheduler.schedule(context)
    }
}
