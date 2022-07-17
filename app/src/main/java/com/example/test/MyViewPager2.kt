package com.example.test

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import java.lang.ref.WeakReference
import java.util.*

class MyViewPager2 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ViewGroup(context, attrs, defStyleAttr) {
    class ItemInfo {
        var pos: Int = 0        // layout 位置为 pos*childWidth, 可以小于 0
        lateinit var viewHolder: XMViewHolder   // 用于对应view

        override fun toString(): String {
            return pos.toString()
        }
    }

    private val mItemInfos = ArrayList<ItemInfo>()
    private var mCurItemInfo: ItemInfo? = null
    private var mCurPosition: Int = 0

    private var mAdapter: XMViewPagerAdapter? = null

    private var realChildWidth: Int = 0


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
            getDefaultSize(0, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount == 0) {
            return
        }
        val childWidth = r - l - paddingLeft - paddingRight
        realChildWidth = childWidth
        val childHeight = b - t
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val itemInfo = getItemInfoByChildView(child)
            itemInfo?.apply {
                val childLeft = paddingLeft + itemInfo.pos * childWidth
                child.layout(childLeft, paddingTop, childLeft + childWidth, paddingTop + childHeight)
            }
        }
    }

    private var mLastMotionX: Float = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                handler.removeMessages(MyHandler2.MSG_AUTO_SCROLL)
                mLastMotionX = x
            }
            MotionEvent.ACTION_MOVE -> {
                handler.removeMessages(MyHandler2.MSG_AUTO_SCROLL)
                val dx = x - mLastMotionX
                scrollTo((scrollX - dx).toInt(), scrollY)
                mLastMotionX = x


                if (realChildWidth > 0) {
                    val curPos = scrollX / realChildWidth
                    if (curPos != mCurPosition) {
                        mCurPosition = curPos
                        populate()
                    }
                }
            }
            else -> {
                if (isAutoScroll) {
                    handler.sendEmptyMessageDelayed(MyHandler2.MSG_AUTO_SCROLL, 1000)
                }
                // TODO: zhuxiaomei 2022/4/5 根据 velocity 和 scroll 完整跳转
            }
        }
        return true // FIXME: zhuxiaomei 2022/3/27 暂不考虑 touch 事件分发
    }

    fun setAdapter(adapter: XMViewPagerAdapter?) {
        if (mAdapter != null) {
            for (itemInfo in mItemInfos) {
                destroyItem(itemInfo)
            }
            mItemInfos.clear()

            mCurItemInfo = null
            mCurPosition = 0
            scrollTo(0, 0)
        }
        mAdapter = adapter
        if (mAdapter == null) {
            return
        }
        populate()
    }

    /**
     * 创建左右和当前item,并展示
     * pos指向当前绘制位置; idx指向当前绘制数据位置
     */
    private fun populate(position: Int = mCurPosition) {
        if (mAdapter == null || mAdapter!!.getCount() <= 0) {
            return
        }

        mCurPosition = position

        //获取真 当前位置,用于清除缓存
        var curIdx = 0 // 需要展示的页面在 mItemInfos 中的位置
        var curItem: ItemInfo? = null
        for (idx in 0 until mItemInfos.size) {
            val item = mItemInfos[idx]
            if (item.pos >= position) { // mItemInfos 按照 pos 从小到大排序
                if (item.pos == position) {
                    curIdx = idx
                    curItem = item
                }
                break
            }
        }

        if (curItem == null) {
            curItem = addNewItem(mCurPosition, curIdx)
        }

        // 目前只支持左右各一个缓存
        val startPos = mCurPosition - 1
        val endPos = mCurPosition + 1

        // 处理 mCurPosition 左边的 item
        var pos = mCurPosition - 1
        var idx = curIdx - 1
        while (true) {
            val itemInfo = if (idx >= 0) mItemInfos[idx] else null
            if (pos < startPos) {
                if (itemInfo == null) { // mItemInfos 按照 pos 顺序排列. 已经超出+-x的范围, 已经不会有其他 itemInfo 需要处理了
                    break
                }
                if (pos == itemInfo.pos) {
                    destroyItem(itemInfo)
                    mItemInfos.removeAt(idx)
                    idx--
                    curIdx--
                }
            } else if (itemInfo != null) {
                idx--
            } else {
                addNewItem(pos, idx)
                curIdx++
            }
            pos--
        }

        // 处理 mCurPosition 右边的 item
        pos = mCurPosition + 1
        idx = curIdx + 1
        while (true) {
            val itemInfo = if (idx < mItemInfos.size) mItemInfos[idx] else null
            if (pos > endPos) {
                if (itemInfo == null) {
                    break
                }
                if (pos == itemInfo.pos) {
                    destroyItem(itemInfo)
                    mItemInfos.removeAt(idx)
                }
            } else if (itemInfo != null && pos == itemInfo.pos) {
                idx++
            } else {
                addNewItem(pos, idx)
                idx++
            }
            pos++
        }
    }

    private fun addNewItem(position: Int, index: Int): ItemInfo {
        Log.d("zzxmer", "addNewItem param: position=$position, index=$index");

        val itemInfo = ItemInfo()
        itemInfo.pos = position
        itemInfo.viewHolder = instantiateItem(position)

        if (index < 0) {
            mItemInfos.add(0, itemInfo)
        } else if (index > mItemInfos.size) {
            mItemInfos.add(itemInfo)
        } else {
            mItemInfos.add(index, itemInfo)
        }

        Log.d("zzxmer", "addNewItem result: mItemInfos=$mItemInfos");
        return itemInfo
    }

    fun getItemInfoByChildView(child: View): ItemInfo? {
        if (mAdapter == null) {
            return null
        }
        for (itemInfo in mItemInfos) {
            if (child.tag == itemInfo.viewHolder) {
                return itemInfo
            }
        }
        return null
    }

    fun autoScroll() {
        populate(mCurPosition + 1)
        scrollTo(mCurPosition * realChildWidth, 0)
        if (isAutoScroll) {
            handler.sendEmptyMessageDelayed(MyHandler2.MSG_AUTO_SCROLL, 1000)
        }
    }

    var isAutoScroll = false
    fun startAutoScroll() {
        isAutoScroll = true
        handler.sendEmptyMessageDelayed(MyHandler2.MSG_AUTO_SCROLL, 1000)
    }

    fun stopAutoScroll() {
        isAutoScroll = false
        handler.removeMessages(MyHandler2.MSG_AUTO_SCROLL)
    }

    private val handler = MyHandler2(WeakReference(this))

    private fun instantiateItem(position: Int): XMViewHolder {
        val currentView = if (mAdapter!!.cacheViewList.isEmpty()) {
            val viewHolder = mAdapter!!.getEmptyView()
            viewHolder.view.tag = viewHolder
            viewHolder.view
        } else {
            mAdapter!!.cacheViewList.remove()
        }

        val size = mAdapter!!.getCount()
        val tempPos = position % size
        val realPos = if (tempPos < 0) {
            tempPos + size
        } else {
            tempPos
        }

        mAdapter!!.bind(currentView, realPos)

        removeView(currentView)
        addView(currentView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

        return currentView.tag as XMViewHolder
    }

    private fun destroyItem(itemInfo: ItemInfo) {
        val view = itemInfo.viewHolder.view
        removeView(view)
        mAdapter!!.cacheViewList.add(view)
    }
}

class MyHandler2(val vp: WeakReference<MyViewPager2>) : Handler(Looper.getMainLooper()) {
    companion object {
        const val MSG_AUTO_SCROLL = 1000
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        removeMessages(msg.what)
        if (msg.what == MSG_AUTO_SCROLL) {
            vp.get()?.autoScroll()
        }
    }
}

open class XMViewPagerAdapter() {
    val cacheViewList = LinkedList<View>()
    open fun getCount(): Int {
        return 0
    }

    open fun bind(view: View, position: Int) {
        throw IllegalArgumentException("should override")
    }

    open fun getEmptyView(): XMViewHolder {
        throw IllegalArgumentException("should override")
    }
}

open class XMViewHolder(val view: View) {

}

