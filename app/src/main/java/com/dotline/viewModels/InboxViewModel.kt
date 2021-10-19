package com.dotline.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dotline.model.BlogContent
import com.dotline.model.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class InboxViewModel(application: Application) : AndroidViewModel(application) {
     var inboxLiveData: MutableLiveData<ArrayList<BlogContent>> = MutableLiveData()
    lateinit var  childLis:ChildEventListener;
    var inobxSet=HashMap<String,BlogContent>()

    init {
    load();
    }
    fun load(){
        val user=FirebaseAuth.getInstance().currentUser;
        if (user!=null){
             childLis=object:ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    var blog=BlogContent.blog(false, snapshot.key!!, snapshot.value as Map<String, Any>)
                    inobxSet.put(blog.id,blog)
                    inboxLiveData.postValue(ArrayList(inobxSet.values))
                    Log.i("inbox",blog.toString())
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    var blog=BlogContent.blog(false, snapshot.key!!, snapshot.value as Map<String, Any>)
                    inobxSet.put(blog.id,blog)

                    inboxLiveData.postValue(ArrayList(inobxSet.values))
                    Log.i("inbox",blog.toString())
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            }
            val databaseOne= FirebaseDatabase.getInstance().reference.child("Answers");
            databaseOne.orderByChild("que_owner")
                .equalTo(user.uid)
                .addChildEventListener(childLis)
            val databasetwo= FirebaseDatabase.getInstance().reference.child("Answers");
            databasetwo.orderByChild("owner").equalTo(user.uid).addChildEventListener(childLis)
        }

    }

    override fun onCleared() {
        super.onCleared()

    }


    fun liveInbox():LiveData<ArrayList<BlogContent>>{
        return  inboxLiveData
    }

}