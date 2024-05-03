package com.example.myapplication

import android.app.Application

class SmackLipApplication: Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}