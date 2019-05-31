package com.example.newsdemo.news

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.newsdemo.R
import com.example.newsdemo.data.News
import com.example.newsdemo.data.NewsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _items = MutableLiveData<List<News>>().apply { value = emptyList() }
    val items: LiveData<List<News>>
        get() = _items

    private val _readNews = MutableLiveData<List<News>>().apply { value = emptyList() }
    val readNews: LiveData<List<News>>
        get() = _readNews

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int>
        get() = _currentFilteringLabel

    private val _isCurrentFilteringAll = MutableLiveData<Boolean>()
    val isCurrentFilteringAll: MutableLiveData<Boolean>
        get() = _isCurrentFilteringAll

    private val _noNewsLabel = MutableLiveData<Int>()
    val noNewsLabel: LiveData<Int>
        get() = _noNewsLabel

    private var _currentFiltering = NewsFilterType.ALL_NEWS

    private val isDataLoadingError = MutableLiveData<Boolean>()

    val emptyAll: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }
    val emptyReadAlready: LiveData<Boolean> = Transformations.map(_readNews) {
        it.isEmpty()
    }

    init {
        setFiltering(NewsFilterType.ALL_NEWS)
    }

    fun start() {
        loadAllNews(true, 1)
    }

    fun setFiltering(requestType: NewsFilterType) {
        _currentFiltering = requestType
        when (requestType) {
            NewsFilterType.ALL_NEWS -> {
                setFilter(R.string.news_list_label_all, R.string.news_list_empty_note)
                _isCurrentFilteringAll.value = true
            }
            NewsFilterType.READY_ALREADY_NEWS -> {
                setFilter(R.string.news_list_label_read, R.string.news_list_empty_note)
                _isCurrentFilteringAll.value = false
            }
        }
    }

    private fun setFilter(@StringRes filteringLabelString: Int, @StringRes noNewsLabelString: Int) {
        _currentFilteringLabel.value = filteringLabelString
        _noNewsLabel.value = noNewsLabelString
    }


    @SuppressLint("CheckResult")
    fun loadAllNews(showLoadingUI: Boolean, page: Int) {
        if (showLoadingUI) {
            _dataLoading.value = true
        }
        newsRepository.getNews(page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data ->
                _dataLoading.value = false
                if (data.status == "ok" && data.articles.isNotEmpty()) {
                    if (page == 1)
                        _items.value = data.articles
                    else{
                        val list = _items.value
                        val resultList = list?.plus(data.articles)
                        _items.value=resultList
                    }
                } else {
                    if (page == 1)
                        _items.value = emptyList()
                    else{}
                }
            }, { error ->
                _dataLoading.value = false
                isDataLoadingError.value = true
            })

    }

    fun addReadNews(news: List<News>) {
        _dataLoading.value = true
        val copyList = arrayListOf<News>()
        for (n in news) {
            try {
                val copy = News(n.title, "", "", true, "")
                copyList.add(copy)
            } catch (e: Exception) {

            }

        }
        if (!news.isNullOrEmpty())
            _readNews.value = copyList
        else
            _readNews.value = emptyList()

        _dataLoading.value = false
    }
}
