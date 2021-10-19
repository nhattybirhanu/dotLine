package com.dotline.Custom

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import com.dotline.R

class CustomSearchView {
    fun customize(searchView: SearchView) {
        val context = searchView.context
        val textView: SearchView.SearchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        textView.setTextColor(Color.BLACK)
        textView.setHintTextColor(context.resources.getColor(R.color.colorSecondaryDark))
        val search_icon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        search_icon.imageTintList =
            ColorStateList.valueOf(context.resources.getColor(R.color.white))
        val hint: TextView = searchView.findViewById(androidx.appcompat.R.id.search_badge)
    }
}