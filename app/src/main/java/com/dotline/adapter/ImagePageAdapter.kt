package com.dotline.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.dotline.R
import com.dotline.model.ImageModel
import kotlinx.android.synthetic.main.image_page_item.view.*

class ImagePageAdapter(var images:ArrayList<ImageModel>):PagerAdapter() {
    override fun getCount(): Int {
    return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var image_layout=LayoutInflater.from(container.context).inflate(R.layout.image_page_item,container,false);
        Glide.with(image_layout.image_view).load(images.get(position).src).placeholder(R.drawable.ic_image).into(image_layout.image_view);
        container.addView(image_layout)
        return  image_layout;
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
    return  view.equals(`object`)
    }
}