package com.dotline.provider

import com.dotline.callbacks.BlogContentCallback
import com.dotline.callbacks.MetaDataCallBack
import com.dotline.model.BlogContent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata

class AnswerProvider() {
   private var answers=HashMap<String,ArrayList<BlogContent>>();
    private var answersFromUsers=HashMap<String,ArrayList<BlogContent>>();
    companion object{

     var instance=AnswerProvider();

        fun MyInstance():AnswerProvider{
            return  instance;
        }



    }


    fun getAnswers(id:String,callback:BlogContentCallback){
       // if (answers.containsKey(id)) callback.contentsResult(answers.get(id)); else

           var database=FirebaseDatabase.getInstance().reference.child("Answers");
            database.orderByChild("que_id").equalTo(id).
            addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var contents= arrayListOf<BlogContent>()
                    for (doc in snapshot.children){
                        var answer=BlogContent.blog(false, doc.key!!, doc.value as Map<String, Any>);
                        contents.add(answer);
                    }
                    answers.put(id,contents);
                    callback.contentsResult(contents)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        }
    fun getAnswersForUser(id:String,callback:BlogContentCallback){
         if (answersFromUsers.containsKey(id)) callback.contentsResult(answersFromUsers.get(id));
         else {
             val database = FirebaseDatabase.getInstance().reference.child("Answers");
             database.orderByChild("owner").equalTo(    id)
                 .addListenerForSingleValueEvent(object : ValueEventListener {
                     override fun onDataChange(snapshot: DataSnapshot) {
                         var contents = arrayListOf<BlogContent>()
                         for (doc in snapshot.children) {
                             var answer =
                                 BlogContent.blog(false, doc.key!!, doc.value as Map<String, Any>);
                             contents.add(answer);
                         }
                         answersFromUsers.put(id, contents);
                         callback.contentsResult(contents)
                     }

                     override fun onCancelled(error: DatabaseError) {
                     }

                 })
         }
    }



}

