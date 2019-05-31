package com.example.newsdemo

import android.app.Application
import com.example.myapplication.network.hasNetwork
import com.example.newsdemo.data.network.RetrofitServiceManager

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        val hasNetwork = hasNetwork(this)
        RetrofitServiceManager.setNetworkStatus(hasNetwork)
        RetrofitServiceManager.setCacheFile(this.cacheDir)
    }
}