package com.dotline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dotline.R
import com.dotline.adapter.ImagePageAdapter
import com.dotline.model.BlogContent
import com.dotline.model.ImageModel
import kotlinx.android.synthetic.main.image_viewer_adapter.*


class ImageViewer:DialogFragment() {
    var blogContent:BlogContent?=null
    var position=0;
    var images:ArrayList<ImageModel>?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NO_TITLE, R.style.Theme_MyApplication);
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
                if (arguments!=null){
                    blogContent= requireArguments().getSerializable("blog") as BlogContent?
                    position=requireArguments().getInt("pos",0);
                    images= requireArguments().getSerializable("images") as ArrayList<ImageModel>?;
                }
                return  inflater.inflate(R.layout.image_viewer_adapter,container,false);
            }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (images!=null){
            var adapter=ImagePageAdapter(images!!)
            viewpager.adapter=adapter;
            viewpager.currentItem=position;
        }
        if (blogContent!=null){

        }
        toolbar.setNavigationOnClickListener { dismiss() }

    }
    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog!!.window
            ?.getAttributes()?.windowAnimations = R.style.DialogAnimation
    }

}