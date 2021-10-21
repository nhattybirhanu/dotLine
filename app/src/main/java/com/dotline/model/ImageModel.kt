package com.dotline.model

import android.media.Image
import android.net.Uri
import java.io.Serializable

class ImageModel:Serializable  {
    var src:Any;
    var remote=false;
    constructor(src:Any){
        this.src=src;
    }

    constructor(src: Any, remote: Boolean) {
        this.src = src
        this.remote = remote
    }

    companion object{
        fun build(image :ArrayList<Any>):ArrayList<ImageModel>{
            val images=ArrayList<ImageModel>();
            image.forEach { images.add(ImageModel(it)) }
            return images;
        }

        fun fromString(images: ArrayList<String>): ArrayList<ImageModel> {
            var imagesV= arrayListOf<ImageModel>();
            images.forEach { imagesV.add(ImageModel(it)) }
            return imagesV;
        }
        fun remotesrc(remote:ArrayList<String>):ArrayList<ImageModel>{
            var imagesV= arrayListOf<ImageModel>();
            remote.forEach { imagesV.add(ImageModel(it,true)) }
            return imagesV;

        }
    }

    override fun toString(): String {
        return src.toString()
    }
}