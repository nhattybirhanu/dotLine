package com.dotline.model

import com.google.firebase.storage.StorageMetadata
import java.io.File

class FileModel {
    var metaData:StorageMetadata;
    var file:File?=null;
    var  type:Int=2;
     var remotePath:String;

    constructor(metaData: StorageMetadata, type: Int, remotePath: String) {
        this.metaData = metaData
        this.type = type
        this.remotePath = remotePath
    }


    companion object{
        var FILE:Int=2;
        var CODE:Int=3;
    }
}