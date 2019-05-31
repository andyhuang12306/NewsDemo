package com.example.newsdemo.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.newsdemo.BindableAdapter
import com.example.newsdemo.R
import com.example.newsdemo.data.News
import kotlinx.android.synthetic.main.news_recycler_item.view.*
import java.lang.Exception


class NewsViewAdapter(private var newsList: List<News>?, private var listener: View.OnClickListener) :
    RecyclerView.Adapter<NewsViewAdapter.RecyclerViewHolder>(), BindableAdapter<List<News>> {

    override fun setData(data: List<News>) {
        newsList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val viewHolder = RecyclerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.news_recycler_item, parent, false)
        )

        viewHolder.itemView.setOnClickListener {
            try{
                val news = newsList?.get(viewHolder.adapterPosition)
                it.tag = news
                listener?.onClick(it)
            }catch (e: Exception){

            }

        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bind(newsList?.get(position))
    }

    override fun getItemCount(): Int {
        return newsList?.size ?: 0
    }

    class RecyclerViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(news: News?) {
            view.textTitle.text = news?.title
            view.textDescription.text = news?.description
            if (news?.readAlready!!)
                view.textDescription.visibility = View.GONE
            else
                view.textDescription.visibility=View.VISIBLE
        }
    }
}