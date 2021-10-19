package com.dotline.provider

import com.dotline.callbacks.MetaDataCallBack
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata

class RemoteFileProvider() {
    var metas=HashMap<String,StorageMetadata>();
    companion object{

     var instance=RemoteFileProvider();

        fun MyInstance():RemoteFileProvider{
            return  instance;
        }



    }


    fun fileMetaData(url:String,callback:MetaDataCallBack){
        if (metas.containsKey(url)) callback.metaResult(metas.get(url));
        else {
            var storage=FirebaseStorage.getInstance().getReferenceFromUrl(url);
            storage.metadata.addOnSuccessListener { metadata->
                metas.put(url,metadata)
                callback.metaResult(metadata);

            } }
        }

    }

