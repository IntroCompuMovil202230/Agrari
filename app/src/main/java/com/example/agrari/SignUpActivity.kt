package com.example.agrari

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.agrari.controller.AuthController
import com.example.agrari.databinding.ActivitySignUpBinding
import com.example.agrari.databinding.ActivitySignUpRamainInfoBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.logging.Logger


class SignUpActivity : AppCompatActivity() {


    lateinit var binding: ActivitySignUpBinding
    lateinit var authController: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authController= AuthController()
        this.binding.signUpContinueButton.setOnClickListener(View.OnClickListener {
            try {
                this.authController.createUserWithEmailPassword(this.binding.signUpEmailInput.text.toString(),this.binding.signUpPasswordInput.text.toString()).addOnCompleteListener {
                    if (!it.isSuccessful) {
                        Log.w("SIGN-UP-CONTROLLER", "signInWithEmail:failed", it.exception)
                        Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                        this.binding.signUpEmailInput.setText("");
                        this.binding.signUpPasswordInput.setText("");
                    }else{
                        startActivity(Intent(this,SignUpRamainInfoActivity::class.java))
                    }
                }
            }catch (e:Exception){
                Toast.makeText(this,e.toString().substring(21),Toast.LENGTH_LONG).show()
                this.binding.signUpEmailInput.setText("");
                this.binding.signUpPasswordInput.setText("");
            }
        })
    }
}