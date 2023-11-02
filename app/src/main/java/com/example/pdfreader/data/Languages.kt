package com.example.pdfreader.data

import android.app.Activity
import android.content.res.Configuration
import java.util.Locale

class Languages {

    companion object {
        private var myLocale: Locale? = null

        fun saveLocale(lang: String, activity: Activity) {
            val langPref = "Language"
            val prefs = activity.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE)
            prefs.edit().putString(langPref, lang).apply()
        }

        fun loadLocale(activity: Activity) {
            val langPref = "Language"
            val prefs = activity.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE)
            val language = prefs.getString(langPref, "")
            if (language != null) {
                changeLang(language, activity)
            }
        }

        fun changeLang(lang: String, activity: Activity) {
            if (lang.isBlank()) {
                return
            }

            myLocale = Locale(lang)
            saveLocale(lang, activity)
            Locale.setDefault(myLocale)

            val config = Configuration()
            config.locale = myLocale
            activity.resources.updateConfiguration(config, activity.resources.displayMetrics)
        }
    }
}
