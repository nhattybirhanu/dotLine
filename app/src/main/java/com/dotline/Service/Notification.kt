package com.dotline.Service

import com.dotline.Service.NotificationType
import java.io.Serializable

class Notification : Serializable {
    var id: String? = null
    var displayName:String?=null;
    var image: String? = null
    var title: String? = null
    var description: String? = null
    var isOpened = false
    var date: String? = null
    var created: Long = 0
    var isSave = false
    var notificationType: NotificationType? = null

    constructor(
        id: String?,
        displayName: String?,
        image: String?,
        description: String?,
        isOpened: Boolean,
        date: String?,
        isSave: Boolean,
        notificationType: NotificationType?
    ) {
        this.id = id
        this.displayName = displayName
        this.image = image
        this.description = description
        this.isOpened = isOpened
        this.date = date
        this.isSave = isSave
        this.notificationType = notificationType
    }

    constructor()

}