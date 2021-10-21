package com.dotline.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dotline.Custom.DividerItem
import com.dotline.R
import com.dotline.adapter.BlogContentAdapter
import com.dotline.callbacks.BlogContentCallback
import com.dotline.model.BlogContent
import com.dotline.provider.QuestionProvider
import com.dotline.provider.UserProfileProvider
import com.dotline.viewModels.UserProfileModel
import kotlinx.android.synthetic.main.content_list.*
import kotlinx.android.synthetic.main.search_layout.*

class QuestionListFragment :DialogFragment() {
    var uid:String?=null;
    var saved:Boolean=false;
   lateinit var adapter:BlogContentAdapter;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (arguments!=null){
            uid= requireArguments().getString("id",null);
            saved= requireArguments().getBoolean("saved",false);
        }

        return inflater.inflate(R.layout.content_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("Qu","outer Called $uid $saved")

        if (uid!=null&&!saved){
            loadForUserId(uid!!);
        } else if (saved){
        var modele=ViewModelProvider(requireActivity()).get(UserProfileModel::class.java)
            modele.profileLiveData().observe(this, Observer {
                if (it!=null){
                    Log.i("Qu","Called")
                    loadSaved(it.fav_questions)
                }
            })
        }
    }

    fun loadSaved(ids:ArrayList<String>){
        adapter= BlogContentAdapter(arrayListOf(), activity as AppCompatActivity,true,true)
        recyclerview.layoutManager=LinearLayoutManager(context);
        recyclerview.adapter=adapter;
        recyclerview.addItemDecoration(DividerItem(requireContext(),resources.getDrawable(R.drawable.list_divider)))
        QuestionProvider.MyInstance().getSavedQuestions(ids,object:BlogContentCallback(){
            override fun contentsResult(blogsContent: ArrayList<BlogContent>?) {
                blogsContent?.let { adapter.addAll(it) }
                super.contentsResult(blogsContent)
            }
        })
    }

    fun loadForUserId(id:String){
        adapter= BlogContentAdapter(arrayListOf(), activity as AppCompatActivity,true,true)
        recyclerview.layoutManager=LinearLayoutManager(context);
        recyclerview.adapter=adapter;
        recyclerview.addItemDecoration(DividerItem(requireContext(),resources.getDrawable(R.drawable.list_divider)))
        QuestionProvider.MyInstance().getQuestionForUser(id,object:BlogContentCallback(){
            override fun contentsResult(blogsContent: ArrayList<BlogContent>?) {
                blogsContent?.let { adapter.addAll(it) }
                super.contentsResult(blogsContent)
            }
        })
    }
}