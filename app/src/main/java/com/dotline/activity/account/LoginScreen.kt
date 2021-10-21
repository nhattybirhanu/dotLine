package com.dotline.activity.account

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dotline.R
import  android.view.View;
import android.widget.Toast
import com.dotline.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.email_sign_up.*
import kotlinx.android.synthetic.main.email_sign_up.email_text
import kotlinx.android.synthetic.main.login_screen.*
import kotlinx.android.synthetic.main.password_sign_up.*
import kotlinx.android.synthetic.main.password_sign_up.password
import kotlinx.android.synthetic.main.profile_page_2.*
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

        } else
            signInWithEmail(email_text.text.toString(),password = password.text.toString());
    }
    fun signInWithEmail(inputemail:String, password:String){
        var auth=FirebaseAuth.getInstance();
        var email=inputemail;
        if (!email.contains("@miu.edu")) email=email.plus("@miu.edu")

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
            task->
            if (task.isSuccessful){
                var user=task.result?.user;
                var verified=user?.isEmailVerified?:let {
                    message("Please try again")

                };
                if (verified is Boolean){
                    if (verified)
                        startActivity(Intent(this, MainActivity::class.java))
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
    fun forgetPassword(view:View){
        if (email_text.text?.isEmpty() == true){
            message("Please fill the empty fields")

        } else {
            var email=email_text.text.toString();
            if (!email!!.contains("@miu.edu")) email=email.plus("@miu.edu")
            val auth=FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(email!!).addOnCompleteListener(OnCompleteListener {
                if (it.isSuccessful){
                    message("Password  reset link is sent to ${email}")
                } else
                    message("Password reset is failed")

            })

        }
    }
}