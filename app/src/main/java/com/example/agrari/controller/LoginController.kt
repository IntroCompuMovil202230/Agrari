package com.example.agrari.controller
import com.example.agrari.services.AuthService
import com.example.agrari.services.CredentialValidator
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

class LoginController {

    var authService: AuthService = AuthService()

    fun validateLoginEmailPassword(email:String, password:String):Boolean{
        if(email.isEmpty() ||  !CredentialValidator.isEmail(email)){
            throw Exception("Invalid email")
        }else if(password.isEmpty() || password.length<8){
            throw Exception("Invalid password (Must have at least 8 characters)")
        }else{
            return true
        }
    }


    fun loginWithEmailPassword(email:String, password:String): Task<AuthResult> {
        return authService.signInWithEmailPassword(email,password)
    }


}