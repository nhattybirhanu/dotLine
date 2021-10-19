package com.dotline.callbacks

import com.google.firebase.storage.StorageMetadata

interface MetaDataCallBack {
    fun metaResult(metadata: StorageMetadata?)
}