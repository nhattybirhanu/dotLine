package com.dotline.provider

import android.util.Log
import com.dotline.callbacks.BlogContentCallback
import com.dotline.callbacks.MetaDataCallBack
import com.dotline.model.BlogContent
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata

class QuestionProvider() {
    private var allquestons=HashMap<String,BlogContent>();
   private
   var questionsForUsers=HashMap<String,ArrayList<BlogContent>>()
    companion object{

     var instance=QuestionProvider();

        fun MyInstance():QuestionProvider{
            return  instance;
        }



    }

    fun setQuestion(id:String,blogContent: BlogContent){
        allquestons.put(id,blogContent)
    }
    fun getQuestion(id:String,source: Source,callback:BlogContentCallback){
     if (allquestons.containsKey(id)) callback.singleResult(allquestons.get(id)!!); else {
         var collection = FirebaseFirestore.getInstance().collection("Questions")
            collection.document(id).get(source).addOnSuccessListener {
                snapshot->
                var blog=BlogContent.blog(true,snapshot.id, snapshot.data!!);
                callback.singleResult(blog)
                allquestons.put(id,blog)


            }

     }

        }

    fun getQuestionForUser(uid:String,callback: BlogContentCallback){
        if (questionsForUsers.containsKey(uid))
        {
            callback.contentsResult(questionsForUsers.get(uid))
            Log.i("Complete", " complete called")

        }  else {
            var collection=FirebaseFirestore.getInstance().collection("Questions")
           var query= collection.whereEqualTo("owner",uid)
               //.orderBy("date", Query.Direction.DESCENDING);
            query.get().
                    addOnSuccessListener { p0 ->
                        var list = arrayListOf<BlogContent>();
                        Log.i("Result", " question provider for user ${list}")
                        for (doc in p0.documents) {
                            var blog = doc.data.let { BlogContent.blog(true, doc.id, it!!) }
                            setQuestion(blog.id, blog);
                            list.add(blog)
                        }

                        questionsForUsers.put(uid, list)
                        callback.contentsResult(list)
                    }.addOnCompleteListener {

            }

        }


    }
    fun  getSavedQuestions(tags:ArrayList<String>,blogContentCallback: BlogContentCallback){
        if(tags.isEmpty()) return
        var collection=FirebaseFirestore.getInstance().collection("Questions")
        collection.whereIn(FieldPath.documentId(),tags)
            .get().
        addOnSuccessListener(object:OnSuccessListener<QuerySnapshot>{
            override fun onSuccess(p0: QuerySnapshot?) {
                p0?.let { handleCallback(it,blogContentCallback) }

            }
        })


    }
    fun  handleCallback(value:QuerySnapshot,callback: BlogContentCallback){
        var list= arrayListOf<BlogContent>();
        for (doc in value!!){
            var blog= doc.data.let { BlogContent.blog(true,doc.id, it) }
            setQuestion(blog.id,blog);
            list.add(blog)
        }
        callback.contentsResult(list)
    }


}

