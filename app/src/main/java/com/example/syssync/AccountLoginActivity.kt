package com.example.syssync

import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.syssync.databinding.ActivityLoginWebviewBinding

class AccountLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginWebviewBinding

    companion object {
        const val EXTRA_URL = "extra_url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra(EXTRA_URL) ?: Constants.TELEGRAM_URL

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.loginWebView, true)

        binding.loginWebView.settings.javaScriptEnabled = true
        binding.loginWebView.settings.domStorageEnabled = true
        binding.loginWebView.settings.databaseEnabled = true
        binding.loginWebView.webViewClient = WebViewClient()
        binding.loginWebView.loadUrl(url)
    }

    override fun onPause() {
        super.onPause()
        // نحفظ الكوكيز فورًا عشان يوركر الفحص بالخلفية يقدر يستخدم نفس الجلسة
        CookieManager.getInstance().flush()
    }
}
