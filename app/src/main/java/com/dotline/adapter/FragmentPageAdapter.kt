package com.natnaelAjema.mycv.Adapter

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter

class FragmentPageAdapter(manager:FragmentManager): FragmentPagerAdapter(manager) {
    var fragemnts=ArrayList<Fragment>();
    var titles=ArrayList<String>();
    override fun getCount()=
        fragemnts.size


    override fun getItem(position: Int): Fragment {
    return fragemnts.get(position);
    }

    fun add(fragemnt:Fragment,title:String){
        fragemnts.add(fragemnt)
        titles.add(title);
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles.get(position);
    }
}