package com.dotline.callbacks

import com.dotline.model.Tag

interface Selected {
    fun select(objects: Tag, selected: Boolean, poistion:Int)
    fun done();
}