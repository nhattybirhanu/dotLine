package com.dotline.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dotline.Custom.DividerItem
import com.dotline.R
import com.dotline.adapter.BlogContentAdapter
import com.dotline.adapter.ProfileAdapter
import com.dotline.callbacks.BlogContentCallback
import com.dotline.callbacks.UserProfileCallBack
import com.dotline.model.BlogContent
import com.dotline.model.Profile
import com.dotline.provider.SearchProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.content_list.*
import kotlinx.android.synthetic.main.search_layout.*

class SearchFragment:DialogFragment() {
        lateinit var  blogadapter: BlogContentAdapter;
    lateinit var  profileadapter: ProfileAdapter;
    var lastdoc:DocumentSnapshot?=null;
    var tags=ArrayList<String>();
     var date:Long=0;
    var username:String?=null;
    var blog_search:Boolean=false;
    var fullText:String?=null;
    var  standalone=false;
    var callback:UserProfileCallBack?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (arguments!=null){
        standalone= requireArguments().getBoolean("custom",false);
        }
        return inflater.inflate(R.layout.search_layout,container,false);
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NO_TITLE, R.style.Theme_MyApplication);
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        blogadapter= BlogContentAdapter(arrayListOf(), requireActivity() as AppCompatActivity,true,false)
        profileadapter= ProfileAdapter(arrayListOf(), requireActivity() as AppCompatActivity,false,standalone);
        search_list.addItemDecoration(DividerItem(requireContext(),resources.getDrawable(R.drawable.list_divider)))
        search_list.layoutManager=LinearLayoutManager(context);
        more.setOnClickListener {
            if (blog_search){
                if (fullText!=null)
                    searchFullTextBlog(fullText!!,false)
                else
                    searchBlog(tags,date,false)
            } else if (username!=null)
                searchUser(username!!,false)
        }
        if(standalone) {
            app_bar.visibility = View.VISIBLE;
            var searchMenu=toolbar.menu.findItem(R.id.search); searchMenu.setVisible(true)
            var searchView: SearchView =
               searchMenu .actionView as SearchView;
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false;
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null && newText.length > 2)
                        searchUser(newText, false)
                    return true;
                }
            })
            toolbar.menu.findItem(R.id.done)
                .setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        Log.i("UserSelector",profileadapter.getSelected().toString()+"${callback==null}")
                        if (callback != null) callback!!.profiles(profileadapter.getSelected())
                        dismiss()
                        return true;
                    }
                }
                )
        }
    }
    fun  searchBlog(tags:ArrayList<String>,date:Long,forceClear:Boolean){
        this.tags=tags;
        this.date=date;
        blog_search=true;


        fullText=null;
        if (forceClear) lastdoc=null;


        search_list.adapter=blogadapter;
            progress.visibility=View.VISIBLE
        more.visibility=View.GONE

        SearchProvider.MyInstance().searchByFilter(tags,date,object :BlogContentCallback(){
            override fun contentsResult(blogsContent: ArrayList<BlogContent>?) {
                if (forceClear)
                blogadapter.clear();
                progress.visibility=View.GONE

                blogsContent?.let { blogadapter.addAll(it)
                    if (blogsContent.isNotEmpty())
                        more.visibility=View.VISIBLE
                }
                super.contentsResult(blogsContent)
            }

            override fun lastdocument(document: DocumentSnapshot) {
                lastdoc=document;
                super.lastdocument(document)
            }
        },lastdoc)
    }
    fun searchUser(username:String,forceClear:Boolean){
        blog_search=false;
        this.username=username;
        search_list.adapter=profileadapter;
        progress.visibility=View.VISIBLE
        more.visibility=View.GONE
        if (forceClear) lastdoc=null;
        SearchProvider.MyInstance().searchUsers(username,object :UserProfileCallBack(){
            override fun profiles(profiles: ArrayList<Profile>) {
                progress.visibility=View.GONE
                if (forceClear)
                profileadapter.clear();
                profiles?.let {
                    if (profiles.isNotEmpty())
                        more.visibility=View.VISIBLE
                    profileadapter.addAll(it)
                }
                super.profiles(profiles)
            }

            override fun lastdocument(documentSnapshot: DocumentSnapshot) {
                lastdoc=documentSnapshot;
                super.lastdocument(documentSnapshot)
            }
        },lastdoc)


    }

    fun searchFullTextBlog(newText: String, forceClear: Boolean) {

        blog_search=true;

        fullText=newText;
        search_list.adapter=blogadapter;
        progress.visibility=View.VISIBLE
        more.visibility=View.GONE
        if (forceClear) lastdoc=null;
        SearchProvider.MyInstance().searchFullTextBlog(newText,object :BlogContentCallback(){
            override fun contentsResult(blogsContent: ArrayList<BlogContent>?) {
                if (forceClear)
                    blogadapter.clear();
                progress.visibility=View.GONE

                blogsContent?.let { blogadapter.addAll(it)
                if (blogsContent.isNotEmpty())
                    more.visibility=View.VISIBLE

                }
                super.contentsResult(blogsContent)
            }

            override fun lastdocument(document: DocumentSnapshot) {
                lastdoc=document;
                super.lastdocument(document)
            }
        },lastdoc)

    }



}