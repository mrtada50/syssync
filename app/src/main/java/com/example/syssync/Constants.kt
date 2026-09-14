package com.example.syssync

object Constants {

    const val PREFS_NAME = "messenger_notifier_prefs"
    const val KEY_LAST_TELEGRAM_COUNT = "last_telegram_count"
    const val KEY_LAST_INSTAGRAM_HAS_NEW = "last_instagram_has_new"
    const val KEY_LAST_CHECK_TIME = "last_check_time"
    const val KEY_AUTO_UPDATE_ENABLED = "auto_update_enabled"
    const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val KEY_DEBUG_TELEGRAM_RESULT = "debug_telegram_result"
    const val KEY_DEBUG_INSTAGRAM_RESULT = "debug_instagram_result"

    const val TELEGRAM_URL = "https://web.telegram.org/k/"
    const val INSTAGRAM_URL = "https://www.instagram.com/direct/inbox/"

    const val PIN_CORRECT   = "256480"
    const val PIN_DECOY     = "421199"
    const val PIN_MAX_ATTEMPTS = 3


    const val WORK_NAME_PERIODIC = "messenger_notifier_periodic_check"
    const val WORK_NAME_ONE_TIME = "messenger_notifier_manual_check"

    /**
     * NOTE: هذا الكود يعتمد على بنية صفحة تليغرام ويب الحالية (نسخة "K").
     * تليغرام يغيّر تصميم الموقع بين فترة وأخرى، فقد تحتاج هذه القراءة لتعديل
     * لاحقًا إذا توقفت عن العمل بعد تحديث بالموقع.
     *
     * الفكرة: بدل تخمين أي محادثة "شخصية" وأيها مجموعة/قناة، نعتمد على فولدر
     * (Chat Folder) مخصص باسم "Contacts" أنشأه المستخدم بنفسه بتليغرام،
     * ونقرأ فقط عداد غير المقروء الظاهر على تبويب هذا الفولدر في أعلى قائمة
     * المحادثات. هذا أدق وأثبت من محاولة تمييز نوع كل محادثة عن طريق الكلاسات.
     */
    const val TELEGRAM_FOLDER_NAME = "Contacts"

    val TELEGRAM_JS = """
        (function() {
            try {
                var target = '$TELEGRAM_FOLDER_NAME';
                var selectors = [
                    '.chatlist-filter',
                    '.tabs-tab',
                    '.menu-tab',
                    '[class*="filter"]',
                    '[class*="folder"]',
                    '[class*="Filter"]',
                    'li[class*="tab"]'
                ];
                for (var s = 0; s < selectors.length; s++) {
                    var els = document.querySelectorAll(selectors[s]);
                    for (var i = 0; i < els.length; i++) {
                        var text = (els[i].textContent || '').trim();
                        if (text.toLowerCase().indexOf(target.toLowerCase()) === -1) { continue; }
                        var re = new RegExp(target + '\\s*([1-9]\\d*)', 'i');
                        var match = text.match(re);
                        if (match) { return match[1]; }
                        return '0';
                    }
                }
                return 'ERROR:not_found';
            } catch (e) {
                return 'ERROR:' + e.message;
            }
        })();
    """

    /**
     * NOTE: نفس ملاحظة تليغرام تنطبق هنا - إنستغرام يغيّر واجهته باستمرار.
     * الفكرة: نبحث عن أيقونة الرسائل (Direct) ونتحقق هل فيها نقطة/عداد إشعار جديد.
     * النتيجة: "1" لو فيه رسالة جديدة، "0" لو ما فيه.
     */
    val INSTAGRAM_JS = """
        (function() {
            try {
                var title = document.title || '';
                var match = title.match(/^\((\d+)\)/);
                if (match) { return match[1]; }
                return '0';
            } catch(e) {
                return 'ERROR:' + e.message;
            }
        })();
    """
}
