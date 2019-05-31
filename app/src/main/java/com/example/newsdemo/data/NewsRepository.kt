package com.example.newsdemo.data

import com.example.newsdemo.data.network.NewsApiService
import io.reactivex.Observable

class NewsRepository (private val apiService:NewsApiService ) {

    fun getNews(page: Int): Observable<Response> {
        val queries = HashMap<String, String>()
        queries["country"] = "sg"
        queries["category"] = "business"
        queries["apiKey"] = "104d7bd77d0b46f2802fef857710e84f"
        queries["page"] = page.toString()
        return apiService.getNews(queries)
    }
}