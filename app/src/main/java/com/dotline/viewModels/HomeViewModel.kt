package com.dotline.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dotline.model.BlogContent
import com.dotline.model.Profile
import com.dotline.provider.QuestionProvider
import com.dotline.provider.UserProfileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {
   lateinit var registration:ListenerRegistration;
    private var blogs: MutableLiveData<List<BlogContent>?> = MutableLiveData()
    init {
    load();
    }
    fun load(){
        var collection=FirebaseFirestore.getInstance().collection("Questions")
    //   whereArrayContainsAny("tags",userProfile().tags)
             //.whereArrayContainsAny("following",userProfile().following)
           //.whereArrayContainsAny("mentions",userProfile().mentions).
        var query:Query=  collection.orderBy("date",Query.Direction.DESCENDING);
            registration=query. addSnapshotListener(EventListener { value, error ->
           if (error!=null){
               return@EventListener;
           }
                    var list= arrayListOf<BlogContent>();
                for (doChange in value!!.documentChanges){
                    Log.i("blog",doChange.toString())
                    if (doChange.type==DocumentChange.Type.ADDED) {
                        var doc = doChange.document;
                        var blog = doc.data.let { BlogContent.blog(true, doc.id, it) }
                        QuestionProvider.MyInstance().setQuestion(blog.id, blog);
                        list.add(blog)
                    }

                }
           blogs.postValue(list);


        })
    }

    override fun onCleared() {
        super.onCleared()
        if(registration!=null) registration.remove()
    }


    fun blogs():LiveData<List<BlogContent>?>{
        return  blogs
    }
    fun userProfile():Profile{
        val user=FirebaseAuth.getInstance().currentUser;
        return UserProfileProvider.MyInstance().profiles.get(user!!.uid)!!;
    }

}