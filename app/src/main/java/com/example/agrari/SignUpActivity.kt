package com.example.agrari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import java.util.logging.Logger

class SignUpActivity : AppCompatActivity() {

    lateinit var continueButton : Button
    val LOG = Logger.getLogger(this.javaClass.name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        continueButton= findViewById(R.id.signUpContinueButton)
        continueButton.setOnClickListener(View.OnClickListener {
            LOG.info("Continue to choose user type.")
            startActivity(Intent(this,ChooseTypeOfUserActivity::class.java))
            //startActivity(Intent(this, ChooseTypeOfUserActivity::class.java))
        })
    }
}