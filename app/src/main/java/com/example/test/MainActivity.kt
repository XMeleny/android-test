package com.example.test

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ViewPager>(R.id.viewpager).adapter = MyPagerAdapter(this)
    }
}

class MyPagerAdapter(private val context: Context) : PagerAdapter() {
    private val dataList = mutableListOf<String>()
    private val cacheViewList = LinkedList<View>()

    init {
        for (i in 1..9) {
            dataList.add((1111 * i).toString())
        }
        Log.d("zzxmer", "dataList = $dataList");
    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        Log.d("zzxmer", "instantiateItem: position=$position, data[pos]=${dataList[position]}");

        val currentView: View = if (cacheViewList.isEmpty()) {
            val newView = LayoutInflater.from(context).inflate(R.layout.custom_view_layout, container, false)
            val holder = MyViewHolder(newView.findViewById(R.id.textview))
            newView.tag = holder
            newView
        } else {
            cacheViewList.remove()
        }

        val viewHolder = currentView.tag as MyViewHolder
        viewHolder.textView.text = dataList[position]

        (currentView.parent as? ViewGroup)?.removeView(currentView)
        container.addView(currentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        return currentView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        Log.d("zzxmer", "destroyItem: position=$position");
        val currentView = `object` as View
        container.removeView(currentView)
        cacheViewList.add(currentView)
    }
}

class MyViewHolder(val textView: TextView)