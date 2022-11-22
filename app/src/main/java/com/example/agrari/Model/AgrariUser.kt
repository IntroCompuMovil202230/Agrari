package com.example.agrari.Model

import com.google.firebase.firestore.QueryDocumentSnapshot

class AgrariUser {

    var uid: String
    var profileImage:String
    var name:String
    var lastName:String
    var email:String
    var phoneNumber:String


    constructor(
        uid: String,
        profileImage: String,
        name: String,
        lastName: String,
        email: String,
        phoneNumber: String
    ) {
        this.uid = uid
        this.profileImage = profileImage
        this.name = name
        this.lastName = lastName
        this.email = email
        this.phoneNumber = phoneNumber
    }

    constructor(doc: QueryDocumentSnapshot){
        this.uid = doc.getString("uid")!!
        this.profileImage = doc.getString("profileImage")!!
        this.name = doc.getString("name")!!
        this.lastName = doc.getString("lastName")!!
        this.email = doc.getString("email")!!
        this.phoneNumber = doc.getString("phoneNumber")!!
    }

    fun toJson():HashMap<String, Any>{
        return  hashMapOf(
            "uid" to this.uid,
            "profileImage" to this.profileImage,
            "name" to this.name,
            "lastName" to this.lastName,
            "email" to this.email,
            "phoneNumber" to  this.phoneNumber,
        )
    }

}