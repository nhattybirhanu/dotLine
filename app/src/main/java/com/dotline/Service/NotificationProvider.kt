package com.dotline.Service

import android.content.Context
import android.util.Log
import com.dotline.callbacks.UserProfileCallBack
import com.dotline.model.BlogContent
import com.dotline.model.Profile
import com.dotline.provider.UserProfileProvider
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class NotificationProvider(var notificationCallBack: NotificationCallBack?, var context: Context?) {
    var user: FirebaseUser? = null
    var dateformat= SimpleDateFormat(", MMM d,yyyy")

    var firebaseAuth: FirebaseAuth?
    private var userProfile: Profile? = null

    private val authStateListener: FirebaseAuth.AuthStateListener =
        FirebaseAuth.AuthStateListener { firebaseAuth ->
            user = firebaseAuth.getCurrentUser()
            if (user != null) {
                val documentReference: DocumentReference =
                    FirebaseFirestore.getInstance().collection("Users").document(user!!.getUid())
                documentReference.get()
                    .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot?> {
                        override fun onSuccess(documentSnapshot: DocumentSnapshot?) {
                            if (documentSnapshot != null &&documentSnapshot.getData()!=null) {
                                userProfile = Profile.instance(
                                    documentSnapshot.data!!,
                                    documentReference.id
                                )
                                Log.i("Notification","user profile")

                                lookAnswered()

                            }
                        }
                    })
            } else destroy()
        }

    fun destroy() {
        firebaseAuth?.removeAuthStateListener(authStateListener)
        if (notificationCallBack != null) notificationCallBack!!.clear()
    }
        fun lookAnswered(){
            var id: String? = user?.uid;
            if (id==null) return
            val database = FirebaseDatabase.getInstance().reference.child("Answers");
            database.orderByChild("que_owner").equalTo(id).limitToLast(3)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var contents = arrayListOf<BlogContent>()
                        for (doc in snapshot.children) {
                            var answer =
                                BlogContent.blog(false, doc.key!!, doc.value as Map<String, Any>);
                            contents.add(answer);
                        }
                        Log.i("Notification","user answered ${contents.size}")

                        prepareNotifcation(contents,NotificationType.ANSWER)

                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }

    fun prepareNotifcation(blogs: List<BlogContent>, notificationType: NotificationType?) {
            notificationCallBack!!.done(blogs.size)
        for (blog in blogs) {
            Log.i("Notification","blog ${blog}")

            UserProfileProvider.MyInstance().userProfile(blog.owner,false,object :
                UserProfileCallBack() {
                override fun profile(profile: Profile) {
                    val notification = Notification(
                        blog.id,
                        profile.displayName,
                        profile.profile_picture,
                        blog.body,
                        false,
                        dateformat.format(blog.date*1000),
                        true,
                        notificationType
                    )
                    notificationCallBack!!.notify(notification!!)

                    super.profile(profile)
                }
            })
            }

    }


    init {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth!!.addAuthStateListener(authStateListener)
        Log.i("Notification","init")

}
}