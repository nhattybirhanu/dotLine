package com.dotline.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.dotline.R
import com.dotline.activity.EditProfileActivity
import com.dotline.activity.Splash
import com.dotline.adapter.LayoutViewPageAdapter
import com.dotline.callbacks.UserProfileCallBack
import com.dotline.model.Profile
import com.dotline.provider.UserProfileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.natnaelAjema.mycv.Adapter.FragmentPageAdapter
import kotlinx.android.synthetic.main.profile_page.*
import kotlinx.android.synthetic.main.profile_page_1.*
import kotlinx.android.synthetic.main.profile_page_2.*

class ProfilePage:DialogFragment(),Toolbar.OnMenuItemClickListener {
  private  var myProfile:Boolean=false;
   private var user_id:String?=null;
    var user:FirebaseUser?=null;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            if (arguments!=null){
               user=FirebaseAuth.getInstance().currentUser;
                if (user!=null){
                    user_id=requireArguments().getString("id",null);
                    user_id?.let { myProfile= user!!.uid.equals(user_id); }
                }
            }
        return inflater.inflate(R.layout.profile_page,container,false);
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NO_TITLE, R.style.Theme_MyApplication);
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (user_id!=null){
            if (myProfile){
                toolbar.inflateMenu(R.menu.profile_setting);
                toolbar.setOnMenuItemClickListener(this);
            }
            setProfile(user_id!!)
            setPage(user_id!!)

        }
        toolbar.setNavigationOnClickListener { dismiss() }
    }
    fun setProfile(uid:String){
        UserProfileProvider.MyInstance().userProfile(uid,myProfile,object: UserProfileCallBack() {
            override fun profile(profile: Profile) {
                var ownerProfile=UserProfileProvider.MyInstance().profiles.get(user!!.uid)
               var views= ArrayList<View>(arrayListOf(viewinflate(R.layout.profile_page_1),viewinflate(R.layout.profile_page_2)))
                val  adapter=LayoutViewPageAdapter(views,profile,
                    requireActivity() as AppCompatActivity
                )
                profile_pager.adapter=adapter;
                profile_pager.offscreenPageLimit=2
                profile_tablayout.setupWithViewPager(profile_pager)
                toolbar.title="@"+profile.userName;
                community.text="Community ${profile.followers+profile.following.size}"
                community.setOnClickListener {
                    var community=Community()
                    val bundle=Bundle(); bundle.putString("uid",profile.id);
                    community.arguments=bundle;
                    community.show(childFragmentManager,profile.id);

                }
            if (myProfile){
                follow.visibility=View.GONE;
            } else {
                var followUser=ownerProfile!!.following.contains(profile.id);
                updateFollowBtn(followUser)
                var followed=profile.following.contains(user!!.uid)
//                if (followed &&followUser)
//                    toolbar.subtitle="Connected"
//                else if (followed)
//                    toolbar.subtitle="Following you"

                follow.setOnClickListener {
                    UserProfileProvider.MyInstance().manageFollowUser(user!!.uid,profile.id,!followUser)
                    updateFollowBtn(!followUser)
                }
            }
            }

        })
    }
    fun updateFollowBtn(following:Boolean){
        follow.text=if (following) "Unfollow" else "follow"
        follow.setCompoundDrawablesWithIntrinsicBounds(0,0,if (following) R.drawable.ic_remove_person else R.drawable.ic_add_person,0)
    }
    fun  setPage(uid: String){
        var adapter=FragmentPageAdapter(childFragmentManager);

        var question=QuestionListFragment(); question.arguments=getB(uid,false);

        var answers=AnswersListFragment(); answers.arguments=getB(uid,false);


        adapter.add(question,"Questioned")
        adapter.add(answers,"Answered")
       if (myProfile) {
            var saved = QuestionListFragment();
            saved.arguments = getB(uid, true);
            adapter.add(saved, "Saved")
           var tagsFragment=TagFragment();
           adapter.add(tagsFragment,"Tags")
        }
        viewpager.adapter=adapter;
        tablayout.setupWithViewPager(viewpager)

        viewpager.offscreenPageLimit=adapter.count

    }
    fun getB(uid:String,saved:Boolean):Bundle{
        var bundle=Bundle();
        bundle.putString("id",uid);
        bundle.putBoolean("saved",saved);
        return bundle;
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.logout->{
               var alertDialog=AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Log out")
                alertDialog.setMessage("Continue processing?")
                alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener(){
                        dialog, which ->
                    FirebaseAuth.getInstance().signOut();
                    startActivity(Intent(context,Splash::class.java))
                    activity?.finish()
                    dialog.dismiss()
                })
                alertDialog.setNegativeButton("Cancle", DialogInterface.OnClickListener(){
                        dialog, which ->
                    dialog.dismiss()
                })
                alertDialog.show()
            }
            R.id.edit->{
                startActivity(Intent(context,EditProfileActivity::class.java))

            }
        }
    return true;
    }
    fun viewinflate(res:Int) =LayoutInflater.from(context).inflate(res,null);



}