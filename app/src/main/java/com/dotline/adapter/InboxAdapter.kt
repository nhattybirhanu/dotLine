package com.dotline.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dotline.R
import com.dotline.activity.QuestionDetail
import com.dotline.callbacks.BlogContentCallback
import com.dotline.callbacks.UserProfileCallBack
import com.dotline.model.BlogContent
import com.dotline.model.Profile
import com.dotline.provider.QuestionProvider
import com.dotline.provider.UserProfileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.inbox_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class InboxAdapter(var  activity: AppCompatActivity,var inboxes:ArrayList<BlogContent>):
    RecyclerView.Adapter<InboxAdapter.InboxHolder>() {
    inner  class InboxHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxHolder {
        return InboxHolder(LayoutInflater.from(parent.context).inflate(R.layout.inbox_item,parent,false))
    }

    override fun onBindViewHolder(holder: InboxHolder, position: Int) {
        var item=inboxes.get(position);
        var  user= FirebaseAuth.getInstance().currentUser;
        if (user==null) return
        holder.itemView.apply {
            var profile_id=item.owner;
            if (item.question_owner!=null&&profile_id.equals(user.uid))
                profile_id= item.question_owner!!;
            UserProfileProvider.MyInstance().userProfile(profile_id,false,
                object : UserProfileCallBack() {
                    override fun profile(profile: Profile) {
                        display_name.text=if(user.uid==profile.id) "You" else profile.displayName;
                        body.text=
                            (if (!profile_id.equals(item.owner)) "You : " else "")+
                            item.body;

                    }

                }
            )
            date.text=formatedData(item.date)
            count.text=item.replays.size.toString();
            if(item.question_id!=null)
            QuestionProvider.MyInstance().getQuestion(item.question_id!!,Source.DEFAULT,object:BlogContentCallback(){
                override fun singleResult(blog: BlogContent) {
                    subject.setText(blog.title)
                    super.singleResult(blog)
                    setOnClickListener() {
                        var detail= Intent(activity, QuestionDetail::class.java);
                        detail.putExtra("question",blog)

                        activity.startActivity(detail);

                    }
                }
            })
        }
    }

    override fun getItemCount(): Int {
    return  inboxes.size
    }
    fun formatedData(date:Long):String{
        var dateForamt=SimpleDateFormat("hh:mm:aa")
        return dateForamt.format(Date(date*1000))
    }
    fun addInbox(inbox:BlogContent){
        inboxes.add(inbox);
        notifyItemInserted(inboxes.size)
    }
    fun addAllInbox(inbox:ArrayList<BlogContent>){
        Collections.sort(inbox, kotlin.Comparator { o1, o2 -> o2.date.compareTo(o1.date) })
        for (item in inbox){
            val index=inboxes.indexOf(item);
            if (index==-1){
                inboxes.add(item);
                notifyItemInserted(inboxes.size)
            }
            else {
                inboxes.set(index,item);
                notifyItemChanged(index)
            }
        }

    }



    }