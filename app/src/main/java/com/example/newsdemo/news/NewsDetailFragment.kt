package com.example.newsdemo.news

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.newsdemo.R
import com.example.newsdemo.data.News

class NewsDetailFragment(private val news: News) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.news_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tv_go_back).setOnClickListener{
            val listFragment = this.activity?.supportFragmentManager?.findFragmentById(R.id.container)
            if (listFragment != null) {
                this.activity?.supportFragmentManager?.beginTransaction()
                    ?.remove(this)
                    ?.show(listFragment)
                    ?.commitNow()
            }
        }

        val img = view.findViewById<ImageView>(R.id.iv_img)
        val content = view.findViewById<TextView>(R.id.tv_content)
        content.text=news.content
    }

    companion object {
        fun newInstance(news: News) = NewsDetailFragment(news)
    }
}
