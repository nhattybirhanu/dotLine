package com.dotline.activity.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dotline.MainActivity
import com.dotline.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.email_verification.*

class EmailVerification:AppCompatActivity() {
    var user:FirebaseUser?=null;
    lateinit var auth:FirebaseAuth;
    override fun onStart() {
        auth=FirebaseAuth.getInstance();
        user= auth.currentUser;
        super.onStart()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_verification);
        if (user!=null){

            message.setText("We sent email verification to ${user?.email}")
            auth.addAuthStateListener {
                user=it.currentUser;
                if (user!=null){
                    if (user!!.isEmailVerified)
                        startActivity(Intent(this, MainActivity::class.java))
                    finish()

                }

            }
        }



    }
    fun  logoOut(view:View){
        FirebaseAuth.getInstance().signOut();
        finish()
    }
    fun  sendAgain(view:View){
        sendEmailVerification()
    }

    fun  sendEmailVerification(){
        user?.sendEmailVerification()?.addOnCompleteListener(this){
                task->
            if (task.isSuccessful){
                message("Email verification sent Successful")
            } else
                message("Sorry sending email verification is failed, try again "
                )


        }
    }

    fun  message(message:String){
        var  snack= Snackbar.make(toolbar,message, Snackbar.LENGTH_SHORT);
        snack.show();
    }

    override fun onResume() {
         user?.reload()?.addOnCompleteListener(this) {

             user= FirebaseAuth.getInstance().currentUser;
             user?.isEmailVerified.let {
        if (it==true){
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }
             }
        };
        super.onResume()
    }

}