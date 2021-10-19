package com.dotline.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dotline.R
import com.dotline.model.Selector
import kotlinx.android.synthetic.main.text_selector.view.*

class TextSelectorAdapter(var selector:ArrayList<Selector>): RecyclerView.Adapter<TextSelectorAdapter.TextSelectorHolder>() {
    inner class TextSelectorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextSelectorHolder {
        return  TextSelectorHolder(LayoutInflater.from(parent.context).inflate(R.layout.text_selector,parent,false));
    }

    override fun onBindViewHolder(holder: TextSelectorHolder, position: Int) {
        var item=selector.get(position);
        holder.itemView.apply {
            title.text=(item.title)
            close.setOnClickListener {
                selector.removeAt(position);
                notifyItemRemoved(position)

            }
        }
    }

    override fun getItemCount(): Int {
        return  selector.size
    }
    fun update(list:ArrayList<Selector>){
        selector.clear();
        selector.addAll(list)
        notifyDataSetChanged()
    }
    fun add(select: Selector){
        selector.add(select);
        notifyItemInserted(selector.size)
    }

    fun clear() {
        selector.clear();
        notifyDataSetChanged()
    }

    fun addAll(newselectors: java.util.ArrayList<Selector>) {
        var oldS=selector.size;
        selector.addAll(newselectors)
        notifyItemRangeInserted(oldS,selector.size)
    }
}