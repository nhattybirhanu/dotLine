package com.dotline.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.dotline.R
import com.dotline.model.Profile
import kotlinx.android.synthetic.main.edit_your_profile.*
import kotlinx.android.synthetic.main.profile_page_1.*
import kotlinx.android.synthetic.main.profile_page_1.view.*
import kotlinx.android.synthetic.main.profile_page_2.*
import kotlinx.android.synthetic.main.profile_page_2.view.*
import kotlinx.android.synthetic.main.profile_page_2.view.email

class LayoutViewPageAdapter(var views: ArrayList<View>, var profile: Profile):PagerAdapter() {
    override fun getCount(): Int {
        return  views.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val page=views.get(position);
        when(position) {
            0 -> {
                page.display_name.text=profile.displayName;
                page.profession.text=profile.department;
                Glide.with(page.profile_image).load(profile.profile_picture).placeholder(R.drawable.ic_default_picture).circleCrop().into(page.profile_image);

            }
            2->{
                page.email.text=profile.email;

            }
        }

        container.addView(page)
        return page
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return  view.equals(`object`);
    }
}