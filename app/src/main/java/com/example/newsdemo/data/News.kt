package com.example.newsdemo.data

data class News (val title:String,
                 val description:String,
                 val content:String,
                 var readAlready:Boolean,
                 val urlToImage:String)