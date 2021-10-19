package com.dotline.activity

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dotline.R
import android.view.View;
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dotline.UploadManager
import com.dotline.Util.QuestionCreatorUtil
import com.dotline.activity.account.EmailVerification
import com.dotline.callbacks.TaskIsDone
import com.dotline.model.ImageConfig
import com.dotline.model.Profile
import com.dotline.viewModels.UserProfileModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.edit_your_profile.*

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditProfileActivity:AppCompatActivity() {
    var requestCode=101;
    var uri:Uri?=null;
    var profileUrl:String?=null;
    lateinit var userProfileModel:UserProfileModel;
  var profile: Profile?=null;
    var updated_DiplayName:String?=null;
    var updated_UserName:String?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.edit_your_profile)
        super.onCreate(savedInstanceState)
        userProfileModel=ViewModelProvider(this).get(UserProfileModel::class.java);
        userProfileModel.profileLiveData().observe(this, Observer {
            if (it != null) {
                profile=it
                display_name.setText(profile!!.displayName)
                username.setText(profile!!.userName)
                Glide.with(image_view).load(profile!!.profile_picture).placeholder(R.drawable.ic_default_picture).circleCrop().into(image_view);
            };

        })
        toolbar.setNavigationOnClickListener {  close()}
    }
    fun close(){
        var dialog= AlertDialog.Builder(this);
        dialog.setTitle("Do you want close editing  ?")
        dialog.setPositiveButton("Close", DialogInterface.OnClickListener(){
                dialog, which ->
            dialog.dismiss()
            finish()
        })
        dialog.setNegativeButton("Continue editing ", DialogInterface.OnClickListener(){
                dialog, which ->
            dialog.dismiss()
        })

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==this.requestCode&&resultCode== RESULT_OK){
            var uri= data?.data;
            uri?.let {  this.uri=uri;
                Glide.with(image_view).load(uri).placeholder(R.drawable.ic_default_picture).circleCrop().into(image_view);
            }
        }
    }
    fun pickImage(view:View){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
    }
    fun update(view:View){
        var upate_fonund=false;
        if (profile!=null){

            if (display_name.text.isNotEmpty()&&!profile!!.displayName.equals(display_name.text.toString())){
                updated_DiplayName=display_name.text.toString();
                upate_fonund=true;
            }

            if (username.text.isNotEmpty()&&!profile!!.userName.equals(username.text.toString())){
                updated_UserName=username.text.toString();
                upate_fonund=true;
            }
             var progess=ProgressDialog(this);
            if (upate_fonund){
                progess.setTitle("Updating profile")
                progess.setCancelable(false)
                progess.show();
                if (updated_UserName!=null){
                    progess.setMessage("Checking the username")
                    checkUsername(updated_UserName!!,object :TaskIsDone{
                        override fun result(successful: Boolean) {
                            if (successful){
                                checkForProfilePicture(progess)
                            } else {
                                updated_UserName=null;
                            }
                        }
                    })
                } else
                    checkForProfilePicture(progess)
            } else

                checkForProfilePicture(progess)



        }
    }
        fun checkForProfilePicture(progess:ProgressDialog){
            if (uri!=null){
                progess.setMessage("Compressing image")

                GlobalScope.launch {
                    if (Looper.myLooper() == null)
                        Looper.prepare();
                    var images= QuestionCreatorUtil.compreesImages(arrayListOf(uri!!), this@EditProfileActivity,)
                    if (images.size>0){
                        progess.setMessage("Uploading")
                        UploadManager.upload_ProfilePicture(images.get(0),object :UploadManager.Task{
                            override fun taskisdone(
                                done: Boolean,
                                data: Map<String, ArrayList<String>>
                            ) {
                                if (done){
                                    if (data.containsKey(UploadManager.PROFILE)){
                                        var list=data.get(UploadManager.PROFILE);
                                        if (list!!.size>0){
                                            profileUrl=list.get(0)

                                        } else {
                                            showMessage("Uploading profile picture is failed")
                                            updateToDatabase(progess)

                                        }
                                    }
                                } else
                                    showMessage("Uploading profile picture is failed")
                                updateToDatabase(progess)

                            }

                        })

                    } else {
                        showMessage("Uploading profile picture is failed")
                        updateToDatabase(progess)

                    }


                }
            }
            else
                updateToDatabase(progess)

        }
    fun updateToDatabase(progess: ProgressDialog){
        progess.setMessage("Uploading data")
        var data=HashMap<String,Any>();
        if (updated_DiplayName!=null) data.put("displayname",updated_DiplayName!!)
        if (updated_UserName!=null)
            data.put("username",updated_UserName!!)
        if (profileUrl!=null)            data.put("profile_picture",profileUrl!!)
        if (profile!=null){
            var docu=FirebaseFirestore.getInstance().collection("Users").document(profile!!.id);
            docu.update(data).addOnCompleteListener(OnCompleteListener {
                if (it.isSuccessful){
                    showMessage("Updated Successful").addCallback(object :
                        Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            finish()
                        }
                    })
                } else
                    showMessage("Updating failed")
                progess.dismiss()
            })
        }

    }
    fun showMessage(message:String): Snackbar {
        var  snack= Snackbar.make(toolbar,message, Snackbar.LENGTH_SHORT);

        snack.show();
        return snack;
    }
    fun  checkUsername(username:String,taskIsDone: TaskIsDone){
        val reference: CollectionReference = FirebaseFirestore.getInstance().collection("Users")
        reference.whereEqualTo("username", username).get(Source.SERVER).addOnSuccessListener(
            OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
                if (queryDocumentSnapshots.isEmpty) {
                    updated_UserName=username;
                    taskIsDone.result(true)
                } else {
                    taskIsDone.result(false)
                    showMessage("User name is taken , try another")
                }


            })

    }




}