package com.dotline.provider

import com.dotline.callbacks.BlogContentCallback
import com.dotline.callbacks.MetaDataCallBack
import com.dotline.callbacks.UserProfileCallBack
import com.dotline.model.BlogContent
import com.dotline.model.Profile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata

class SearchProvider() {
    private var allquestons=HashMap<String,BlogContent>();
   private
   var questionsForUsers=HashMap<String,ArrayList<BlogContent>>()
    companion object{

     var instance=SearchProvider();

        fun MyInstance():SearchProvider{
            return  instance;
        }



    }

    fun setQuestion(id:String,blogContent: BlogContent){
        allquestons.put(id,blogContent)
    }

    fun searchByFilter(tags:ArrayList<String>,date:Long,callback: BlogContentCallback,lastDoc: DocumentSnapshot?){
        var questionCollection=FirebaseFirestore.getInstance().collection("Questions")
        var query:Query=questionCollection;
      if (tags.isNotEmpty()){

         query = questionCollection.whereArrayContainsAny("tags",tags)
      }
        if (date>-1){
            query=query?.whereGreaterThanOrEqualTo("date",date/1000);
        }
        if (lastDoc!=null){
           query= query.startAfter(lastDoc)
        }
        query.limit(1).get().
            addOnSuccessListener { value->

                var list= arrayListOf<BlogContent>();
                for (doc in value!!){
                    var blog= doc.data.let { BlogContent.blog(true,doc.id, it) }
                    setQuestion(blog.id,blog);
                    list.add(blog)
                }
                callback.contentsResult(list)
                if (list.isNotEmpty()) callback.lastdocument(value.documents.get(value.count()-1))

        }
    }
    fun searchUsers(key:String,callback:UserProfileCallBack,lastDoc: DocumentSnapshot?){
        var questionCollection=FirebaseFirestore.getInstance().collection("Users")
        var query=questionCollection.whereGreaterThanOrEqualTo("username","$key")
            .whereLessThanOrEqualTo("username","$key~")
        if (lastDoc!=null){
            query=query.startAfter(lastDoc);
        }
        query.limit(1).get().addOnSuccessListener {result->
            var list= arrayListOf<Profile>();
            if (result!=null) {
                for (doc in result!!) {
                   var profile=Profile.instance(doc.data,doc.id)
                    list.add(profile)
                }
                if (!result.isEmpty)
                callback.lastdocument(result.documents.get(result.count()-1))
                callback.profiles(list)
            }
        }
    }
    fun searchFullTextBlog(text:String,callback:BlogContentCallback,last_doc:DocumentSnapshot?){
        var questionCollection=FirebaseFirestore.getInstance().collection("Questions")
        var query:Query=questionCollection;
        if (text.isNotEmpty()){
            query = questionCollection.whereGreaterThanOrEqualTo("title", text)
                .whereLessThanOrEqualTo("title","$text~");
            if (last_doc!=null){
                query= query.startAfter(last_doc)
            }

            query.limit(1).get().
            addOnSuccessListener { value->

                var list= arrayListOf<BlogContent>();
                for (doc in value!!){
                    var blog= doc.data.let { BlogContent.blog(true,doc.id, it) }
                    setQuestion(blog.id,blog);
                    list.add(blog)
                }
                callback.contentsResult(list)
                if (list.isNotEmpty()) callback.lastdocument(value.documents.get(value.count()-1))

            }}



    }




}

