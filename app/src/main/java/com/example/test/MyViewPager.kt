package com.example.test

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import java.util.*
import kotlin.math.max
import kotlin.math.min

/*
1. 处理滑动逻辑
    1. 检测滑动，计算滑动值
    2. 根据速度和阈值等，实现fully scroll
2. 显示多个view
    1. 根据scroll值显示
3. 允许设置adapter
 */

/**
 * 考虑每个子view都是刚好match width-paddingLeft-paddingRight
 */
class MyViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ViewGroup(context, attrs, defStyleAttr), DataChangeListener {

    private val INVALID_CHILD_WIDTH = -1
    private var mRealChildWidth = INVALID_CHILD_WIDTH

    var mFirstLayout = true

    var mAdapter: MyViewPagerAdapter? = null


    fun test() {
        addView(TextView(context).apply { text = "11111111111" })
        addView(TextView(context).apply { text = "22222222222" })
        addView(TextView(context).apply { text = "33333333333" })
        addView(TextView(context).apply { text = "44444444444" })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
            getDefaultSize(0, heightMeasureSpec))
        populate()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount == 0) {
            return
        }
        val childWidth = r - l - paddingLeft - paddingRight
        mRealChildWidth = childWidth
        val childHeight = b - t
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val itemInfo = getItemInfoByChild(child)
            itemInfo?.apply {
                val childLeft = paddingLeft + itemInfo.position * childWidth
                child.layout(childLeft, paddingTop, childLeft + childWidth, paddingTop + childHeight)
            }
        }

        if (mFirstLayout) {
            scrollToPosition(mCurrentPosition, false)
        }
        mFirstLayout = false
    }

    var mLastMotionX: Float = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mLastMotionX = x
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - mLastMotionX
                scrollTo((scrollX - dx).toInt(), scrollY)
                mLastMotionX = x
            }
            else -> {
                if (mAdapter == null) {
                    scrollTo(0, 0)
                } else {
                    setCurrentItemInternal(((scrollX.toFloat() / getChildWidth().toFloat()) + 0.5f).toInt(), true)
                }
            }
        }
        return true // FIXME: zhuxiaomei 2022/3/27 暂不考虑 touch 事件分发
    }

    // TODO: zhuxiaomei 2022/3/27 smooth scroll
    private fun scrollToPosition(position: Int, smoothScroll: Boolean) {
        scrollTo(position * getChildWidth(), 0)
        if (!smoothScroll) { // FIXME: zhuxiaomei 2022/3/28 事件发送有问题
            for (pageScrollLister in mOnPageChangeListeners) {
                pageScrollLister.onPageScrolled(position, 0f, 0)
            }
        }
    }

    fun setAdapter(adapter: MyViewPagerAdapter) {
        if (mAdapter != null) {
            for (item in mItemInfos) {
                mAdapter!!.destroyItem(this, item.position, item.obj)
            }
            mItemInfos.clear()
            mCurrentPosition = 0
            mRealChildWidth = INVALID_CHILD_WIDTH
            scrollTo(0, 0)
            mAdapter!!.dataChangeListener = null
        }

        mAdapter = adapter
        mAdapter!!.dataChangeListener = this
        val firstLayout = mFirstLayout
        mFirstLayout = true
        if (firstLayout) {
            requestLayout()
        } else {
            populate()
        }
    }

    class ItemInfo {
        lateinit var obj: Any
        var position: Int = 0
    }

    private val mItemInfos = arrayListOf<ItemInfo>()

    var mCurrentPosition = 0

    private fun populate() {
        populate(mCurrentPosition)
    }

    private fun populate(position: Int) {
        var lastItemInfo: ItemInfo? = null
        if (mCurrentPosition != position) {
            lastItemInfo = getItemInfoByPosition(mCurrentPosition)
            mCurrentPosition = position
        }

        if (mAdapter == null || mAdapter!!.getCount() <= 0) {
            return
        }

        var curIdx = 0
        var curItem: ItemInfo? = null

        for (idx in 0 until mItemInfos.size) {
            val item = mItemInfos[idx]
            if (item.position >= position) {
                if (item.position == position) {
                    curIdx = idx
                    curItem = item
                }
                break
            }
        }

        if (curItem == null) {
            curItem = addNewItem(mCurrentPosition, curIdx)
        }

        // 目前只支持左右一个缓存，并不考虑每个 child view 的大小（都当成是 measure width - padding)
        val startPos = max(mCurrentPosition - 1, 0)
        val endPos = min(mCurrentPosition + 1, mAdapter!!.getCount() - 1)

        var index: Int = curIdx - 1
        for (pos in mCurrentPosition - 1 downTo 0) {
            val itemInfo = if (index >= 0) mItemInfos[index] else null
            if (pos < startPos) {
                if (itemInfo == null) {
                    break
                }
                if (pos == itemInfo.position) {
                    mItemInfos.removeAt(index)
                    mAdapter!!.destroyItem(this, pos, itemInfo.obj)
                    index--
                    curIdx--
                }
            } else if (itemInfo != null && pos == itemInfo.position) {
                index--
            } else {
                addNewItem(pos, index + 1)
                curIdx++
            }
        }

        index = curIdx + 1
        for (pos in mCurrentPosition + 1 until mAdapter!!.getCount()) {
            val itemInfo = if (index < mItemInfos.size) mItemInfos[index] else null
            if (pos > endPos) {
                if (itemInfo == null) {
                    break
                }
                if (pos == itemInfo.position) {
                    mItemInfos.removeAt(index)
                    mAdapter!!.destroyItem(this, pos, itemInfo.obj)
                }
            } else if (itemInfo != null && pos == itemInfo.position) {
                index++
            } else {
                addNewItem(pos, index)
                index++
            }
        }
    }


    private fun addNewItem(position: Int, index: Int): ItemInfo {
        val itemInfo = ItemInfo()
        itemInfo.position = position
        itemInfo.obj = mAdapter!!.instantiateItem(this, position)

        if (index < 0 || index >= mItemInfos.size) {
            mItemInfos.add(itemInfo)
        } else {
            mItemInfos.add(index, itemInfo)
        }

        return itemInfo
    }

    private fun getItemInfoByPosition(position: Int): ItemInfo? {
        for (itemInfo in mItemInfos) {
            if (itemInfo.position == position) {
                return itemInfo
            }
        }
        return null
    }

    private fun getItemInfoByChild(child: View): ItemInfo? {
        if (mAdapter == null) {
            return null
        }
        for (itemInfo in mItemInfos) {
            if (mAdapter!!.isViewFromObject(child, itemInfo.obj)) {
                return itemInfo
            }
        }
        return null
    }

    private fun getChildWidth() = if (mRealChildWidth >= 0) mRealChildWidth else measuredWidth - paddingLeft - paddingRight

    override fun onChange() { // 是由 mAdapter 调起的，mAdapter 不会为空
        var newCurrentPosition: Int = mCurrentPosition

        var index = 0
        while (index < mItemInfos.size) {
            Log.d("zzxmer", "index=$index");
            val itemInfo = mItemInfos[index]
            val pos = mAdapter!!.getItemPosition(itemInfo.obj)

            if (pos == MyViewPagerAdapter.POSITION_UNCHANGED) {
                index++
                continue
            }

            if (pos == MyViewPagerAdapter.POSITION_NONE) {
                mAdapter!!.destroyItem(this, itemInfo.position, itemInfo.obj)
                mItemInfos.remove(itemInfo)
                continue
            }

            if (itemInfo.position != pos) {
                if (itemInfo.position == mCurrentPosition) {
                    newCurrentPosition = pos
                }
                itemInfo.position = pos
            }
            index++
        }
        mItemInfos.sortWith { o1, o2 -> o1.position - o2.position }
        setCurrentItemInternal(newCurrentPosition, false)

        populate(newCurrentPosition)
        requestLayout()
    }

    fun setCurrentItem(toPosition: Int) {
        setCurrentItemInternal(toPosition, false)
    }

    fun getCurrentItem(): Int {
        return mCurrentPosition
    }

    private fun setCurrentItemInternal(toPosition: Int, smoothScroll: Boolean) {
        if (mAdapter == null || mAdapter!!.getCount() <= 0) {
            return
        }
        val position = toPosition.coerceIn(0, mAdapter!!.getCount() - 1)

        if (mFirstLayout) {
            mCurrentPosition = position
            requestLayout()
        } else {
            populate(position)
            scrollToPosition(position, smoothScroll)
        }
    }

    val mOnPageChangeListeners = ArrayList<OnPageChangeListener>()
    fun addOnPageChangeListener(listener: OnPageChangeListener) {
        mOnPageChangeListeners.add(listener)
    }
}

open class MyViewPagerAdapter {
    var dataChangeListener: DataChangeListener? = null
    open fun getCount(): Int {
        return 0
    }

    open fun instantiateItem(container: ViewGroup, position: Int): Any {
        throw IllegalArgumentException("should override")
    }

    open fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        // do nothing
    }

    open fun isViewFromObject(view: View, obj: Any): Boolean {
        return false
    }

    fun notifyDataSetChanged() {
        dataChangeListener?.onChange()
    }

    companion object {
        const val POSITION_UNCHANGED = -1
        const val POSITION_NONE = -2
    }


    open fun getItemPosition(obj: Any): Int {
        return POSITION_UNCHANGED
    }
}

interface DataChangeListener {
    fun onChange()
}