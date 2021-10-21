package com.dotline.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.dotline.R
import com.dotline.adapter.TagAdapter
import com.dotline.callbacks.Selected
import com.dotline.model.Tag
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.android.synthetic.main.profile_page.*
import kotlinx.android.synthetic.main.tag_selector.*
import java.util.ArrayList


class TagSelector:DialogFragment(),Toolbar.OnMenuItemClickListener {
    var selector:Selected?=null;
    var toolbar: Toolbar?=null;
    lateinit var adapter:TagAdapter;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tag_selector,container,false);
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NO_TITLE, R.style.Theme_MyApplication);
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar=view.findViewById(R.id.toolbar);
        toolbar?.setOnMenuItemClickListener(this)
        toolbar?.setNavigationOnClickListener { dismiss() }
       if (selector!=null){
           adapter=TagAdapter(Tag.tags, selector!!, activity as AppCompatActivity,true);
           val layoutManager = FlexboxLayoutManager(context)
           layoutManager.flexDirection = FlexDirection.ROW
           layoutManager.justifyContent = JustifyContent.SPACE_EVENLY
           recyclerview.layoutManager=layoutManager;
           recyclerview.adapter=adapter;
       }

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.done-> {
                selector?.done();
                dismiss();
            }
        }
return false
    }

    fun setSelected(tags: ArrayList<Tag>) {
        for (tag in tags){
            tag.selected=true;
            adapter.modify(tag)

        }
    }


}