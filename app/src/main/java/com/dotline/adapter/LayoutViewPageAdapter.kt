package com.dotline.adapter

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.dotline.R
import com.dotline.fragments.WebFragment
import com.dotline.model.Profile
import kotlinx.android.synthetic.main.edit_your_profile.*
import kotlinx.android.synthetic.main.profile_page_1.*
import kotlinx.android.synthetic.main.profile_page_1.view.*
import kotlinx.android.synthetic.main.profile_page_2.*
import kotlinx.android.synthetic.main.profile_page_2.view.*
import kotlinx.android.synthetic.main.profile_page_2.view.email

class LayoutViewPageAdapter(var views: ArrayList<View>, var profile: Profile,var activity: AppCompatActivity):PagerAdapter() {

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
            1->{
                page.email.text=profile.email;
                page.linkdin.text=profile.linkedin;
                page.github.text=profile.github;
                handleUrlClicks(page.github,"Github")
                handleUrlClicks(page.linkdin,"LinkedIn")
            }
        }

        container.addView(page)
        return page
    }
    fun  handleUrlClicks(textView:TextView,title:String) {
        //create span builder and replaces current text with it
            textView.apply {

                text = SpannableStringBuilder.valueOf(text).apply {
                    //search for all URL spans and replace all spans with our own clickable spans
                    getSpans(0, length, URLSpan::class.java).forEach {
                        //add new clickable span at the same position
                        setSpan(
                            object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    var bundle=Bundle();
                                    bundle.putString("title",title)
                                    bundle.putString("url",it.url);
                                    var webFragment=WebFragment();
                                    webFragment.arguments=bundle
                                    webFragment.show(activity.supportFragmentManager,"");

                                //   onClicked?.invoke(it.url)
                                }
                            },
                            getSpanStart(it),
                            getSpanEnd(it),
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        //remove old URLSpan
                        removeSpan(it)
                    }
                }
                //make sure movement method is set
                movementMethod = LinkMovementMethod.getInstance()
            }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return  view.equals(`object`);
    }
}