package com.example.test

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var vp: MyViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dataList = mutableListOf<String>().apply {
            for (i in 0..3) {
                add((i * 1111111).toString())
            }
            Log.d("zzxmer", "init data list: $this");
        }
        vp = findViewById(R.id.vp2)
        vp.setAdapter(MyPagerAdapter(this, vp, dataList))

        findViewById<Button>(R.id.btn_scroll).setOnClickListener {
            vp.startAutoScroll()
        }
    }

    override fun onPause() {
        super.onPause()
        vp.stopAutoScroll()
    }
}

class MyPagerAdapter(private val context: Context, private val viewPager: MyViewPager2, private val dataList: List<String>) : MyViewPagerAdapter2() {
    private val cacheViewList = LinkedList<TextView>()

    override fun getCount(): Int {
        return dataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.tag == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val tempPos = position % dataList.size
        val realPos = if (tempPos < 0) {
            tempPos + dataList.size
        } else {
            tempPos
        }
        Log.d("zzxmer", "instantiateItem: position=$position, realPos=$realPos");
        Log.d("zzxmer", "instantiateItem: dataList[realPos]=${dataList[realPos]}");
        // get view and data
        val currentView: TextView = if (cacheViewList.isEmpty()) {
            val newView = TextView(context)
            newView.gravity = Gravity.CENTER
            newView.setBackgroundColor(Color.RED)
            newView
        } else {
            cacheViewList.remove()
        }
        val data = dataList[realPos]

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
}

class Holder(val data: String, val viewHolder: ViewHolder) {
    class ViewHolder(val textView: TextView)// TODO: zhuxiaomei 2022/3/13 elements of MyView
}