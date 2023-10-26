package com.example.pdfreader.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pdfreader.R
import com.example.pdfreader.data.Libs

class ChangeLanguageActivity : AppCompatActivity() {

    private var lang = ""
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_language)

        goBackHome()
        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.pdf)


    }

    fun change(view: View) {
        lang = when (view.id) {
            R.id.radioVietnam -> "vi"
            R.id.radioEnglish -> "en"
            else -> ""
        }


        Libs.changeLang(lang, this)
        Log.e("LANG", lang)

        // recreate screen
        recreate()
    }

    private fun goBackHome() {
        val buttonBack = findViewById<ImageView>(R.id.ivHomeBack)
        buttonBack.setOnClickListener {
            onBackPressed()
        }
    }



}