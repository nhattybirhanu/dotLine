package com.dotline.callbacks

import com.dotline.model.Profile
import com.dotline.viewModels.UserProfileModel
import com.google.firebase.firestore.DocumentSnapshot

open class UserProfileCallBack {
    open fun profile(profile:Profile){}
    open fun profiles(profiles:ArrayList<Profile>){}
    open fun lastdocument(documentSnapshot: DocumentSnapshot){}
}