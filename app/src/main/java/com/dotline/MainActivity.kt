package com.dotline

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.*
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import androidx.work.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dotline.Custom.CustomSearchView
import com.dotline.Service.NotificationService
import com.dotline.adapter.TextSelectorAdapter
import com.dotline.callbacks.Selected
import com.dotline.fragments.*
import com.dotline.model.Selector
import com.dotline.model.Tag
import com.dotline.viewModels.UserProfileModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.natnaelAjema.mycv.Adapter.FragmentPageAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.filter.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    lateinit var toolbar:androidx.appcompat.widget.Toolbar;
        var profile_page:ProfilePage?=null;
        var user:FirebaseUser?=null;
        var menu:Menu?=null;
        lateinit var searchFragment: SearchFragment;
        var snackbar: Snackbar?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar)
        permission()
        var pagerAdapter=FragmentPageAdapter(supportFragmentManager);
        pagerAdapter.add(Home(),"Home")
        searchFragment= SearchFragment();
        pagerAdapter.add(searchFragment,"Inbox")
        pagerAdapter.add(InboxFragment(),"Inbox")
        viewpager.adapter=pagerAdapter;
        viewpager.offscreenPageLimit=2;
        user= FirebaseAuth.getInstance().currentUser;
        navigation()
        schedule()
        var userViewmoMdoel=ViewModelProvider(this).get(UserProfileModel::class.java);
        userViewmoMdoel.profileLiveData().observe(this, androidx.lifecycle.Observer {

            if (it!=null){
                Glide.with(this).load(it.profile_picture).placeholder(R.drawable.ic_default_picture).circleCrop().into(object : CustomTarget<Drawable?>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?

                    ) {
                        menu!!.findItem(R.id.profile).setIcon(resource)

                    }
                })
            }
        })

    }

    fun navigation(){
        bottom_bar.setOnItemSelectedListener {

            viewpager.setCurrentItem(it,true)
        }
        viewpager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                bottom_bar.onItemSelected?.invoke(navid(position))
                if(menu!=null){
                    var showSearch=position==1;
                    menu!!.findItem(R.id.search).setVisible(showSearch)
                    menu!!.findItem(R.id.filter).setVisible(showSearch)
                    menu!!.findItem(R.id.profile).setVisible(!showSearch)
                    if (snackbar!=null&&snackbar!!.isShown&&!showSearch) snackbar!!.dismiss();
                if (showSearch)
                menu!!.findItem(R.id.search).expandActionView();else menu!!.findItem(R.id.search).collapseActionView()
                }

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }
    fun navid(pos:Int):Int{
        when(pos){
            0->return R.id.home
            1->return R.id.search
            2->return R.id.notifcation
        }
    return R.id.home
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        this.menu=menu;
        var searchView:SearchView= menu!!.findItem(R.id.search).actionView as SearchView;
        CustomSearchView().customize(searchView)
        searchView.queryHint="search by typing @username , question "
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (searchFragment!=null) newText?.let {
                    if (newText.isNotEmpty()&&newText.get(0)=='@'&&newText.length>3)
                    searchFragment.searchUser(it.replace("@",""),true)
                    else if (newText.length>2) searchFragment.searchFullTextBlog(newText,true)
                }

                return true
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.profile->

            {
                if (user==null) return false
                if (profile_page==null){
                    profile_page= ProfilePage();
                    var bundle=Bundle();
                    bundle.putString("id",user!!.uid)
                    profile_page!!.arguments=bundle;
                }
                profile_page!!.show(supportFragmentManager,"Mine")

            }
            R.id.filter ->{
                if(snackbar!=null) snackbar!!.show() else
                showFilter()
            }
        }
        return true
    }

    private fun showFilter() {
        var date:Long=-1;
       snackbar=Snackbar.make(toolbar,"",Snackbar.LENGTH_INDEFINITE);
        var sView:Snackbar.SnackbarLayout= snackbar!!.view as Snackbar.SnackbarLayout;
        val textView =
            sView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        textView.visibility = View.INVISIBLE

        var layout=LayoutInflater.from(this).inflate(R.layout.filter,null);
        sView.addView(layout,0)
        sView.setBackgroundColor(Color.TRANSPARENT)
        var para:FrameLayout.LayoutParams=
            sView.layoutParams as FrameLayout.LayoutParams;
        para.gravity=Gravity.TOP;
        var adapter=TextSelectorAdapter(ArrayList());
        layout.tags_list.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        layout.tags_list.adapter=adapter;
        layout.close_filter.setOnClickListener { snackbar!!.dismiss() }
        layout.search_filter.setOnClickListener {
            searchFragment.searchBlog(Selector.toStirngArray(adapter.selector),date,true);
            snackbar!!.dismiss()


        }
        layout.list_close.setOnClickListener {
            adapter.clear();
            layout.tag_list_layout.visibility=View.GONE
        }
        layout.tag_filiter.setOnClickListener {
            var tagSelector=TagSelector();
            tagSelector.selector=object :Selected{
                override fun select(objects: Tag, selected: Boolean, poistion: Int) {
                    layout.tag_list_layout.visibility=View.VISIBLE
                    if (selected){
                        adapter.add(Selector(objects.name,objects.name))
                    }
                }

                override fun done() {

                }

            }
            tagSelector.show(supportFragmentManager,"")
        }
        layout.tag_date.setOnClickListener {
            var datePickerDialog = if (SDK_INT >= Build.VERSION_CODES.N) {
                DatePickerDialog(it.context)
            } else {
                TODO("VERSION.SDK_INT < N")
            }
            if (datePickerDialog!=null){
                datePickerDialog.setTitle("Select starting date ")
                datePickerDialog.setOnDateSetListener(object :DatePickerDialog.OnDateSetListener{
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        layout.date_close.visibility=View.VISIBLE;
                        var pickedDate:Calendar=Calendar.getInstance();pickedDate.set(year,month,dayOfMonth);
                        date=pickedDate.timeInMillis/1000;
                        layout.date_display.visibility=View.VISIBLE;
                        layout.date_display.text="After $month/$dayOfMonth/$year";
                    }

                })
                datePickerDialog.show();
            }

        }
        layout.date_close.setOnClickListener {
            layout.date_close.visibility=View.GONE;
            date=-1;
            layout.date_display.visibility=View.VISIBLE;
            layout.date_display.text="";
        }
        sView.setPadding(0,0,0,0)
        snackbar!!.show()


}
    fun schedule() {
        val constraints: Constraints = Constraints.Builder()
     .setRequiredNetworkType(NetworkType.CONNECTED)

            .build()
        val request =
            PeriodicWorkRequest.Builder(NotificationService::class.java, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag("Notification")
                .build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("Notification", ExistingPeriodicWorkPolicy.REPLACE, request)
    }

    fun permission(){
        if (SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 2296)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED //       &&checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
            }
        }

    }
}