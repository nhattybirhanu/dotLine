package com.dotline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dotline.R
import com.dotline.adapter.BlogContentAdapter
import com.dotline.callbacks.BlogContentCallback
import com.dotline.model.BlogContent
import com.dotline.provider.AnswerProvider
import com.dotline.provider.QuestionProvider
import kotlinx.android.synthetic.main.content_list.*

class AnswersListFragment :DialogFragment() {
    var uid:String?=null;
    lateinit var adapter: BlogContentAdapter;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (arguments!=null){
            uid= requireArguments().getString("id",null);
        }
        return inflater.inflate(R.layout.content_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (uid!=null){
            loadForUserId(uid!!);
        }  }
    fun loadForUserId(id:String){
        adapter= BlogContentAdapter(arrayListOf(), activity as AppCompatActivity,true)
        recyclerview.layoutManager= LinearLayoutManager(context);
        recyclerview.adapter=adapter;
        AnswerProvider.MyInstance().getAnswersForUser(id,object: BlogContentCallback(){
            override fun contentsResult(blogsContent: ArrayList<BlogContent>?) {
                blogsContent?.let { adapter.addAll(it) }
                super.contentsResult(blogsContent)
            }
        })
    }

}