package com.example.test

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var vp: MyViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dataList = mutableListOf<String>().apply {
            for (i in 1..3) {
                add((i * 1111111).toString())
            }
            Log.d("zzxmer", "init data list: $this");
        }
        vp = findViewById(R.id.vp2)
        vp.setAdapter(MyXMPagerAdapter(this, dataList))

        findViewById<Button>(R.id.btn_start_scroll).setOnClickListener {
            vp.startAutoScroll()
        }

        findViewById<Button>(R.id.btn_stop_scroll).setOnClickListener {
            vp.stopAutoScroll()
        }
    }
}

class MyXMPagerAdapter(private val context: Context, private val dataList: List<String>) : XMViewPagerAdapter() {
    override fun getCount(): Int {
        return dataList.size
    }

    override fun bind(view: View, position: Int) {
        Log.d("zzxmer", "bind: position=$position")
        val myView = view as TextView
        myView.text = dataList[position]
    }

    override fun getEmptyView(): XMViewHolder {
        val newView = TextView(context)
        newView.gravity = Gravity.CENTER
        newView.setBackgroundColor(Color.RED)

        return XMViewHolder(newView)
    }
}