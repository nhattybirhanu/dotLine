package com.dotline.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.dotline.MainActivity
import com.dotline.R
import com.dotline.activity.account.EmailVerification
import com.dotline.activity.account.LoginScreen
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.sign_up.*

class Splash:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash);
        Handler().postDelayed(Runnable {
             var user=FirebaseAuth.getInstance().currentUser;
              if (user==null){
                  startActivity(Intent(this@Splash,LoginScreen::class.java))
              }  else if (user.isEmailVerified){
                  startActivity(Intent(this@Splash,MainActivity::class.java))

              } else{
                  startActivity(Intent(this@Splash,EmailVerification::class.java))

              }
            finish()

        },1000)
    }
    fun  message(message:String){
        var  snack= Snackbar.make(toolbar,message, Snackbar.LENGTH_SHORT);
        snack.show();
    }
}