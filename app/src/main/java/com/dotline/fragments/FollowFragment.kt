package com.dotline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dotline.R
import com.dotline.adapter.ProfileAdapter
import com.dotline.callbacks.UserProfileCallBack
import com.dotline.model.Profile
import com.dotline.provider.UserProfileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.content_list.*

class FollowFragment:Fragment() {
    var follow:Boolean?=null;
    var usersId:ArrayList<String>?=null
    var user_ID:String?=null;
    lateinit var adapter:ProfileAdapter;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (arguments!=null){
            follow= requireArguments().getBoolean("follow");
            user_ID=requireArguments().getString("uid");
            usersId=requireArguments().getStringArrayList("usersId")
        }
        return inflater.inflate(R.layout.content_list,container,false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (follow!=null&&user_ID!=null){
            adapter = ProfileAdapter(
                arrayListOf(),
                requireActivity() as AppCompatActivity,
                FirebaseAuth.getInstance().currentUser?.uid.equals(user_ID) ?: false,
                false
            );
            recyclerview.layoutManager=LinearLayoutManager(context)
            recyclerview.adapter=adapter;
            if (follow!!&&usersId!=null) setFollowing(usersId!!)
            else if (!follow!!) setFollowers(user_ID!!)
        }
    }

    fun setFollowing(usersId:ArrayList<String>){
        if(usersId.isNotEmpty())
        UserProfileProvider.MyInstance().getFollowing(usersId, object :UserProfileCallBack(){
            override fun profiles(profiles: ArrayList<Profile>) {
                adapter.addAll(profiles)
                super.profiles(profiles)
            }
        })
    }

    fun setFollowers(userId:String){
        UserProfileProvider.MyInstance().getFollowers(userId, object :UserProfileCallBack(){
            override fun profiles(profiles: ArrayList<Profile>) {
                adapter.addAll(profiles)
                super.profiles(profiles)
            }
        })
    }

}