package com.dotline.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dotline.R
import com.dotline.model.FileModel
import kotlinx.android.synthetic.main.file_download_item.view.*

class RemoteFileAdapter(var files:ArrayList<FileModel>): RecyclerView.Adapter<RemoteFileAdapter.FileModelHolder>() {
    inner class FileModelHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):FileModelHolder {
        return FileModelHolder(LayoutInflater.from(parent.context).inflate(R.layout.file_download_item,parent,false));
    }

    override fun onBindViewHolder(holder: FileModelHolder, position: Int) {
        var item=files.get(position);
        holder.itemView.apply {
            name.setCompoundDrawablesRelativeWithIntrinsicBounds(if (item.type==FileModel.FILE) R.drawable.ic_file else R.drawable.ic_code,0,0,0)
            name.text=(item.metaData.name)
        }
    }

    override fun getItemCount(): Int {
        return  files.size
    }
    fun  add(item:FileModel){
        files.add(item);
        notifyItemInserted(files.size)
    }
    fun update(list:ArrayList<FileModel>){
        files.clear();
        files.addAll(list)
        notifyDataSetChanged()
    }
}