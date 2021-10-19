package com.dotline.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dotline.model.Profile
import com.dotline.model.Profile.Companion.instance
import com.dotline.provider.UserProfileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class UserProfileModel(application: Application) : AndroidViewModel(application) {
    private var profile_data: MutableLiveData<Profile?> = MutableLiveData()
    var listenerRegistration: ListenerRegistration? = null
    private val authStateListener: AuthStateListener = AuthStateListener {
        val user = FirebaseAuth.getInstance().currentUser
        Log.i("Shuffle", "Account " + (user == null))
        if (user == null) {
            profile_data.postValue(null)
        } else if (listenerRegistration == null) {
            val documentReference =
                FirebaseFirestore.getInstance().collection("Users").document(user.uid)
            listenerRegistration =
                documentReference.addSnapshotListener(EventListener { value, error ->
                    Log.i("Shuffle", "Account data " + (value == null) + user.uid)
                    if (value != null&&value.data!=null) {
                        val profile = instance(
                            (value.data)!!, value.id
                        )
                        profile_data.postValue(profile)
                        if (!profile.verfied&&user.isEmailVerified){
                            documentReference.update("verified",true);
                        }
                        UserProfileProvider.MyInstance().profiles.put(profile.id,profile);
                    } else if (error != null) {
                        Log.i("UserAc", error.localizedMessage)
                    }
                })
        }
    }

    fun profileLiveData(): LiveData<Profile?> {
        return profile_data
    }

    override fun onCleared() {
        super.onCleared()
        if (listenerRegistration != null) listenerRegistration!!.remove()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

    init {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }
}