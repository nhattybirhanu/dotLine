package com.dotline.provider

import android.os.Environment
import android.util.Log
import com.dotline.callbacks.MetaDataCallBack
import com.dotline.model.FileModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import java.io.File

class RemoteFileProvider() {
    var metas=HashMap<String,FileModel>();
    companion object{

     var instance=RemoteFileProvider();

        fun MyInstance():RemoteFileProvider{
            return  instance;
        }



    }


    fun fileMetaData(url:String,callback:MetaDataCallBack){

            var storage=FirebaseStorage.getInstance().getReferenceFromUrl(url);
            storage.metadata.addOnSuccessListener { metadata->
                var filemode=FileModel(metadata,FileModel.FILE,url);
                filemode.remotePath=url;
                var file=File(Environment.getExternalStorageDirectory().absolutePath+"/${Environment.DIRECTORY_DOCUMENTS}/dotLine/${metadata.name}");
                    if (file.exists()){
                        filemode.file=file;
                        Log.i("File","Exist")
                    }
                Log.i("File","${file.name}")

                        metas.put(url,filemode)
                callback.metaResult(filemode);

            } }

    }

