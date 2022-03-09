package com.example.test

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
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
    private val cacheViewList = LinkedList<TextView>()

    init {
        for (i in 1..9) {
            dataList.add((1111 * i).toString())
        }
    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        Log.d("zzxmer", "instantiateItem: position=$position, data[pos]=${dataList[position]}");
        val currentView: TextView = if (cacheViewList.isEmpty()) {
            val newView = TextView(context)

            newView.gravity = Gravity.CENTER
            newView
        } else {
            cacheViewList.remove()
        }

        currentView.text = dataList[position]

        (currentView.parent as? ViewGroup)?.removeView(currentView)
        container.addView(currentView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        return currentView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        Log.d("zzxmer", "destroyItem: position=$position");
        val currentView = `object` as TextView
        container.removeView(currentView)
        cacheViewList.add(currentView)
    }
}