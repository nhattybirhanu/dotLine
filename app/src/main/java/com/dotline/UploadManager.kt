package com.dotline

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class UploadManager {
    companion object{
        var IMAGE:String="images";
        var FILE:String="files";
        var CODE:String="codes";
        var PROFILE:String="profile";
        fun upload_ProfilePicture(path:String,callback:Task){
            var storageRef= FirebaseStorage.getInstance().reference.child("Profile_Picture")
            var file=File(path);
            var uri = Uri.fromFile(file)
            val filename: String = file.getName()

            val refernce = storageRef.child(filename);
            var uploadTask = refernce.putFile(uri)
            uploadTask.addOnFailureListener {
                callback.taskisdone(false, hashMapOf());
                // Handle unsuccessful uploads
            }.addOnSuccessListener { taskSnapshot ->
                refernce.downloadUrl.addOnSuccessListener { result->
                   val resultData=HashMap<String,ArrayList<String>>();
                   resultData.put(PROFILE, arrayListOf(result.toString()))
                   callback.taskisdone(true,resultData)
                }
            }




        }
    }
    var allresults=HashMap<String,ArrayList<String>>();
    var root:String?=null
    var images:ArrayList<String>
    var files:ArrayList<String>
    var codes:ArrayList<String>
    var task:Task

    constructor(
        root: String,
        images: ArrayList<String>,
        files: ArrayList<String>,
        codes: ArrayList<String>,
        task: Task
    ) {
        this.root = root
        this.images = images
        this.files = files
        this.codes = codes
        this.task = task
    call(null, arrayListOf(),root)
    }

    interface Task{
        fun  taskisdone(done:Boolean,data:Map<String,ArrayList<String>>)
    }


    fun call(tag: String?,result:ArrayList<String>,root: String){
        if (tag!=null) allresults.put(tag,result)
        if (!images.isEmpty()){
            uploadData( IMAGE ,root, images )
            images.clear()
        } else if (!files.isEmpty()){
                uploadData( FILE ,root, files )
            files.clear()
        } else if (!codes.isEmpty()){
            uploadData( CODE,root, codes)
            codes.clear();

        } else{
            task.taskisdone(true,allresults)
            Log.i("Upload","data back to editor")
        }
    }





    fun uploadData(tag:String,folder:String,files:ArrayList<String>){
        var uploadedUri=ArrayList<String>()
        var storageRef= FirebaseStorage.getInstance().reference.child(folder)
        var  count=0;
        var all=files.size;
        for((index,filepath:String) in files.withIndex()){
            var file=File(filepath);
            var uri = Uri.fromFile(file)
            val filename: String = file.getName()

            val refernce = storageRef.child(tag).child(filename);
            var uploadTask = refernce.putFile(uri)
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener { taskSnapshot ->
                refernce.downloadUrl.addOnSuccessListener { result->
                    uploadedUri.add(result.toString())
                    count++;
                    Log.i("Upload","Uploading done $index ${all} $count")
                    if (count==all){
                        Log.i("Upload","all Uploading done $index")

                        call(tag,uploadedUri,folder)
                    }
                }
            }  }



    }


}