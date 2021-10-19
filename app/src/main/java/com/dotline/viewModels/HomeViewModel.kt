package com.dotline.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dotline.model.BlogContent
import com.dotline.model.Profile
import com.dotline.provider.QuestionProvider
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class HomeViewModel(application: Application) : AndroidViewModel(application) {
   lateinit var registration:ListenerRegistration;
    private var blogs: MutableLiveData<List<BlogContent>?> = MutableLiveData()
    init {
    load();
    }
    fun load(){
        var collection=FirebaseFirestore.getInstance().collection("Questions")
       registration= collection.orderBy("date",Query.Direction.DESCENDING).addSnapshotListener(EventListener { value, error ->
           if (error!=null){
               return@EventListener;
           }
                    var list= arrayListOf<BlogContent>();
                for (doc in value!!){
                    var blog= doc.data.let { BlogContent.blog(true,doc.id, it) }
                    QuestionProvider.MyInstance().setQuestion(blog.id,blog);
                      list.add(blog)
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

}