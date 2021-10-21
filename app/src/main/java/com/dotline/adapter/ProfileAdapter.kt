package com.dotline.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dotline.R
import com.dotline.callbacks.UserProfileCallBack
import com.dotline.fragments.ProfilePage
import com.dotline.model.BlogContent
import com.dotline.model.Profile
import kotlinx.android.synthetic.main.user_item.view.*

class ProfileAdapter(var profiles:ArrayList<Profile>,var activity: AppCompatActivity,var showOption:Boolean,var select_Mode:Boolean) : RecyclerView.Adapter<ProfileAdapter.ProfileHolder>() {
    inner  class ProfileHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileHolder {
    return  ProfileHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_item,parent,false))
    }

    override fun onBindViewHolder(holder: ProfileHolder, position: Int) {
        var profile=profiles.get(position);
        holder.itemView.apply {
            display_name.text=profile.displayName;
            username.text=profile.userName
            if (!select_Mode)
            openprofile(this,profile)
            else {
                layout.setBackgroundColor(activity.resources.getColor(if(!profile.selected) R.color.white else R.color.colorSecondary))
                setOnClickListener {  profile.selected=!profile.selected;
                    notifyItemChanged(position) }

            }
        Glide.with(profile_image).load(profile.profile_picture).placeholder(R.drawable.ic_default_picture).circleCrop().into(profile_image);
        }
    }

    override fun getItemCount()=profiles.size
    fun openprofile(view: View,profile: Profile){

        view.setOnClickListener {

            val profile_page= ProfilePage();
            var bundle= Bundle();
            bundle.putString("id",profile.id)
            profile_page.arguments=bundle;
            profile_page.show(activity.supportFragmentManager,"");

        }
    }
    fun  addAll(profiles:ArrayList<Profile>){
        var oldS=profiles.size;
        this.profiles.addAll(profiles);
        notifyItemRangeInserted(oldS,profiles.size)
    }

    fun clear() {
        profiles.clear();
        notifyDataSetChanged()
    }
    fun getSelected():ArrayList<Profile>{
      return profiles.filter { profile -> profile.selected } as ArrayList<Profile>
    }
}