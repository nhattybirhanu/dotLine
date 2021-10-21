package com.dotline.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dotline.MainActivity
import com.dotline.R
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.HashMap
import kotlin.coroutines.resumeWithException

class NotificationService(var context: Context, workerParams: WorkerParameters) :
  ListenableWorker(context, workerParams), NotificationCallBack {
    private var nCoutn: Int=0;
    var notificationProvider: NotificationProvider? = null
    var notificationManager: NotificationManager? = null
    var name = "dotLine"
    var group_notification = "GROUP_NOTIFICATION"
    var id = "1002"
    var notification_id = 100
    var notificationHashMap: HashMap<String, android.app.Notification>? = null
    var networkListener: NetworkListener? = null
    var connected = false
    var time: Long = 0
    private val summary_id = 1010
    lateinit var future:SettableFuture<ListenableWorker.Result>;
    fun onCreate() {
        initializeNotification()
         notificationProvider =
            NotificationProvider(this@NotificationService, context)
        Log.i("Notification","init from")

        if (notificationHashMap == null) notificationHashMap = HashMap()
        networkListener = object : NetworkListener(context) {
            override fun networkConnection(context: Context?, connected: Boolean, mobile: Boolean) {
                //if (time<System.currentTimeMillis()- Time.onehour)
                notificationManager!!.cancelAll()
                super.networkConnection(context, connected, mobile)
            }
        }
        run {
         }
        if (connected) {


        } else {
            for (id in notificationHashMap!!.keys) {
                val notification = notificationHashMap!![id]
                if (notification != null) {
                    val bundle: Bundle = notification.extras
                    notificationManager!!.notify(id, bundle.getInt("id", 1), notification)
                }
            }
        }
        time = System.currentTimeMillis()

    }

    fun initializeNotification() {
        notificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
    }


//    override suspend fun doWork(): Result {
//
//        try {
//
//            suspendCancellableCoroutine<Int> { cancellableContinuation ->
//                // Here you can call your asynchronous callback based network
//
//                fun onComplete() {
//                    cancellableContinuation.resumeWith(
//                        kotlin.Result.success(100))
//                }
//
//                fun onError(error: Error?) {
//
//                    cancellableContinuation.resumeWithException(
//                         Throwable()
//                    )
//
//                }
//
//            }
//
//        }catch (e: Exception) {
//            return Result.failure()
//        }
//        return Result.success()
//    }


    fun onDestroy() {
        if (notificationProvider != null) notificationProvider!!.destroy()
    }

   override fun notify(notification: Notification?) {
        createNotification(notification)
        if (notification != null && !notificationHashMap!!.containsKey(notification.id)) {
        } else if (notification != null) {
            showSavedNot(notification)
        }
    }

   override fun clear() {
        notificationManager!!.cancelAll()
        notificationHashMap!!.clear()
    }

    override fun done(count:Int) {
        nCoutn=count;

    }

    fun showAsGroup() {
        if (notificationHashMap == null) return
        val summaryNotification: android.app.Notification.Builder = android.app.Notification.Builder(
            context
        )
            .setContentTitle("You got answers") //set content text to support devices running API level < 24
            .setContentText(notificationHashMap!!.size.toString() + " messages from dotLine")
            .setSmallIcon(R.drawable.ic_dot) //build summary info into InboxStyle template
            //specify which group this notification belongs to
            .setGroup(group_notification) //set this notification as the summary for the group
            .setGroupSummary(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            summaryNotification.setChannelId(id)
        }
        val intent = Intent(getApplicationContext(), MainActivity::class.java)
        intent.setAction(ACTION_NOTIFICATION_ALL)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            getApplicationContext(),
            notification_id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        summaryNotification.setContentIntent(pendingIntent)
        notificationManager!!.notify(summary_id, summaryNotification.build())
    }

    fun createNotification(notification: Notification?) {
        val builder = android.app.Notification.Builder(getApplicationContext())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(id)
        }
        val bundle = Bundle()
        bundle.putInt("id", notification_id)
        builder.extras = bundle
        builder.setContentTitle(notification!!.displayName)
        builder.setContentText(notification!!.description)
        builder.setGroup(group_notification)
        builder.setSmallIcon(R.drawable.ic_dot)
    //    builder.setSubText(if (notification.getNotificationType() === NotificationType.DAILY) "Daily feeds" else if (notification.getNotificationType() === NotificationType.FOLLOWING) "For you" else "New Albums")
        Glide.with(getApplicationContext()).asBitmap().load(notification.image).circleCrop()
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    builder.setLargeIcon(resource)
                    val intent = Intent(getApplicationContext(), MainActivity::class.java)
                    intent.putExtra("notification", notification)
                    intent.setAction(ACTION_NOTIFICATION)
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(
                        getApplicationContext(),
                        notification_id,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    builder.setContentIntent(pendingIntent)
                    val notification1 = builder.build()
                    notificationHashMap!!.put(notification.id!!,  notification1)
                    notificationManager!!.notify(notification.id, notification_id, notification1)
                    notification_id++
                    if (notificationHashMap != null && notificationHashMap!!.size > 2) showAsGroup()
                    if (nCoutn==notificationHashMap!!.size) future.set(ListenableWorker.Result.success());
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    fun showSavedNot(notification: Notification) {
        val not = notificationHashMap!![notification!!.id]
        if (not != null) {
            val bundle: Bundle = not.extras
            notificationManager!!.notify(notification!!.id, bundle.getInt("id", 1), not)
        }
    }

    companion object {
        var ACTION_NOTIFICATION = "ACTION_NOTIFICATION"
        var ACTION_NOTIFICATION_ALL = "ACTION_NOTIFICATION_ALL"
    }

    init {
       // onCreate()
    }

    override fun startWork(): ListenableFuture<Result> {
        future=SettableFuture.create<ListenableWorker.Result>()
        onCreate()
        Log.i("JobT", "Started")
return  future;
    }
}