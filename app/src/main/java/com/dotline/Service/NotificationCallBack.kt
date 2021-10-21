package com.dotline.Service


interface NotificationCallBack {
    fun notify(notification: Notification?)
    fun clear()
    fun done( count:Int);
}