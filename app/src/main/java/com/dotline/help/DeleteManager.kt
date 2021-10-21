package com.dotline.help

import android.os.Environment
import android.util.Log
import com.dotline.model.BlogContent
import com.dotline.model.FileModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class DeleteManager {
    companion object{
        fun deleteBLog(blogContent: BlogContent){
            if (blogContent.question){
              var doc= FirebaseFirestore.getInstance().collection("Questions").document(blogContent.id)
                doc.delete()

            } else
            {
                var database= FirebaseDatabase.getInstance().reference.child("Answers");
                database.orderByChild("que_id").equalTo(blogContent.question_id).
                addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (doc in snapshot.children){
                          doc!!.ref.removeValue()
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
            }
            if (blogContent.images.isNotEmpty()) deleteFile(blogContent.images)
            if (blogContent.files.isNotEmpty()) deleteFile(blogContent.files)

        }
        fun deleteFile(urls:ArrayList<String>){
            for (url in urls){
                var storage= FirebaseStorage.getInstance().getReferenceFromUrl(url);
                storage.delete();

            }
        }
    }

}