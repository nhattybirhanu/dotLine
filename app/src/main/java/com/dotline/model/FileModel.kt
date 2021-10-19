package com.dotline.model

import com.google.firebase.storage.StorageMetadata

class FileModel {
    lateinit var metaData:StorageMetadata;
    var  type:Int=2;

    constructor(metaData: StorageMetadata, type: Int) {
        this.metaData = metaData
        this.type = type
    }

    companion object{
        var FILE:Int=2;
        var CODE:Int=3;
    }
}