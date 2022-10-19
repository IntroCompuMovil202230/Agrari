package com.example.agrari.controller

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthController {
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser():FirebaseUser?{
        return mAuth.currentUser
    }

    fun signOut(){
        mAuth.signOut()
    }

    fun createUserWithEmailPassword(email:String, password:String): Task<AuthResult> {
        if(!CredentialValidator.isEmail(email) || email.isEmpty() || password.isEmpty()){
            throw Exception("Correo o contraseña inválida")
        }
        return mAuth.createUserWithEmailAndPassword(email,password)
    }

    fun signInWithEmailPassword(email:String, password:String): Task<AuthResult> {
        if(!CredentialValidator.isEmail(email) || email.isEmpty() || password.isEmpty()){
            throw Exception("Correo o contraseña inválida")
        }
        return mAuth.signInWithEmailAndPassword(email,password)
    }

}