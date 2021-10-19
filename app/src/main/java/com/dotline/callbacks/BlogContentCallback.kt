package com.dotline.callbacks

import com.dotline.model.BlogContent
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.StorageMetadata

open class BlogContentCallback {
    open fun contentsResult(blogsContent:ArrayList<BlogContent>?){}
   open fun singleResult(blog:BlogContent){}
    open fun lastdocument(document:DocumentSnapshot){}
}