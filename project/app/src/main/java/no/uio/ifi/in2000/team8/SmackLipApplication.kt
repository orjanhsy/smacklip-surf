package no.uio.ifi.in2000.team8

import android.app.Application

class SmackLipApplication: Application() {
    companion object {
        lateinit var container: AppContainer
    }

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

}