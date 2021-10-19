package com.dotline.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dotline.R
import com.dotline.fragments.ImageViewer
import com.dotline.model.BlogContent
import com.dotline.model.ImageConfig
import com.dotline.model.ImageModel
import kotlinx.android.synthetic.main.selector_image_item.view.*

class ImageAdapter(var activity:AppCompatActivity,var images:ArrayList<ImageModel>,var option:ImageConfig,var blogContent: BlogContent?) : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {
            var id=10;

    inner class ImageHolder : RecyclerView.ViewHolder
    {
        constructor(itemView: View):super(itemView){
            itemView.close.setVisibility(if(option.closeButton) View.VISIBLE else View.GONE)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {


        return ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.selector_image_item,parent,false))
    }


    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
    var pic=images.get(position);
        holder.itemView.apply{
            Glide.with(image).load(pic.src).placeholder(R.drawable.ic_image).into(image)
            close.setOnClickListener {
                images.removeAt(position)
                notifyItemRemoved(position)
            }
            image.setOnClickListener {
                var imageView=ImageViewer();
                var arg=Bundle();
                if (blogContent!=null)
                arg.putSerializable("blog",blogContent);
                arg.putSerializable("images",images)
                arg.putInt("pos",position);
                imageView.arguments=arg;
                imageView.show(activity.supportFragmentManager,"");


            }
        }

    }

    override fun getItemCount() =images.size;
    fun addImages(images:ArrayList<ImageModel>){
        var  oldS=this.images.size;
        this.images.addAll(images);

        notifyItemRangeInserted(oldS,images.size)
    }
}