package com.example.syssync

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.syssync.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var audioMgr: AudioManager
    private var isNavigatingInternally = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        audioMgr = getSystemService(AUDIO_SERVICE) as AudioManager

        setupSlider(binding.seekAlarm,        binding.labelAlarm,        AudioManager.STREAM_ALARM,        "Alarm Volume")
        setupSlider(binding.seekMusic,        binding.labelMusic,        AudioManager.STREAM_MUSIC,        "Music Volume")
        setupSlider(binding.seekNotification, binding.labelNotification, AudioManager.STREAM_NOTIFICATION, "Notification Volume")
        setupSlider(binding.seekRinger,       binding.labelRinger,       AudioManager.STREAM_RING,         "Ringer Volume")
        setupSlider(binding.seekSystem,       binding.labelSystem,       AudioManager.STREAM_SYSTEM,       "System Volume")
        setupSlider(binding.seekVoice,        binding.labelVoice,        AudioManager.STREAM_VOICE_CALL,   "Voice Volume")

        binding.titleText.setOnLongClickListener {
            isNavigatingInternally = true
            startActivity(Intent(this, PinEntryActivity::class.java))
            true
        }

        binding.gearButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Audio Settings")
                .setMessage("Sound profile: Custom\nEqualizer: Flat\nEnhance bass: Off\nSurround sound: Off")
                .setPositiveButton("OK") { d, _ -> d.dismiss() }
                .show()
        }
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
        val max     = audioMgr.getStreamMaxVolume(stream)
        val current = audioMgr.getStreamVolume(stream)
        seekBar.max      = max
        seekBar.progress = current
        label.text       = "$name ($current/$max)"

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                label.text = "$name ($progress/$max)"
                if (fromUser) {
                    try { audioMgr.setStreamVolume(stream, progress, 0) } catch (_: Exception) {}
                }
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })
    }
}
