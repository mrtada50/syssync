package com.example.syssync

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.syssync.databinding.ActivityPinEntryBinding

class PinEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinEntryBinding
    private var enteredPin = ""
    private var remainingAttempts = Constants.PIN_MAX_ATTEMPTS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val digits = listOf(
            binding.btn0 to "0", binding.btn1 to "1", binding.btn2 to "2",
            binding.btn3 to "3", binding.btn4 to "4", binding.btn5 to "5",
            binding.btn6 to "6", binding.btn7 to "7", binding.btn8 to "8",
            binding.btn9 to "9"
        )
        digits.forEach { (btn, d) -> btn.setOnClickListener { onDigit(d) } }

        binding.btnBackspace.setOnClickListener {
            if (enteredPin.isNotEmpty()) {
                enteredPin = enteredPin.dropLast(1)
                updateDisplay()
            }
        }

        binding.btnHelp.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Help")
                .setMessage("Enter your device PIN to access restricted audio profiles.\n\nFor support: support@audiomgr.app")
                .setPositiveButton("OK") { d, _ -> d.dismiss() }
                .show()
        }
    }

    private fun onDigit(d: String) {
        if (enteredPin.length >= 6) return
        enteredPin += d
        updateDisplay()
        if (enteredPin.length == 6) checkPin()
    }

    private fun updateDisplay() {
        binding.pinDisplay.text = if (enteredPin.isEmpty()) "Enter pin code"
        else "●  ".repeat(enteredPin.length).trim()
    }

    private fun checkPin() {
        when (enteredPin) {
            Constants.PIN_CORRECT -> {
                startActivity(Intent(this, SecretActivity::class.java))
                finish()
            }
            Constants.PIN_DECOY -> {
                enteredPin = ""
                updateDisplay()
                binding.errorText.visibility = View.GONE
                startActivity(Intent(this, EmptyPageActivity::class.java))
            }
            else -> {
                remainingAttempts--
                enteredPin = ""
                updateDisplay()
                if (remainingAttempts <= 0) {
                    AlertDialog.Builder(this)
                        .setMessage("تم تجاوز عدد المحاولات المسموح بها")
                        .setPositiveButton("حسناً") { _, _ -> finish() }
                        .setCancelable(false)
                        .show()
                } else {
                    binding.errorText.visibility = View.VISIBLE
                    binding.errorText.text = "رمز الهاتف غلط، أعد الإدخال\n($remainingAttempts محاولات متبقية)"
                }
            }
        }
    }
}
