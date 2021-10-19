package com.dotline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dotline.R
import com.dotline.callbacks.UserProfileCallBack
import com.dotline.model.Profile
import com.dotline.provider.UserProfileProvider
import com.google.firebase.auth.FirebaseAuth
import com.natnaelAjema.mycv.Adapter.FragmentPageAdapter
import kotlinx.android.synthetic.main.community_layout.*

class Community:DialogFragment() {
    var userId:String?=null;
    var selfAccount=false;
    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NO_TITLE, R.style.Theme_MyApplication);
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (arguments!=null){
            val user=FirebaseAuth.getInstance().currentUser;
            if (user!=null){
                userId= requireArguments().getString("uid");
                if (userId!=null) selfAccount= user.uid.equals(userId);
            }
        }
        return inflater.inflate(R.layout.community_layout,container,false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener {
            dismiss()
        }
        if (userId!=null){
            UserProfileProvider.MyInstance().userProfile(userId!!,false,object :UserProfileCallBack(){
                override fun profile(profile: Profile) {

                    var adapter = FragmentPageAdapter(childFragmentManager);
                    var following=FollowFragment();
                    following.arguments=argFollow(profile.id,true,profile.following)
                    var followers=FollowFragment();
                    followers.arguments=argFollow(profile.id,false,null)
                    adapter.add(following,"Following ${profile.following.size}");
                    adapter.add(followers,"Followers ${profile.followers}");
                    viewpager.adapter=adapter;
                    viewpager.offscreenPageLimit=2;
                    tablayout.setupWithViewPager(viewpager)
                    super.profile(profile)
                }
            })
        }
    }

    fun  argFollow(user_id:String,follow:Boolean,follow_ids:ArrayList<String>?):Bundle{
        var bundle=Bundle()
        bundle.putBoolean("follow",follow);
        bundle.putString("uid",user_id);
        if (follow) bundle.putStringArrayList("usersId",follow_ids);
        return bundle;
    }

}