package com.example.newsdemo.data

data class Response (val status:String,
                     val totalResults:Int,
                     val articles:List<News>
                     )