package com.dotline.adapter

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dotline.R
import com.dotline.activity.QuestionDetail
import com.dotline.callbacks.BlogContentCallback
import com.dotline.provider.UserProfileProvider
import com.dotline.callbacks.Selected
import com.dotline.callbacks.UserProfileCallBack
import com.dotline.fragments.ProfilePage
import com.dotline.model.*
import com.dotline.provider.QuestionProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import io.github.armcha.autolink.AutoLinkTextView
import io.github.armcha.autolink.MODE_CUSTOM
import kotlinx.android.synthetic.main.answer_item.view.*
import kotlinx.android.synthetic.main.common_blog_content.view.*
import kotlinx.android.synthetic.main.profile_page_1.view.*
import kotlinx.android.synthetic.main.question_item.view.*
import kotlinx.android.synthetic.main.question_item.view.body
import kotlinx.android.synthetic.main.question_item.view.profile_image
import kotlinx.android.synthetic.main.question_item.view.tag_list
import kotlinx.android.synthetic.main.question_item.view.title
import kotlinx.android.synthetic.main.question_item.view.user_name
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BlogContentAdapter(var blogs:ArrayList<BlogContent>,var activity: AppCompatActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
   var myProfilePage:Profile?=null;
    var common:Boolean=false;
    constructor( blogs:ArrayList<BlogContent>, activity: AppCompatActivity,commonView:Boolean) : this(blogs,activity) {
        common=commonView;
    }

    inner class QuestionHolder : RecyclerView.ViewHolder {
        constructor(itemView: View) : super(itemView){
            itemView.apply {


            }
        }
    }
    inner class AnswerHolder : RecyclerView.ViewHolder {
        constructor(itemView: View) : super(itemView){
            itemView.apply {


            }
        }
    }
    inner class CommonHolder : RecyclerView.ViewHolder {
        constructor(itemView: View) : super(itemView){
            itemView.apply {


            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    if (viewType==3)
        return CommonHolder(LayoutInflater.from(parent.context).inflate(R.layout.common_blog_content,parent,false));
        else
    if (viewType==1)
     return QuestionHolder(LayoutInflater.from(parent.context).inflate(R.layout.question_item,parent,false));
        else
            return AnswerHolder(LayoutInflater.from(parent.context).inflate(R.layout.answer_item,parent,false));
    }

    override fun getItemViewType(position: Int): Int {
        return if (common) 3 else if (blogs.get(position).question ) 1 else 2;
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var blog=blogs.get(position);
        var  user=FirebaseAuth.getInstance().currentUser;
        Log.i("blog_A","$blog ${getItemViewType(position)}")
        when(getItemViewType(position)){
            1->{
                var questionHolder=holder as QuestionHolder;
                questionHolder.itemView.apply {
                   setOnClickListener {
                        openQuestion(blog)

                    }
                    if(user!!.uid.equals(blog.owner)){

                        save.visibility=View.GONE ;
                    } else if (myProfilePage!=null){
                        save.visibility= View.VISIBLE;
                        manageFav(save,!(myProfilePage!!.fav_questions.contains(blog.id)),blog.id,user.uid,false)
                        save.setOnClickListener {

                            manageFav(save,myProfilePage!!.fav_questions.contains(blog.id),blog.id,user.uid,true)

                        }
                    }
                    title.text=blog.title;
                    body.text=blog.body;
                    answer_count.setText(blog.a_count.toString())
                    setProfile(user_name,profile_image,blog);
                    if (blog.images.isNotEmpty()){
                        setImages(images_list,blog)
                        }
                    if (blog.tags.isNotEmpty()){
                        setTags(tag_list,blog)
                    }
                    openprofile(user_name,blog)
                    openprofile(profile_image,blog)

                }
            }
            2->{
                var answerHolder=holder as AnswerHolder;
                answerHolder.itemView.apply {
                    body.text=blog.body;
                    if (blog.images.isNotEmpty()){
                        setImages(answer_images_list,blog)
                    }
                    setProfile(answer_user_name,answer_profile_image,blog);
                    send.setOnClickListener {
                        if (replay_input.text.isEmpty()){
                            showMessage(this,"Please Write some message")

                        } else {
                            showMessage(this,"Replay sent")
                            sendReplay(blog.id,replay_input.text.toString());
                            if (user!=null) blog.replays.add(blog.replays.size,Replay(user.uid,"@${user.uid}@ ${replay_input.text.toString()}"))
                            replay_input.text.clear();
                            notifyItemChanged(position)

                        }

                    }
                    setReplay(replay_body,blog.replays)
                    manageButton(up,down,blog,user?.uid?:"")

                    openprofile(answer_user_name,blog)
                    openprofile(answer_profile_image,blog)

                    if (user!=null){
                        up.setOnClickListener { sendLikeFeedBack(blog.id,true)
                            manageButton(up,down,blog,user.uid)

                            blog.feedbacks=LikeFeedBack.removeFeedBackFor(user.uid,blog.feedbacks);
                            blog.feedbacks.add(LikeFeedBack(user.uid,1))
                        }
                        down.setOnClickListener { sendLikeFeedBack(blog.id,false)

                            blog.feedbacks=LikeFeedBack.removeFeedBackFor(user.uid,blog.feedbacks);
                            blog.feedbacks.add(LikeFeedBack(user.uid,0))
                            manageButton(up,down,blog,user.uid)
                        }

                        }

                }
            }
            3->{
                var  commonHolder=holder as CommonHolder;
                commonHolder.itemView.apply {
                    if (blog.question){
                        setOnClickListener { openQuestion(blog) }
                        title.text=blog.title;

                    }
                    else if (blog.question_id!=null)
                    {
                        QuestionProvider.MyInstance().getQuestion(blog.question_id!!,Source.DEFAULT,object :BlogContentCallback(){
                            override fun singleResult(blog: BlogContent) {
                                title.text=blog.title;
                            setOnClickListener { openQuestion(blog) }
                                super.singleResult(blog)
                            }
                        })
                    }

                subtitle.text=blog.body;
                    option.visibility=if (user!!.uid.equals(blog.owner)) View.VISIBLE else View.GONE
                }
            }
        }
    }
    fun openQuestion(blog: BlogContent){
        var detail=Intent(activity,QuestionDetail::class.java);
        detail.putExtra("question",blog)

        activity.startActivity(detail)
    }
    fun  manageButton(up:Button,down:Button,blog: BlogContent,user_id:String){
        up.text=LikeFeedBack.getCountForFeedBack(1,blog.feedbacks).toString()
        down.text=LikeFeedBack.getCountForFeedBack(0,blog.feedbacks).toString()
        var user_f:Long=LikeFeedBack.getUserFeedBack(user_id,blog.feedbacks);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)

            if (user_f>-1){
                if (user_f.compareTo(1)==0){
                    up.compoundDrawableTintList= ColorStateList.valueOf(Color.GREEN)
                    down.compoundDrawableTintList= ColorStateList.valueOf(activity.resources.getColor(R.color.colorAccent))

                }
                else{
                    up.compoundDrawableTintList= ColorStateList.valueOf(activity.resources.getColor(R.color.colorAccent))

                    down.compoundDrawableTintList= ColorStateList.valueOf(Color.GREEN)
                }

            }
    }
    fun showMessage(view:View,message:String):Snackbar{
        var snack=Snackbar.make(view,message,Snackbar.LENGTH_SHORT);
        snack.show();
        return snack
    }

    override fun getItemCount(): Int {
    return          blogs.size;

    }
    fun add(blog:BlogContent){
        blogs.add(blog);
        notifyItemInserted(blogs.size-1);
        notifyDataSetChanged()

    }


    fun addAll(blogs: List<BlogContent>) {
        val pos=this.blogs.size;
        this.blogs.addAll(blogs);
        notifyItemRangeInserted(pos,blogs.size)
    }

  private  fun setImages(recyclerView: RecyclerView,blog:BlogContent){
        val layoutManager = GridLayoutManager(recyclerView.context,2,
            if(blog.images.size>2)GridLayoutManager.HORIZONTAL else GridLayoutManager.VERTICAL,false)

        recyclerView.   setLayoutManager(layoutManager)
        recyclerView.adapter=ImageAdapter(activity,ImageModel.fromString(blog.images), ImageConfig(200,200,false,false),blog);

    }
    private fun setProfile(nameView:TextView,profile_Image:ImageView,blog:BlogContent){
        UserProfileProvider.MyInstance().userProfile(blog.owner,false,
            object : UserProfileCallBack() {
                override fun profile(profile: Profile) {
                    nameView.text=profile.displayName;
                    Glide.with(profile_Image).load(profile.profile_picture).placeholder(R.drawable.ic_default_picture).circleCrop().into(profile_Image);

                }

            }
        )
    }
   private fun setTags(recyclerView: RecyclerView,blog:BlogContent){
        val layoutManager = LinearLayoutManager(recyclerView.context,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.layoutManager=layoutManager;
        var tags= Tag.toTagList(blog.tags);
        recyclerView.adapter=TagAdapter(tags,object :Selected{
            override fun select(objects: Tag, selected: Boolean, poistion: Int) {
            }

            override fun done() {
            }

        },activity,false)

    }
    fun sendReplay(id:String,replay:String){
        var user=FirebaseAuth.getInstance().currentUser;
        if (user!=null) {
            val database = FirebaseDatabase.getInstance().reference.child("Answers").child(id);
            database.child("replay").push().setValue("@${user.uid}@ $replay");

        }
    }
    fun setReplay(textView: AutoLinkTextView,replays:ArrayList<Replay>){

//        val custom = MODE_CUSTOM("\\sAndroid\\b", "\\sGoogle\\b")
        var usernames= arrayListOf<String>();
        var userIDMap=HashMap<String,String>();
        var replayBody=StringBuilder();
        var count=0;
        var total=replays.size;
        textView.onAutoLinkClick {
            showMessage(textView, "Hello ${ userIDMap.get(it.originalText.trim()) }")
        }
        for (reply in replays){
            var body=reply.message;
            val start=body.indexOf('@')
            val end=body.lastIndexOf('@')
            val userid=body.substring(start+1,end);
            if(userid.isNotEmpty()){
            UserProfileProvider.MyInstance().userProfile(userid,false,
                object : UserProfileCallBack() {
                    override fun profile(profile: Profile) {
                        userIDMap.put(profile.userName,profile.id)
                        count++;
                          usernames.add("\\s${profile.userName}\\b")
                        var custom=MODE_CUSTOM("\\s${profile.userName}\\b")
                        textView.addAutoLinkMode(custom)
                        textView.addSpan(custom, StyleSpan(Typeface.NORMAL), UnderlineSpan())

                        body=  body.replaceRange(start,end+1, " ${ profile.userName.trim() } ");
                        replayBody.append(body+if(count!=total)'\n' else "");
                        if (count==total){

                            textView.customModeColor=activity.resources.getColor(R.color.colorSecondaryDark)
                            textView.text = replayBody.toString();
                        }
                    }

                }
            )
        } else{
            count++;
        }
        }
    }
    fun openprofile(view: View,blog: BlogContent){

        view.setOnClickListener {
            val profile_page= ProfilePage();
            var bundle= Bundle();
            bundle.putString("id",if (blog.question) blog.owner else blog.question_owner)
            profile_page.arguments=bundle;
            profile_page.show(activity.supportFragmentManager,"");

        }
    }
    fun sendLikeFeedBack(answerId:String,like:Boolean){
        var user=FirebaseAuth.getInstance().currentUser;
        if (user!=null) {
            val database = FirebaseDatabase.getInstance().reference.child("Answers").child(answerId).child("feedbacks");
            database.child(user.uid).setValue(if (like) 1 else 0);

        }
    }

    fun manageFav(imB:ImageButton,seleted:Boolean,uid:String,user_id:String,update:Boolean){
        if(update){
            var doc = FirebaseFirestore.getInstance().collection("Users").document(user_id)
            doc.update(
                "fav_q",
                if (seleted) FieldValue.arrayRemove(uid) else FieldValue.arrayUnion(uid)
            );

        }
        imB.setImageResource( if (seleted) R.drawable.ic_save else R.drawable.ic_saved)
    }



    fun clear() {
        blogs.clear(); notifyDataSetChanged()
    }
}