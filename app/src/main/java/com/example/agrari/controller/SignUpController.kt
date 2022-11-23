package com.example.agrari.controller
import android.location.Location
import com.example.agrari.Model.AgrariUser
import com.example.agrari.services.AuthService
import com.example.agrari.services.CredentialValidator
import com.example.agrari.services.DB_Service
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult


class SignUpController {

    var authService: AuthService = AuthService()
    var dbService: DB_Service = DB_Service()


    fun saveUserSignUpInfo(id:String,email:String, name:String, lastName:String,phoneNumber:String, profileImage:String):Boolean{
        if(id.isEmpty() || id.length<8){
            throw Exception("Invalid id")
        }else if(email.isEmpty() ||  !CredentialValidator.isEmail(email)){
            throw Exception("Invalid email")
        }else if(name.isEmpty()){
            throw Exception("Invalid name")
        }else if(lastName.isEmpty()){
            throw Exception("Invalid last name")
        }else if(phoneNumber.isEmpty()){
            throw Exception("Invalid  number")
        }else if(profileImage.isEmpty()){
            throw Exception("Invalid image")
        }else{
            dbService.addNewUser(AgrariUser(id,profileImage,name,lastName,email,phoneNumber))
            return true
        }
    }


    fun signUpWithEmailPassword(email:String, password:String): Task<AuthResult> {
       return authService.createUserWithEmailPassword(email,password)
    }

    /*fun addUserToDB(){
        dbService.addNewUser(this.currentUser)
    }*/





}