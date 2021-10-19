package com.dotline.model

import android.content.Context
import com.bumptech.glide.Glide

class ImageConfig {
    var height:Int=100;
    var width:Int=100;
    var size=100;
    var circle=true;
    var closeButton:Boolean=false;

    constructor(size: Int, circle: Boolean) {
        this.size = size
        this.circle = circle
        height=size;
        width=size;
    }

    constructor(height: Int, width: Int, circle: Boolean, closeButton: Boolean) {
        this.height = height
        this.width = width
        this.circle = circle
        this.closeButton = closeButton
    }


}