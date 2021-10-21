package com.dotline.adapter

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.dotline.R
import com.dotline.model.FileModel
import kotlinx.android.synthetic.main.file_download_item.view.*


class RemoteFileAdapter(var files:ArrayList<FileModel>,var activity:AppCompatActivity): RecyclerView.Adapter<RemoteFileAdapter.FileModelHolder>() {
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
            if(item.file!=null)
                download.visibility=View.GONE;
            else {
                download.setOnClickListener {
                    download(item)
                    download.visibility=View.GONE;
                }
            }
            setOnClickListener {
                if(item.file==null)
                {
                    download(item)
                } else{
                    download.visibility=View.GONE;
                    openDocument(item)

                }
            }
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
    fun download(fileModel: FileModel){

        val request = DownloadManager.Request(Uri.parse(fileModel.remotePath))
        request.setTitle("${fileModel.metaData.name}")

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS,
            "dotLine/${fileModel.metaData.name}"
        )

// get download service and enqueue file

// get download service and enqueue file
        val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        manager!!.enqueue(request)
    }
    fun openDocument(fileModel: FileModel) {
        val intent = Intent(Intent.ACTION_VIEW)
        val extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(fileModel.file).toString())
        val mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        if (extension.equals("", ignoreCase = true) || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the fileModel.file
            intent.setDataAndType(Uri.fromFile(fileModel.file), "text/*")
        } else {
            intent.setDataAndType(Uri.fromFile(fileModel.file), mimetype)
        }

        // custom message for the intent
        activity.startActivity(Intent.createChooser(intent, fileModel.metaData.name))
    }
}