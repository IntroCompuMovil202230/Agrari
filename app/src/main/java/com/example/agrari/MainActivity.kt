package com.example.agrari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.agrari.controller.AuthController
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    lateinit var loginButton: Button
    lateinit var signUpText: TextView
    lateinit var authController: AuthController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginButton= findViewById(R.id.welcomeScreenLoginButton)
        signUpText= findViewById(R.id.welcomeScreenSignUpText)
        authController=AuthController()


        if(authController.getCurrentUser()!=null){
            //startActivity(Intent(this,HomeActivity::class.java))
            startActivity(Intent(this,HomeVendedor::class.java))
        }

        loginButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        })

        signUpText.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        })
    }
}