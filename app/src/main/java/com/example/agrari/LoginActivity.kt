package com.example.agrari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class LoginActivity : AppCompatActivity() {

    lateinit var recoverPasswordText: TextView
    lateinit var loginButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        recoverPasswordText= findViewById(R.id.recoverPasswordText)
        recoverPasswordText.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, RecoverPasswordActivity:: class.java))
        })

        loginButton = findViewById(R.id.login)
        loginButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        })
    }
}