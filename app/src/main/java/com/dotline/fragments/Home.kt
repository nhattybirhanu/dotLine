package com.dotline.fragments

import android.content.Intent
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
import com.dotline.activity.QuestionCreator
import com.dotline.adapter.BlogContentAdapter
import com.dotline.viewModels.HomeViewModel
import com.dotline.viewModels.UserProfileModel
import kotlinx.android.synthetic.main.home.*
import java.util.*

class Home:DialogFragment() {
   lateinit var homeViewModel:HomeViewModel;
    lateinit var  adapter:BlogContentAdapter;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        custom_fab.fabIcon=resources.getDrawable(R.drawable.ic_add);
        custom_fab.setOnClickListener { startActivity(Intent(context,QuestionCreator::class.java)) }
        adapter=BlogContentAdapter(arrayListOf(), activity as AppCompatActivity,false,true)
     //   recyclerview.setHasFixedSize(true);
        var userProfileModel=ViewModelProvider(requireActivity()).get(UserProfileModel::class.java);
        userProfileModel.profileLiveData().observe(this, Observer { profile->
            if (profile!=null){
                adapter.myProfilePage=profile;
                setListener()
            } else requireActivity().finish()

        })


    }
    fun setListener(){
        recyclerview.layoutManager=LinearLayoutManager(context);
        recyclerview.addItemDecoration(DividerItem(requireContext(),resources.getDrawable(R.drawable.list_divider)))
        recyclerview.adapter=adapter;
        homeViewModel=ViewModelProvider(requireActivity()).get(HomeViewModel::class.java);
        homeViewModel.blogs().observe(this, Observer { blogs->
             Collections.reverse(blogs);
            adapter.addAll(blogs!!);
            Log.i("blog",blogs.toString())

        });
    }
}