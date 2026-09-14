package com.example.syssync

import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.syssync.databinding.ActivitySecretBinding
import com.google.android.material.tabs.TabLayout

class SecretActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecretBinding
    private var currentTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecretBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWebView(binding.webViewTelegram,  Constants.TELEGRAM_URL)
        setupWebView(binding.webViewInstagram, Constants.INSTAGRAM_URL)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Telegram"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Instagram"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentTab = tab.position
                binding.webViewTelegram.visibility  = if (currentTab == 0) android.view.View.VISIBLE else android.view.View.GONE
                binding.webViewInstagram.visibility = if (currentTab == 1) android.view.View.VISIBLE else android.view.View.GONE
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupWebView(webView: WebView, url: String) {
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl(url)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        finishAndRemoveTask()
    }

    override fun onPause() {
        super.onPause()
        CookieManager.getInstance().flush()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val current = if (currentTab == 0) binding.webViewTelegram else binding.webViewInstagram
        if (current.canGoBack()) current.goBack()
        else super.onBackPressed()
    }
}
