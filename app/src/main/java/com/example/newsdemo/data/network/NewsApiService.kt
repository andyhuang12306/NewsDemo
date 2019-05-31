package com.example.newsdemo.data.network

import com.example.newsdemo.data.Response
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.QueryMap


interface NewsApiService {

    @GET("/v2/top-headlines")
    fun getNews(@QueryMap queries: Map<String, String>): Observable<Response>

}