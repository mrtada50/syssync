package com.example.syssync

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        NotificationHelper.createChannel(applicationContext)

        val telegramResult = withTimeoutOrNull(45_000) {
            checkSite(Constants.TELEGRAM_URL, Constants.TELEGRAM_JS)
        }
        val instagramResult = withTimeoutOrNull(30_000) {
            checkSite(Constants.INSTAGRAM_URL, Constants.INSTAGRAM_JS)
        }

        val prefs = applicationContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean(Constants.KEY_NOTIFICATIONS_ENABLED, true)

        // حفظ النتيجة الخام للتشخيص
        prefs.edit()
            .putString(Constants.KEY_DEBUG_TELEGRAM_RESULT, telegramResult ?: "timeout/null")
            .putString(Constants.KEY_DEBUG_INSTAGRAM_RESULT, instagramResult ?: "timeout/null")
            .apply()

        // Telegram: compare private-chat unread count, notify if it increased
        val telegramCount = telegramResult?.toIntOrNull()
        if (telegramCount != null) {
            val lastCount = prefs.getInt(Constants.KEY_LAST_TELEGRAM_COUNT, 0)
            if (telegramCount > lastCount && notificationsEnabled) {
                NotificationHelper.notify(
                    applicationContext,
                    1001,
                    "تنبيه صوتي",
                    "تحقق من صوت المكالمات"
                )
            }
            prefs.edit().putInt(Constants.KEY_LAST_TELEGRAM_COUNT, telegramCount).apply()
        }

        // Instagram: نفس منطق تليغرام — رقم من عنوان الصفحة، إشعار لو زاد
        val instagramCount = instagramResult?.toIntOrNull()
        if (instagramCount != null) {
            val lastCount = prefs.getInt(Constants.KEY_LAST_INSTAGRAM_HAS_NEW, 0)
            if (instagramCount > lastCount && notificationsEnabled) {
                NotificationHelper.notify(
                    applicationContext,
                    1002,
                    "تنبيه صوتي",
                    "تحقق من صوت المنبه"
                )
            }
            prefs.edit().putInt(Constants.KEY_LAST_INSTAGRAM_HAS_NEW, instagramCount).apply()
        }

        prefs.edit().putLong(Constants.KEY_LAST_CHECK_TIME, System.currentTimeMillis()).apply()

        return Result.success()
    }

    /**
     * يفتح الصفحة داخل WebView مخفي (على الـ main thread)، ينتظر تحميلها،
     * ثم ينفّذ كود JavaScript المرسل ويرجع نتيجته كنص.
     */
    private suspend fun checkSite(url: String, js: String): String = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { cont ->
            val webView = WebView(applicationContext)
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.settings.databaseEnabled = true

            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, finishedUrl: String) {
                    // تليغرام ويب SPA ثقيلة وتحتاج وقت أطول عشان تظهر تبويبات الفولدر
                    val delay = if (url.contains("telegram")) 12_000L else 6_000L
                    Handler(Looper.getMainLooper()).postDelayed({
                        view.evaluateJavascript(js) { result ->
                            val cleaned = result?.trim('"') ?: "null"
                            if (cont.isActive) cont.resume(cleaned)
                            view.destroy()
                        }
                    }, delay)
                }
            }

            cont.invokeOnCancellation {
                webView.destroy()
            }

            webView.loadUrl(url)
        }
    }
}
