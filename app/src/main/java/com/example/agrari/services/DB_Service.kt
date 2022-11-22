package com.example.agrari.services

import com.example.agrari.Model.AgrariPost
import com.example.agrari.Model.AgrariUser
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DB_Service {

    val db = Firebase.firestore

    val userCollection: String = "users"
    val postsCollection: String = "posts"



    fun updateAvailableStatus(userUid: String, status:Boolean){

        db.collection(this.userCollection).document(userUid).update(mapOf(
            "available" to status
        ))
    }

    fun updateLocation(userUid: String, location: LatLng){
        db.collection(this.userCollection).document(userUid).update(mapOf(
            "latitude" to location.latitude,
            "longitud" to location.longitude
        ))
    }

    fun addNewUser(newUser: AgrariUser){
        db.collection(this.userCollection).document(newUser.uid).set(newUser.toJson())
    }


    fun addNewPost(newPost:AgrariPost){
         var reference:DocumentReference= db.collection(this.postsCollection).document()
        newPost.uid=reference.id
        db.collection(this.postsCollection).document(newPost.uid).set(newPost.toJson())
    }



    fun getAllPosts(): Query {
        return db.collection(this.postsCollection)
    }

    fun sellerPosts(sellerUid:String):Query{
        return db.collection(this.postsCollection).whereEqualTo("seller_uid",sellerUid)
    }

}