package com.example.agrari.services

import android.text.TextUtils
import org.intellij.lang.annotations.RegExp

class CredentialValidator {

     companion object {
          fun isEmail(email:String):Boolean{
               return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
          }
     }




}