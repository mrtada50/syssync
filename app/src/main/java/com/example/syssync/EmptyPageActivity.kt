package com.example.syssync

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EmptyPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = FrameLayout(this).apply {
            setBackgroundColor(0xFF1E2124.toInt())
        }
        val text = TextView(this).apply {
            text = "الصفحة فارغة"
            setTextColor(0xFF888888.toInt())
            textSize = 18f
            gravity = Gravity.CENTER
        }
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
        layout.addView(text, params)
        setContentView(layout)
    }
}
