package com.example.agrari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.agrari.databinding.ActivityLoginBinding
import com.example.agrari.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var recoverPasswordText: TextView
    lateinit var loginButton: Button
    lateinit var mAuth: FirebaseAuth
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.mAuth=FirebaseAuth.getInstance()
        binding.recoverPasswordText.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, RecoverPasswordActivity:: class.java))
        })
        this.binding.login.setOnClickListener(View.OnClickListener {
            this.mAuth.signInWithEmailAndPassword(this.binding.loginEmailInput.text.toString(),this.binding.loginPasswordInput.text.toString()).addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.w("SIGN-UP-CONTROLLER", "signInWithEmail:failed", it.exception)
                    Toast.makeText(this, "El usuario no existe", Toast.LENGTH_SHORT).show()
                    this.binding.loginEmailInput.setText("");
                    this.binding.loginPasswordInput.setText("");
                }else{
                    startActivity(Intent(this, HomeActivity::class.java))
                }
            }
        })
    }
}