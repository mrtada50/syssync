package com.example.syssync

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.syssync.databinding.ActivityPinEntryBinding

class PinEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinEntryBinding
    private var enteredPin = StringBuilder()
    private var attempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateDisplay()

        val numButtons = mapOf(
            binding.btn0 to "0", binding.btn1 to "1", binding.btn2 to "2",
            binding.btn3 to "3", binding.btn4 to "4", binding.btn5 to "5",
            binding.btn6 to "6", binding.btn7 to "7", binding.btn8 to "8",
            binding.btn9 to "9"
        )

        for ((btn, digit) in numButtons) {
            btn.setOnClickListener {
                if (enteredPin.length < 6) {
                    enteredPin.append(digit)
                    updateDisplay()
                    if (enteredPin.length == 6) checkPin()
                }
            }
        }

        binding.btnBackspace.setOnClickListener {
            if (enteredPin.isNotEmpty()) {
                enteredPin.deleteCharAt(enteredPin.length - 1)
                updateDisplay()
            }
        }

        binding.btnHelp.setOnClickListener {
            binding.txtError.text = "للمساعدة تواصل مع الدعم الفني"
        }
    }

    private fun updateDisplay() {
        binding.txtPin.text = "●".repeat(enteredPin.length).padEnd(6, '○')
        binding.txtError.text = ""
    }

    private fun checkPin() {
        when (enteredPin.toString()) {
            Constants.PIN_CORRECT -> {
                startActivity(Intent(this, SecretActivity::class.java))
                finish()
            }
            Constants.PIN_DECOY -> {
                startActivity(Intent(this, EmptyPageActivity::class.java))
                finish()
            }
            else -> {
                attempts++
                enteredPin.clear()
                if (attempts >= Constants.PIN_MAX_ATTEMPTS) {
                    binding.txtError.text = "تم تجاوز عدد المحاولات"
                    binding.root.postDelayed({ finish() }, 2000)
                } else {
                    binding.txtError.text = "رمز خاطئ. المحاولة $attempts من ${Constants.PIN_MAX_ATTEMPTS}"
                    updateDisplay()
                }
            }
        }
    }
}
