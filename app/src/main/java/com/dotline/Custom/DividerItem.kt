package com.dotline.Custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*

class DividerItem(private val context: Context, var drawable: Drawable) : ItemDecoration() {
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: State) {
//        val left: Int = parent.getPaddingLeft() + 0
        val right: Int = parent.getWidth() - parent.getPaddingRight()
        val childcount: Int = parent.getChildCount()
        for (i in 0 until childcount - 1) {
            val view: View = parent.getChildAt(i)
            val layoutParams: LayoutParams =
                view.layoutParams as LayoutParams
            val top: Int = view.bottom + layoutParams.bottomMargin
            val bottom: Int = top + drawable.getIntrinsicHeight()
            drawable.setBounds(0, top, right, bottom)
            drawable.draw(c)
        }
    }


}