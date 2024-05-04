package com.example.myapplication

import android.app.Application

class SmackLipApplication: Application() {
    companion object {
        lateinit var container: AppContainer
    }

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

    override fun onTerminate() { // har denne en funksjon?
        super.onTerminate()
        container = DefaultAppContainer(this)
    }

}