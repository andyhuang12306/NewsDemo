package com.example.newsdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.newsdemo.news.NewsFragment
import com.example.newsdemo.news.NewsViewModel

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, NewsFragment.newInstance())
                .commitNow()
        }
    }

    fun obtainViewModel(): NewsViewModel {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance(application)).get(NewsViewModel::class.java)
    }
}
