package com.dotline.callbacks

import com.dotline.model.FileModel
import com.google.firebase.storage.StorageMetadata

interface MetaDataCallBack {
    fun metaResult(file: FileModel?)
}