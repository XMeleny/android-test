package com.example.test

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.TextView

/*
1. 处理滑动逻辑
    1. 检测滑动，计算滑动值
    2. 根据速度和阈值等，实现fully scroll
2. 显示多个view
    1. 根据scroll值显示
3. 允许设置adapter
 */

class MyViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ViewGroup(context, attrs, defStyleAttr) {


    val mTouchSlop by lazy { ViewConfiguration.get(context).scaledPagingTouchSlop }

    fun test() {
        addView(TextView(context).apply { text = "11111111111" })
        addView(TextView(context).apply { text = "22222222222" })
//        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
            getDefaultSize(0, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount == 0) {
            return
        }
        val childWidth = r - l - paddingLeft - paddingRight
        val childHeight = b - t
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childLeft = paddingLeft + i * childWidth
            child.layout(childLeft, paddingTop, childLeft + childWidth, paddingTop + childHeight)
        }
    }

    var mLastMotionX: Float = 0f
    var mLastMotionY: Float = 0f


    // 用scrollTo滑动内容
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mLastMotionX = x
                mLastMotionY = y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - mLastMotionX
                scrollTo((scrollX - dx).toInt(), scrollY)
                mLastMotionX = x
            }

            else -> {
            }
        }
        return true
    }
}