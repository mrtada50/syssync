package com.example.syssync

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.syssync.databinding.ActivityUpdateSettingsBinding
import java.util.concurrent.TimeUnit

class UpdateSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

        binding.autoUpdateSwitch.isChecked = prefs.getBoolean(Constants.KEY_AUTO_UPDATE_ENABLED, true)
        binding.notificationsSwitch.isChecked = prefs.getBoolean(Constants.KEY_NOTIFICATIONS_ENABLED, true)

        binding.autoUpdateSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(Constants.KEY_AUTO_UPDATE_ENABLED, isChecked).apply()
            if (isChecked) {
                val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES).build()
                WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                    Constants.WORK_NAME_PERIODIC,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
            } else {
                WorkManager.getInstance(applicationContext).cancelUniqueWork(Constants.WORK_NAME_PERIODIC)
            }
        }

        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(Constants.KEY_NOTIFICATIONS_ENABLED, isChecked).apply()
        }
    }
}
