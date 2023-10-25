package com.example.pdfreader.activities

import android.app.Application
import com.google.gson.Gson

class App : Application() {

    companion object {
        private val sInstance: App by lazy {
            App()
        }

        fun self() = sInstance
    }

    private val gson = Gson()

    fun getGson() = gson
}
