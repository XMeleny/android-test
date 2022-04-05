package com.example.test

import android.content.Context
import android.graphics.Color
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
import java.lang.ref.WeakReference
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var vp: MyViewPager2
    private lateinit var handler: MyHandler

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

        handler = MyHandler(WeakReference(vp))
        handler.sendEmptyMessageDelayed(MyHandler.MSG_AUTO_SCROLL, 1000)
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


class MyHandler(private val viewPager: WeakReference<MyViewPager2>) : Handler(Looper.myLooper()!!) {
    companion object {
        const val MSG_AUTO_SCROLL = 1000
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        removeMessages(msg.what)
        if (msg.what == MSG_AUTO_SCROLL) {

//            viewPager.get()?.apply { // 自动轮播，测试期间暂时关闭
//                currentItem += 1
//            }
        }
    }
}