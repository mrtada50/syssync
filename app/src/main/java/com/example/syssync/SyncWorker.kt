package com.example.syssync

import android.content.Context
import android.content.SharedPreferences
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.CookieManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun doWork(): Result = withContext(Dispatchers.Main) {
        try {
            checkTelegram()
            checkInstagram()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun checkTelegram() {
        val result = withTimeoutOrNull(45_000L) {
            loadAndEval(Constants.TELEGRAM_URL, Constants.TELEGRAM_JS, 12_000L)
        }

        prefs.edit().putString(Constants.KEY_DEBUG_TELEGRAM_RESULT, result ?: "timeout").apply()

        val telegramCount = result?.toIntOrNull() ?: return
        val lastCount = prefs.getInt(Constants.KEY_LAST_TELEGRAM_COUNT, 0)

        if (telegramCount > lastCount) {
            prefs.edit().putInt(Constants.KEY_LAST_TELEGRAM_COUNT, telegramCount).apply()
            NotificationHelper.sendNotification(
                applicationContext,
                1,
                "تنبيه صوتي",
                "تحقق من صوت المكالمات"
            )
        }
    }

    private suspend fun checkInstagram() {
        val result = withTimeoutOrNull(30_000L) {
            loadAndEval(Constants.INSTAGRAM_URL, Constants.INSTAGRAM_JS, 6_000L)
        }

        prefs.edit().putString(Constants.KEY_DEBUG_INSTAGRAM_RESULT, result ?: "timeout").apply()

        val instagramCount = result?.toIntOrNull() ?: return
        val lastCount = prefs.getInt(Constants.KEY_LAST_INSTAGRAM_COUNT, 0)

        if (instagramCount > lastCount) {
            prefs.edit().putInt(Constants.KEY_LAST_INSTAGRAM_COUNT, instagramCount).apply()
            NotificationHelper.sendNotification(
                applicationContext,
                2,
                "تنبيه صوتي",
                "تحقق من صوت المنبه"
            )
        }
    }

    private suspend fun loadAndEval(url: String, js: String, delayMs: Long): String? =
        suspendCancellableCoroutine { cont ->
            val webView = WebView(applicationContext)
            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
            }
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, pageUrl: String) {
                    view.postDelayed({
                        view.evaluateJavascript(js) { value ->
                            val clean = value?.trim()?.removeSurrounding("\"")
                            webView.destroy()
                            if (cont.isActive) cont.resume(clean)
                        }
                    }, delayMs)
                }
            }
            webView.loadUrl(url)

            cont.invokeOnCancellation { webView.destroy() }
        }
}
