package com.dotline.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dotline.Custom.DividerItem
import com.dotline.R
import com.dotline.adapter. BlogContentAdapter
import com.dotline.adapter.ImageAdapter
import com.dotline.adapter.RemoteFileAdapter
import com.dotline.adapter.TagAdapter
import com.dotline.callbacks.BlogContentCallback
import com.dotline.callbacks.MetaDataCallBack
import com.dotline.callbacks.Selected
import com.dotline.callbacks.UserProfileCallBack
import com.dotline.model.*
import com.dotline.provider.AnswerProvider
import com.dotline.provider.RemoteFileProvider
import com.dotline.provider.UserProfileProvider
import com.google.firebase.storage.StorageMetadata
import kotlinx.android.synthetic.main.question_detail.*
import java.text.SimpleDateFormat
import java.util.*
import android.view.View;
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.dotline.fragments.ProfilePage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.user_item.view.*


class QuestionDetail:AppCompatActivity(),Toolbar.OnMenuItemClickListener {
    var blogContent: BlogContent?=null;
    lateinit var profile: Profile;
        var myProfile: Profile?=null;
    var dateformat=SimpleDateFormat(", MMM d,yyyy")
     var user:FirebaseUser?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.question_detail)
        super.onCreate(savedInstanceState);
        blogContent= intent.getSerializableExtra("question") as BlogContent;
        user=FirebaseAuth.getInstance().currentUser;
        blogContent?.let {
            date.setText("Asked "+dateformat.format(Date(blogContent!!.date*1000)))
            question_title.text=blogContent!!.title
            body.text=blogContent!!.body
            profile(blogContent!!.owner)
            setImages(blogContent!!.images)
            setTags(blogContent!!.tags)
            answer_fab.fabIcon=getDrawable(R.drawable.ic_edit);
            setFile(blogContent!!.files)
            if (user!=null){

                myProfile=UserProfileProvider.MyInstance().profiles.get(user!!.uid)!!
                if(myProfile!=null){
                    var save=toolbar.menu.findItem(R.id.save);
                    save.setIcon(if (myProfile!!.fav_questions.contains(blogContent!!.id)) R.drawable.ic_saved else R.drawable.ic_save)
                }

            }
            setAnswers(blogContent!!.id);
            if (blogContent!!.closed){
                answer_fab.visibility=View.GONE;
                closedMessage.visibility=View.VISIBLE;
            } else         nestedscroll()

            answer_fab.setOnClickListener {
                var answer=Intent(this,QuestionCreator::class.java)
                answer.putExtra("question",false)
                answer.putExtra("question_id",blogContent!!.id);
                answer.putExtra("question_owner",blogContent!!.owner);
                startActivity(answer)

            }

        }
        toolbar.setNavigationOnClickListener { finish() }

    }
    fun nestedscroll(){
        nested.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val TAG = "nested_sync"
            if (scrollY > oldScrollY) {
                Log.i(TAG, "Scroll DOWN")
                answer_fab.hide()
            }
            if (scrollY < oldScrollY) {
                Log.i(TAG, "Scroll UP")
                answer_fab.show()
            }
            if (scrollY == 0) {
                Log.i(TAG, "TOP SCROLL")
            }
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                Log.i(TAG, "BOTTOM SCROLL")

            }
        })
    }

    fun  setTags(tags:ArrayList<String>){
        if (tags!=null&&tags.isNotEmpty()){
            val layoutManager = LinearLayoutManager(this ,LinearLayoutManager.HORIZONTAL,false);
            tags_list.layoutManager=layoutManager;
            var tags= Tag.toTagList(tags);
            tags_list.adapter= TagAdapter(tags,object : Selected {
                override fun select(objects: Tag, selected: Boolean, poistion: Int) {
                }

                override fun done() {
                }

            },this,false)

        }
    }
    fun setImages(images:ArrayList<String>?){
        if (images!=null) {
            val layoutManager = GridLayoutManager(
                this,
                2,
                if (images.size > 2) GridLayoutManager.HORIZONTAL else GridLayoutManager.VERTICAL,
                false
            )

            images_list.setLayoutManager(layoutManager)
            images_list.adapter =
                ImageAdapter(this,ImageModel.fromString(images), ImageConfig(200, 200, false, false),blogContent!!);
        }
    }
    fun  profile(id:String){
        UserProfileProvider.MyInstance().userProfile(id,false,
            object : UserProfileCallBack() {
                override fun profile(profile: Profile) {
                    user_name.text=profile.displayName;
                    Glide.with(profile_image).load(profile.profile_picture).placeholder(R.drawable.ic_default_picture).circleCrop().into(profile_image);
                    profile_image.setOnClickListener {

                        val profile_page= ProfilePage();
                        var bundle= Bundle();
                        bundle.putString("id",id)
                        profile_page.arguments=bundle;
                        profile_page.show(supportFragmentManager,"");
                    }
                }

            }
        )
    }

    fun setFile(urls:ArrayList<String>){
        if (!urls.isNullOrEmpty())
        {
            val fileAdapter=RemoteFileAdapter(arrayListOf(),this);
            file_list.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            file_list.adapter=fileAdapter;
            urls.forEach {
                RemoteFileProvider.MyInstance().fileMetaData(it,object :MetaDataCallBack{
                    override fun metaResult(fileModel: FileModel?) {
                        if(fileModel!=null)
                        fileAdapter.add(fileModel)
                    }

                })

            }

        }

    }
    fun setAnswers(uid:String){
        val answer_adapter=
            com.dotline.adapter.BlogContentAdapter(arrayListOf(), this, blogContent!!.closed)
        answer_adapter.myProfilePage=myProfile;
        answer_list.layoutManager=LinearLayoutManager(this);
        answer_list.addItemDecoration(DividerItem(this,resources.getDrawable(R.drawable.list_divider)))
        answer_list.adapter=answer_adapter

        AnswerProvider.MyInstance().getAnswers(uid,object : BlogContentCallback() {
            override fun contentsResult(blogsContent: ArrayList<BlogContent>?) {
                if (blogsContent != null) {
                    answer_adapter.addAll(blogsContent)
                }
            }

        })

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
            when(item!!.itemId){
                R.id.save->{
                    if (myProfile!=null){
                        var selected = myProfile!!.fav_questions.contains(blogContent!!.id);
                        var doc = FirebaseFirestore.getInstance().collection("Users").document(myProfile!!.id)
                        doc.update(
                            "fav_q",
                            if (selected) FieldValue.arrayRemove(myProfile!!.id) else FieldValue.arrayUnion(myProfile!!.id)
                        );

                        item.setIcon( if (selected) R.drawable.ic_save else R.drawable.ic_saved)
                    }
                }
            }
    return true;
    }


}