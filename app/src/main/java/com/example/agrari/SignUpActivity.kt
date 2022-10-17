package com.example.agrari

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.agrari.databinding.ActivitySignUpBinding
import com.example.agrari.databinding.ActivitySignUpRamainInfoBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.logging.Logger


class SignUpActivity : AppCompatActivity() {


    lateinit var binding: ActivitySignUpBinding
    lateinit var mAuth: FirebaseAuth

    val LOG = Logger.getLogger(this.javaClass.name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.mAuth=FirebaseAuth.getInstance()
        this.binding.signUpContinueButton.setOnClickListener(View.OnClickListener {
            this.mAuth.createUserWithEmailAndPassword(this.binding.signUpEmailInput.text.toString(),this.binding.signUpPasswordInput.text.toString()).addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.w("SIGN-UP-CONTROLLER", "signInWithEmail:failed", it.exception)
                    Toast.makeText(this, "El usuario no existe", Toast.LENGTH_SHORT).show()
                    this.binding.signUpEmailInput.setText("");
                    this.binding.signUpPasswordInput.setText("");
                }else{
                    startActivity(Intent(this,SignUpRamainInfoActivity::class.java))
                }
            }
        })
    }
}