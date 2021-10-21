package com.dotline.activity.account

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.dotline.R
import com.dotline.adapter.TagAdapter
import com.dotline.callbacks.Selected
import com.dotline.model.Tag
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.acadami_sign_up.view.*
import kotlinx.android.synthetic.main.display_name.*
import kotlinx.android.synthetic.main.display_name.view.*
import kotlinx.android.synthetic.main.email_sign_up.*
import kotlinx.android.synthetic.main.email_sign_up.view.*
import kotlinx.android.synthetic.main.password_sign_up.*
import kotlinx.android.synthetic.main.sign_up.*
import kotlinx.android.synthetic.main.tags_sign_up.*
import kotlinx.android.synthetic.main.tags_sign_up.view.*
import kotlinx.android.synthetic.main.username.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SignUp:AppCompatActivity() {
    lateinit     var pages: ArrayList<View> ;
    lateinit var adapter:SignUpAdapter;
    var displayName:String?=null;
    var userName:String?=null;
         var email: String? =null;
    lateinit var department:String;
    lateinit var  auth:FirebaseAuth;
    lateinit var progressBar:ProgressDialog
    var selectedTags=ArrayList<Tag>();
    var confiremd_password:String?=null
    var academy:String?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)
        pages= arrayListOf<View>(layoutView(R.layout.email_sign_up)
            ,layoutView(R.layout.password_sign_up)
            ,layoutView(R.layout.display_name),
            layoutView(R.layout.username),
            layoutView(R.layout.acadami_sign_up),
            layoutView(R.layout.tags_sign_up))
        adapter=SignUpAdapter(pages)
        createProgressDialog()
        viewpager.adapter=adapter;
        viewpager.offscreenPageLimit=6;
        auth= FirebaseAuth.getInstance();
        toolbar.setNavigationOnClickListener {
            close()
        }
    }
    fun close(){
        var dialog= AlertDialog.Builder(this);
        dialog.setTitle("Do you want cancle sign up ?")
        dialog.setPositiveButton("Close", DialogInterface.OnClickListener(){
                dialog, which ->
            dialog.dismiss()
            finish()
        })
        dialog.setNegativeButton("Cancle", DialogInterface.OnClickListener(){
                dialog, which ->
            dialog.dismiss()
        })

        dialog.show()
    }

    fun createProgressDialog(){
    progressBar= ProgressDialog(this);
        progressBar.setCancelable(false)
    }
    fun next(view:View){

        manageViewPager(true);
    }
    fun pre(view:View){
      manageViewPager(false)
    }
    fun  manageViewPager(canGonext:Boolean?){
        var goNext=canGonext;
        when(viewpager.currentItem){
            0->{
                if (TextUtils.isEmpty(email_text.text.toString())){
                    showMessage("Please enter your miu email",false)
                    return
                }
                if (email==null&&canGonext!=null){
                    goNext=null

                    progressBar.setMessage("Checking email")
                    progressBar.show()
                    emailIsTaken(email_text.text.toString());
                }


            }
            1->{
                if (canGonext!!){
                    if (password.text.toString().length<6){
                        showMessage("Please Make your password more than 5 character",false)
                        return
                    }
                    if (!password.text.toString().equals(repeat_password.text.toString())){
                        showMessage("Please repeat your password correctly",false)
                        return
                    }
                    confiremd_password=password.text.toString();

                }



            }
            2->{
                if (canGonext!!) {
                    if (TextUtils.isEmpty(display_name.text)) {
                        showMessage("please enter your name", false)

                        return
                    } else displayName = display_name.text.toString();
                }
            }
            3->{
                if (canGonext!=null&&canGonext!!){
                    goNext=null;
                    if (TextUtils.isEmpty(user_name.text))
                    {
                        showMessage("please enter unique username",false)

                    } else if(userName?.isEmpty() ?: true){
                        progressBar.setMessage("Checking username")
                        progressBar.show()
                        checkUsername(user_name.text.toString())
                    } else goNext=canGonext;
                }




            }
            5->{
                if (canGonext!!&&email!=null&&confiremd_password!=null){
                    progressBar.setMessage("Creating account")
                    progressBar.show()
                    createUserWithNameEmailAndPassword(email,confiremd_password)

                } else
                Log.i("auth","validation is not passed")

            }
        }
        if (canGonext==null){
            next.setEnabled(true)
            previous.setEnabled(true)
        }
        if(goNext!=null){
            if(goNext)
                viewpager.next() else viewpager.previous()
            signup_Progress.setProgress(20*viewpager.currentItem+(if(goNext) 1 else -1))
            next.setEnabled(true)
            previous.setEnabled(true)
        } else{
            next.setEnabled(false)
            previous.setEnabled(false)
        }


         }

    fun emailIsTaken(ver_email:String) {
        var email=ver_email;
        Log.i("Auth","Email $email")
        if (!email.contains("@miu.edu")) email=email.plus("@miu.edu")
        Log.i("Auth","Email validate $email")

        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener(this){task->
                if (task.isSuccessful) {
                    val queryResult:SignInMethodQueryResult= task.result as SignInMethodQueryResult;
                    val have_user =
                        queryResult.getSignInMethods() != null && queryResult.signInMethods?.size!! >0?:0
                    Log.i("Auth","Checking email "+have_user)
                    if (have_user ) {
                    showMessage("User is found with this email",true)
                        manageViewPager(null)

                    } else {
                        this.email=email;
                        manageViewPager(true)
                    }
                }  else {
                    Log.i("Auth","Checking email canceled ${ver_email}");

                }

                progressBar.cancel();

            }

    }
    fun  checkUsername(username:String){
        val reference: CollectionReference = FirebaseFirestore.getInstance().collection("Users")
        reference.whereEqualTo("username", username).get(Source.SERVER).addOnSuccessListener(
            OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                if (queryDocumentSnapshots.isEmpty) {
                  userName=username;
                    manageViewPager(true)
                } else {

                    showMessage("User name is taken , try another",true)
                    manageViewPager(null)
                }

                progressBar.cancel()
            })

    }




   inner class SignUpAdapter(var  pages: List<View>) :
        PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            Log.i("adapter",position.toString());
                var page=pages.get(position)
                when(position){
                    4->{
                        page.acadamey_selector.setOnItemSelectedListener(
                            object :OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                academy=resources.getStringArray(R.array.academy).get(position);

                                }

                               override fun onNothingSelected(parent: AdapterView<*>?) {


                               }
                    }
                        );
                    }
                        5->{
                        lateinit var selected_adapter:TagAdapter;
                        lateinit var tag_Adaptaer:TagAdapter;
                        selected_adapter=TagAdapter(java.util.ArrayList(), object : Selected {
                            override fun select(objects: Tag, selected: Boolean, poistion: Int) {
                                selectedTags.remove(objects)
                                tag_Adaptaer.addTag(objects );
                                selected_adapter.delete(objects);
                            }

                            override fun done() {
                                TODO("Not yet implemented")
                            }
                        },this@SignUp,true);
                            val layoutManager2 = FlexboxLayoutManager(this@SignUp)
                            layoutManager2.flexDirection = FlexDirection.ROW
                            layoutManager2.justifyContent = JustifyContent.SPACE_EVENLY

                        page.selected_tags.layoutManager=layoutManager2;
                        page.selected_tags.adapter=selected_adapter;
                        tag_Adaptaer=TagAdapter(Tag.tags, object : Selected {
                            override fun select(objects: Tag, selected: Boolean, poistion: Int) {
                                if (selected){
                                    selectedTags.add(objects)
                                    selected_adapter.addTag(objects)
                                   tag_Adaptaer.delete(objects)

                                }
                            }

                            override fun done() {
                            }
                        },this@SignUp,true);


                        val layoutManager = FlexboxLayoutManager(this@SignUp)
                        layoutManager.flexDirection = FlexDirection.ROW
                        layoutManager.justifyContent = JustifyContent.SPACE_EVENLY
                        page.all_tags.   setLayoutManager(layoutManager)
                        page.all_tags.adapter=tag_Adaptaer;

                    }

                }

            container.addView(page)
            return page
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view.equals(`object`)
        }

        override fun getCount(): Int {
            return pages.size
        }


    }

    fun layoutView(layout:Int):View{
        return LayoutInflater.from(this).inflate(layout, null, false);
    }

    fun showMessage(message:String,action:Boolean): Snackbar {
        var  snack=Snackbar.make(toolbar,message,Snackbar.LENGTH_SHORT);
        if (action){
            snack.setAction("Retry", View.OnClickListener { manageViewPager(true) })
        }
        snack.show();
    return snack;
    }
    fun createUserWithNameEmailAndPassword(email:String?,password:String?){
        if (email!=null && password!=null){
            Log.i("auth","validation is passed")
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( this) {task->

                        Log.i("auth","created completed")
                        if (task.isSuccessful()) {
                            Log.i("auth","created  successful")
                            val user=task.result?.user;
                            if (user!=null)
                            createProfile(user)
                            Log.i("auth","created  successful user ${user?.uid?:"null"}")


                        } else
                        {
                            Log.i("auth","created failed "+task.exception)
                            progressBar.cancel()
                            showMessage("Sorry cannot create account",false);

                        }
        }
    }



    }

    fun createProfile(user:FirebaseUser){
        progressBar.setMessage("Creating profile")
        var map=HashMap<String,Any>()
        map.put("username",userName!!)
        map.put("displayname",displayName!!)
        map.put("tags",Tag.toStringList(selectedTags))
        map.put("department",academy!!)
        map.put("verified",false)
        map.put("signUp_date",Timestamp.now().seconds);
        map.put("email", user.email!!)
        var doc=FirebaseFirestore.getInstance().collection("Users").document(user.uid);
        doc.set(map).addOnCompleteListener {
           progressBar.dismiss()
            showMessage("Account created successfully ",false).addCallback(object :
                Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                    startActivity(Intent(this@SignUp,EmailVerification::class.java))
                    finish()
                }
            })


        }.addOnCanceledListener{

            progressBar.dismiss()

        }

        }


        }



