package com.example.syssync

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object AlarmScheduler {

    private const val INTERVAL_MS = 15 * 60 * 1000L // 15 دقيقة

    private fun buildPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, CheckReceiver::class.java)
        return PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val pi = buildPendingIntent(context)
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + INTERVAL_MS,
            pi
        )
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.cancel(buildPendingIntent(context))
    }
}
