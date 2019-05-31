package com.example.newsdemo.news

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsdemo.R
import com.example.newsdemo.data.News
import com.example.newsdemo.databinding.NewsFragmentBinding

class NewsFragment : Fragment(), View.OnClickListener {

    private lateinit var viewDataBinding: NewsFragmentBinding
    private var readNews: ArrayList<News> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = NewsFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = (activity as NewsActivity).obtainViewModel()
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.viewmodel?.start()
    }

    override fun onClick(v: View?) {
        val news = v?.tag as News

        if(readNews.contains(news))return
        readNews.add(news)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_all -> {
                viewDataBinding.viewmodel?.setFiltering(NewsFilterType.ALL_NEWS)
                true
            }
            else -> {
                viewDataBinding.viewmodel?.setFiltering(NewsFilterType.READY_ALREADY_NEWS)
                viewDataBinding.viewmodel?.addReadNews(readNews)
                false
            }
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.news_fragment_menu, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
        initRecyclerView()
        setupRefreshLayout()
    }

    private fun initRecyclerView() {
        viewDataBinding.newsList.layoutManager = LinearLayoutManager(this.context) as RecyclerView.LayoutManager?
        viewDataBinding.newsList.addItemDecoration(DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL))

        if (viewDataBinding.viewmodel != null)
            viewDataBinding.newsList.adapter = NewsViewAdapter(viewDataBinding.viewmodel!!.items.value, this)

        viewDataBinding.newsList.addOnItemTouchListener(MyOnItemTouchListener(viewDataBinding, MyOnRightClickListener(viewDataBinding.viewmodel, readNews)))
    }

    private fun setupRefreshLayout() {
        viewDataBinding.refreshLayout.run {
            setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
                ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark)
            )
            scrollUpChild = viewDataBinding.newsList
        }
    }

    companion object {
        fun newInstance() = NewsFragment()
    }
}

class MyOnRightClickListener(private val viewModel: NewsViewModel?, private val readNewsList: ArrayList<News>) : OnRightClickListener {

    override fun onRightClick(position: Int) {
        if(!readNewsList.isNullOrEmpty()){
            readNewsList.removeAt(position)
            viewModel?.addReadNews(readNewsList)
        }
    }
}

@SuppressLint("StaticFieldLeak")
class MyOnItemTouchListener(private val viewModel: NewsFragmentBinding, private val rightClickListener: OnRightClickListener) : RecyclerView.OnItemTouchListener {

    var xDown: Int = 0
    var yDown: Int = 0
    var xMove: Int = 0
    var yMove: Int = 0
    var touchSlop: Int = 100
    private var currentSelectPosition: Int = 0
    var moveWidth: Int = 0
    var hiddenWidth: Int = 0
    var isFirst: Boolean = true
    var lastItemLayout: LinearLayout? =null
    var currentItemLayout: LinearLayout? =null

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        when(e.action){
            MotionEvent.ACTION_DOWN->handleDownEvent(rv, e)

            MotionEvent.ACTION_MOVE-> handleMoveEvent(rv, e)

            MotionEvent.ACTION_UP->handleUpEvent(rv, e)
        }


        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }

    private fun handleUpEvent(rv: RecyclerView, e: MotionEvent) {
        val scrollX = currentItemLayout?.scrollX
        if(hiddenWidth> moveWidth){
            val toX = hiddenWidth - moveWidth
            if(scrollX!! > hiddenWidth/2){
                currentItemLayout?.scrollBy(toX, 0)
                moveWidth= hiddenWidth
            }else{
                currentItemLayout?.scrollBy(0- moveWidth, 0)
                moveWidth=0
            }
        }
        lastItemLayout= currentItemLayout
    }

    private fun handleMoveEvent(rv: RecyclerView, e: MotionEvent) {
        xMove = e.x.toInt()
        yMove = e.y.toInt()

        val dx = xMove - xDown
        val dy = yMove - yDown

        if(viewModel.viewmodel?.isCurrentFilteringAll?.value!!) return
        if(dx<0&&Math.abs(dx)> touchSlop&&Math.abs(dy)< touchSlop){
            var newScrollX = Math.abs(dx)
            if(moveWidth>= hiddenWidth){
                newScrollX=0
            }else if(moveWidth+newScrollX> hiddenWidth){
                newScrollX= hiddenWidth- moveWidth
            }

            currentItemLayout?.scrollBy(newScrollX, 0)
            moveWidth += newScrollX
        }else if(dx>0){
            currentItemLayout?.scrollBy(0- moveWidth, 0)
            moveWidth=0
        }
    }

    private fun handleDownEvent(rv: RecyclerView, e: MotionEvent) {
        xDown = e.x.toInt()
        yDown = e.y.toInt()
        val linearLayoutManager = rv.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
        val rect = Rect()
        val count = rv.childCount
        for(i in 0..count){
            val child = rv.getChildAt(i)
            child.getHitRect(rect)
            if(rect.contains(xDown, yDown)){
                currentSelectPosition=firstVisibleItemPosition+i
                break
            }
        }

        if(isFirst) isFirst=false
        else{
            if(lastItemLayout!=null&& moveWidth>0){
                lastItemLayout?.scrollBy((0- moveWidth), 0)
                hiddenWidth=0
                moveWidth=0
                rightClickListener?.onRightClick(currentSelectPosition)
            }
        }


        val item = rv.getChildAt(currentSelectPosition - firstVisibleItemPosition)
        if(item!=null){
            val viewHolder = rv.getChildViewHolder(item)
            currentItemLayout = viewHolder.itemView as LinearLayout

            val llHidenView = currentItemLayout?.findViewById<LinearLayout>(R.id.llHidden)
            hiddenWidth= llHidenView?.width!!
        }
    }

}

interface OnRightClickListener {
    fun onRightClick(position: Int)
}
