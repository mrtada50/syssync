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
    private lateinit var webViewTelegram: WebView
    private lateinit var webViewInstagram: WebView
    private lateinit var webViewX: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecretBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webViewTelegram = binding.webViewTelegram
        webViewInstagram = binding.webViewInstagram
        webViewX = binding.webViewX

        setupWebView(webViewTelegram, Constants.TELEGRAM_URL)
        setupWebView(webViewInstagram, Constants.INSTAGRAM_URL)
        setupWebView(webViewX, Constants.X_URL)

        // Show Telegram by default
        webViewTelegram.visibility = android.view.View.VISIBLE
        webViewInstagram.visibility = android.view.View.GONE
        webViewX.visibility = android.view.View.GONE

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Telegram"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Instagram"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("X"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                webViewTelegram.visibility = android.view.View.GONE
                webViewInstagram.visibility = android.view.View.GONE
                webViewX.visibility = android.view.View.GONE

                when (tab.position) {
                    0 -> webViewTelegram.visibility = android.view.View.VISIBLE
                    1 -> webViewInstagram.visibility = android.view.View.VISIBLE
                    2 -> webViewX.visibility = android.view.View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupWebView(webView: WebView, url: String) {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            userAgentString = "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36"
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            loadWithOverviewMode = true
            useWideViewPort = true
        }
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.webViewClient = WebViewClient()
        webView.loadUrl(url)
    }

    override fun onPause() {
        super.onPause()
        CookieManager.getInstance().flush()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        finishAndRemoveTask()
    }

    override fun onDestroy() {
        webViewTelegram.destroy()
        webViewInstagram.destroy()
        webViewX.destroy()
        super.onDestroy()
    }
}
