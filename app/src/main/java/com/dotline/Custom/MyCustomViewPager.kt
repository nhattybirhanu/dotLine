package com.dotline.Custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class MyCustomViewPager(context: Context?, attrs: AttributeSet?) : ViewPager(
    context!!, attrs
) {
 var enabledPager = false
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (enabledPager) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (enabledPager) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setPagingEnabled(enabledPager: Boolean) {
        this.enabledPager = enabledPager
    }
    fun next(){
        setCurrentItem(currentItem+1)
    }

    fun previous(){
        setCurrentItem(currentItem-1)
    }
}