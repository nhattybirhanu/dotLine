package com.dotline.provider

import com.dotline.callbacks.UserProfileCallBack
import com.dotline.model.Profile
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileProvider() {
    var profiles=HashMap<String,Profile>();
    companion object  {

     var instance=UserProfileProvider();

        fun MyInstance():UserProfileProvider{
            return  instance;
        }



    }
    fun userProfile(id:String,update:Boolean,callback:UserProfileCallBack){

        if (profiles.containsKey(id)) profiles.get(id)?.let { callback.profile(it) }
        else {
            var  document=FirebaseFirestore.getInstance().collection("Users").document(id);
            document. get().addOnSuccessListener { profileDoc->
                if (profileDoc.data!=null){
                    var profile=Profile.instance(profileDoc.data!!,profileDoc.id)
                    profiles.put(profile.id,profile);
                    callback.profile(profile)
                }


            }
        }
    }
    fun getFollowers(id:String,callback:UserProfileCallBack){
        var  collection=FirebaseFirestore.getInstance().collection("Users");
        var query=collection.whereArrayContains("following",id);
        query.get().addOnSuccessListener {
            value->
            if (value!=null){
                var profileList= arrayListOf<Profile>()
                for(profileDoc in value){
                    val profile=Profile.instance(profileDoc.data,profileDoc.id)
                    profiles.put(profile.id,profile);
                    profileList.add(profile);
                }
                callback.profiles(profileList)

            }
        }


    }
    fun getFollowing(user_id:ArrayList<String>,callback:UserProfileCallBack){
        var  collection=FirebaseFirestore.getInstance().collection("Users");
        var query=collection.whereIn(FieldPath.documentId(),user_id);
        query.get().addOnSuccessListener {
                value->
            if (value!=null){
                var profileList= arrayListOf<Profile>()
                for(profileDoc in value){
                    val profile=Profile.instance(profileDoc.data,profileDoc.id)
                    profiles.put(profile.id,profile);
                    profileList.add(profile);
                }
                callback.profiles(profileList)

            }
        }


    }
    fun manageFollowUser(uid:String,follow_id:String,follow:Boolean){
        var  document=FirebaseFirestore.getInstance().collection("Users").document(uid);
            document.update("following",if(follow)FieldValue.arrayUnion(follow_id) else FieldValue.arrayRemove(follow_id))
        var ownerdoc=FirebaseFirestore.getInstance().collection("Users").document(follow_id);
        ownerdoc.update("followers",if(follow)FieldValue.increment(1) else FieldValue.increment(-1))
    }


}