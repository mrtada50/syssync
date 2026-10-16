package com.example.syssync

object Constants {
    const val PIN_CORRECT = "256480"
    const val PIN_DECOY = "421199"
    const val PIN_MAX_ATTEMPTS = 3

    const val TELEGRAM_URL = "https://web.telegram.org/k/"
    const val INSTAGRAM_URL = "https://www.instagram.com/direct/inbox/"
    const val X_URL = "https://x.com/messages"

    const val TELEGRAM_FOLDER_NAME = "Contacts"

    val TELEGRAM_JS = """
        (function() {
            try {
                var tabs = document.querySelectorAll('.chatlist-filter');
                for (var i = 0; i < tabs.length; i++) {
                    var text = tabs[i].innerText || '';
                    var match = text.match(/Contacts\s*([1-9]\d*)/);
                    if (match) return match[1];
                }
                return '0';
            } catch(e) { return 'ERROR:' + e.message; }
        })()
    """.trimIndent()

    val INSTAGRAM_JS = """
        (function() {
            try {
                var title = document.title;
                var match = title.match(/\((\d+)\)/);
                if (match) return match[1];
                return '0';
            } catch(e) { return 'ERROR:' + e.message; }
        })()
    """.trimIndent()

    const val KEY_DEBUG_TELEGRAM_RESULT = "debug_telegram_result"
    const val KEY_DEBUG_INSTAGRAM_RESULT = "debug_instagram_result"

    const val PREFS_NAME = "syssync_prefs"
    const val KEY_LAST_TELEGRAM_COUNT = "last_telegram_count"
    const val KEY_LAST_INSTAGRAM_COUNT = "last_instagram_count"
}
