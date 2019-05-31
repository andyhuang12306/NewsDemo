package com.example.newsdemo.data.network

import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import java.io.File
import java.util.concurrent.TimeUnit


class RetrofitServiceManager private constructor() {
    private val mRetrofit: Retrofit


    init {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
        builder.readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
        if (cachPath!!.isFile) {
            builder.cache(Cache(cachPath!!, CACHESIZE))
        }
        builder.addNetworkInterceptor { chain ->
            val originalResponse = chain.proceed(chain.request())
            val cacheControl = originalResponse.header("Cache-Control")
            if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")
            ) {
                originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + 5000)
                    .build()
            } else {
                originalResponse
            }
        }
        builder.addInterceptor { chain ->
            var request = chain.request()
            if ((!netWorkConnected!!)!!) {
                request = request.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached")
                    .build()
            }
            chain.proceed(request)
        }
        val client = builder.build()

        mRetrofit = Retrofit.Builder()
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    private object SingletonHolder {
        internal val INSTANCE = RetrofitServiceManager()
    }

    fun <T> create(service: Class<T>): T {
        return mRetrofit.create(service)
    }

    companion object {

        private val TIME_OUT = 5
        private val BASE_URL = "https://newsapi.org/v2/"
        private val CACHESIZE = (1024 * 1024 * 10).toLong()
        var cachPath: File? = null
        private var netWorkConnected: Boolean? = false

        fun setNetworkStatus(hasNetwork: Boolean?) {
            netWorkConnected = hasNetwork
        }

        val networkStatus: Boolean
            get() = netWorkConnected!!

        fun setCacheFile(path: File?) {
            cachPath = path
        }

        val cachePath: File
            get() = cachePath

        val instance: RetrofitServiceManager
            get() = SingletonHolder.INSTANCE
    }
}
