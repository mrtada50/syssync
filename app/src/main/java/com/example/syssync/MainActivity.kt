package com.example.syssync

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.syssync.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var audioMgr: AudioManager
    private var isNavigatingInternally = false

    private val notifPermLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted or denied, continue */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        audioMgr = getSystemService(AUDIO_SERVICE) as AudioManager

        setupSlider(binding.seekAlarm,        binding.lblAlarm,        AudioManager.STREAM_ALARM,        "Alarm")
        setupSlider(binding.seekMusic,        binding.lblMusic,        AudioManager.STREAM_MUSIC,        "Music")
        setupSlider(binding.seekNotification, binding.lblNotification, AudioManager.STREAM_NOTIFICATION, "Notification")
        setupSlider(binding.seekRing,         binding.lblRing,         AudioManager.STREAM_RING,         "Ringer")
        setupSlider(binding.seekSystem,       binding.lblSystem,       AudioManager.STREAM_SYSTEM,       "System")
        setupSlider(binding.seekVoice,        binding.lblVoice,        AudioManager.STREAM_VOICE_CALL,   "Voice Call")

        binding.titleText.setOnLongClickListener {
            isNavigatingInternally = true
            startActivity(Intent(this, PinEntryActivity::class.java))
            true
        }

        binding.btnSettings.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Audio Settings")
                .setMessage("All audio streams are managed automatically by the system.")
                .setPositiveButton("OK", null)
                .show()
        }

        requestNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        isNavigatingInternally = false
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (!isNavigatingInternally) {
            finishAndRemoveTask()
        }
    }

    private fun setupSlider(seekBar: SeekBar, label: TextView, stream: Int, name: String) {
        val max = audioMgr.getStreamMaxVolume(stream)
        val cur = audioMgr.getStreamVolume(stream)
        seekBar.max = max
        seekBar.progress = cur
        label.text = "$name: $cur"

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    audioMgr.setStreamVolume(stream, progress, 0)
                    label.text = "$name: $progress"
                }
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notifPermLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
