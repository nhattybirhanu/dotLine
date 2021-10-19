package com.dotline.model

import android.media.Image
import android.net.Uri
import java.io.Serializable

class ImageModel:Serializable  {
    var src:Any;

    constructor(src:Any){
        this.src=src;
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
    }

    override fun toString(): String {
        return src.toString()
    }
}