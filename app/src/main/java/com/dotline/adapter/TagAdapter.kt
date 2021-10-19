package com.dotline.adapter

import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dotline.R
import com.dotline.callbacks.Selected
import com.dotline.model.Tag
import kotlinx.android.synthetic.main.big_tag_item.view.*

class TagAdapter(var tags:ArrayList<Tag>,var selector:Selected,var activity: AppCompatActivity,var selectMood:Boolean): RecyclerView.Adapter<TagAdapter.TagHolder>() {

    inner  class TagHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagHolder {
        return  TagHolder(LayoutInflater.from(parent.context).inflate(R.layout.big_tag_item,parent,false));
    }

    override fun onBindViewHolder(holder: TagHolder, position: Int) {
        var tag=tags.get(position);
        holder.itemView.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&&selectMood) {
                tag_name.backgroundTintList= ColorStateList.valueOf(activity.resources.getColor(if (tag.selected) R.color.colorAccent else R.color.colorSecondaryDark))
            }
            tag_name.text=tag.name;
            tag_name.setOnClickListener {
                    if (selectMood){
                        tag.selected=!tag.selected;
                        notifyItemChanged(position)

                    }
                selector.select(tag,tag.selected,position)

            }

        }
    }

    override fun getItemCount(): Int = tags.size

    fun  addTag(tag:Tag){
        tags.add(tag);
        notifyItemInserted(tags.size);
    }
    fun modify(tag:Tag){
        var i=tags.indexOf(tag);
        if (i!=-1){
            tags.set(i,tag);
            notifyItemChanged(i);
        }
    }
    fun  delete(tag: Tag){
        var i=tags.indexOf(tag);
        if (i!=-1){
            tags.removeAt(i)
            notifyItemRemoved(i);
        }
    }
}