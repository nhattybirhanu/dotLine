package com.dotline.activity.account

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dotline.R
import  android.view.View;
import android.widget.Toast
import com.dotline.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.email_sign_up.*
import kotlinx.android.synthetic.main.password_sign_up.*
import kotlinx.android.synthetic.main.sign_up.*

class LoginScreen:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)
    }
    fun signUp(view:View){
        startActivity(Intent(this,SignUp::class.java));
    }
    fun signIn(view:View){
        if (email_text.text?.isEmpty()?:true||password.text?.isEmpty()?:true){
           message("Please fill the empty fields")

        } else signInWithEmail(email_text.text.toString().plus("@miu.edu"),password = password.text.toString());
    }
    fun signInWithEmail(email:String,password:String){
        var auth=FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
            task->
            if (task.isSuccessful){
                var user=task.result?.user;
                var verified=user?.isEmailVerified?:let {
                    message("Please try again")

                };
                if (verified is Boolean){
                    if (verified)      startActivity(Intent(this, MainActivity::class.java))
                    else{
                    user?.sendEmailVerification();

                        startActivity(Intent(this, EmailVerification::class.java))
                    }
                    finish()

                }
            } else {
                message("your password or email  is incorrect")

            }
        }
    }
    fun  message(message:String){
        var  snack= Snackbar.make(email_text,message, Snackbar.LENGTH_SHORT);
        snack.show();
    }
}