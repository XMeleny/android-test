package com.example.test

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var viewPager: ViewPager
    lateinit var handler: MyHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewpager)
        val dataList = mutableListOf<String>().apply {
            for (i in 1..5) {
                add((i * 1111111).toString())
            }
            Log.d("zzxmer", "init data list: $this");
        }
        initViewPager(dataList)
        handler = MyHandler(WeakReference(viewPager))
        handler.sendEmptyMessageDelayed(MyHandler.MSG_AUTO_SCROLL, 1000)
    }

    fun initViewPager(dataList: List<String>) {
        val myPagerAdapter = MyPagerAdapter(this, viewPager, dataList)
        viewPager.adapter = myPagerAdapter
        if (dataList.size > 1) {
            viewPager.currentItem = 1
        }

        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffset == 0f) {
                    Log.d("zzxmer", "onPageScrolled: pos=$position");
                    myPagerAdapter.update()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager.SCROLL_STATE_DRAGGING || state == ViewPager.SCROLL_STATE_SETTLING) {
                    handler.removeMessages(MyHandler.MSG_AUTO_SCROLL)
                } else {
                    handler.sendEmptyMessageDelayed(MyHandler.MSG_AUTO_SCROLL, 1000)
                }
            }
        })
    }
}

class MyPagerAdapter(private val context: Context, private val viewPager: ViewPager, private val dataList: List<String>) : PagerAdapter() {
    private val cacheViewList = LinkedList<TextView>()
    private var showList = ArrayList<String>()

    init {
        resetData(0)
    }

    /**
     * @param pos data 在 dataList 中的真实位置
     * 使用这个方法替代 viewPager.setCurrentItem
     */
    fun resetData(pos: Int) {
        if (dataList.size > 1) {

            showList.clear()

            val leftPos = (dataList.size + pos - 1) % (dataList.size)
            val realPos = (pos + dataList.size) % (dataList.size) // 不直接用pos，为了避免越界异常
            val rightPos = (pos + 1) % (dataList.size)

            showList.add(dataList[leftPos])
            showList.add(dataList[realPos])
            showList.add(dataList[rightPos])
        } else if (dataList.size == 1) {
            showList = dataList as ArrayList<String>
        }

        Log.d("zzxmer", "resetData: pos=$pos, showList=$showList");
    }

    override fun getCount(): Int {
        return showList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.tag == `object` // FIXME: zhuxiaomei 2022/3/13
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        Log.d("zzxmer", "instantiateItem: position=$position, data[pos]=${showList[position]}");
        // get view and data
        val currentView: TextView = if (cacheViewList.isEmpty()) {
            val newView = TextView(context)
            newView.gravity = Gravity.CENTER
            newView
        } else {
            cacheViewList.remove()
        }
        val data = showList[position]

        // bind view and data
        currentView.text = data

        // add view to pager
        (currentView.parent as? ViewGroup)?.removeView(currentView)
        container.addView(currentView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        val holder = Holder(data, Holder.ViewHolder(currentView))

        currentView.tag = holder
        return holder
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        Log.d("zzxmer", "destroyItem: position=$position");
        val holder = `object` as Holder

        val currentView = holder.viewHolder.textView

        container.removeView(currentView)
        cacheViewList.add(currentView)
    }

    fun update() {
        resetData(dataList.indexOf(showList[viewPager.currentItem]))// get real index of showing element in data list
        notifyDataSetChanged() // invoke getItemPosition
    }

    override fun getItemPosition(`object`: Any): Int {
        val holder = `object` as Holder
        val data = holder.data
        Log.d("zzxmer", "getItemPosition: showList=${showList}, data=$data")

        if (showList.contains(data)) {
            return showList.indexOf(data)
        }
        return POSITION_NONE // invoke destroyItem
    }
}

class Holder(val data: String, val viewHolder: ViewHolder) {
    class ViewHolder(val textView: TextView)// TODO: zhuxiaomei 2022/3/13 elements of MyView
}


class MyHandler(private val viewPager: WeakReference<ViewPager>) : Handler(Looper.myLooper()!!) {
    companion object {
        const val MSG_AUTO_SCROLL = 1000
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        removeMessages(msg.what)
        if (msg.what == MSG_AUTO_SCROLL) {
            viewPager.get()?.apply {
                currentItem += 1
            }
        }
    }
}