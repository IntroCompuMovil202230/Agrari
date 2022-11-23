package com.example.agrari.services

import com.example.agrari.Model.AgrariCategory
import com.example.agrari.Model.AgrariPost
import com.example.agrari.Model.AgrariUser
import com.example.agrari.Model.Chat
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DB_Service {

    val db = Firebase.firestore

    val userCollection: String = "users"
    val postsCollection: String = "posts"
    val chatsCollection: String = "chats"



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


    fun addNewChat(newChat: Chat){
        var reference:DocumentReference= db.collection(this.chatsCollection).document()
        newChat.uid=reference.id
        db.collection(this.chatsCollection).document(newChat.uid).set(newChat.toJson())
    }


    fun updateChat(currentChat: Chat){
        db.collection(this.chatsCollection).document(currentChat.uid).update(currentChat.toJson())
    }

    fun getChat(userUid: String, sellerUid: String):Query{
        return db.collection(this.chatsCollection).whereEqualTo("uid_user",userUid).whereEqualTo("uid_seller",sellerUid)
    }


    fun getUserChats(userUid: String):Query{
        return db.collection(this.chatsCollection).whereEqualTo("uid_user",userUid)
    }

    fun getSellerChats( sellerUid: String):Query{
        return db.collection(this.chatsCollection).whereEqualTo("uid_seller",sellerUid)
    }


    fun addNewPost(newPost:AgrariPost){
         var reference:DocumentReference= db.collection(this.postsCollection).document()
        newPost.uid=reference.id
        var reference2:DocumentReference = db.collection(this.postsCollection).document(newPost.uid)
        reference2.set(newPost.toJson())
    }



    fun getAllPosts(): Query {
        return db.collection(this.postsCollection)
    }


    fun getPostByMetrajeRange(lowerValue:Double, upperValue:Double): Query {
        return db.collection(this.postsCollection).whereGreaterThanOrEqualTo("area",lowerValue).whereLessThanOrEqualTo("area",upperValue)
    }


    fun getPostByCategory(category: String): Query {
        return db.collection(this.postsCollection).whereEqualTo("category",category)
    }


    fun getPostByDepartamento(departamento: String): Query {
        return db.collection(this.postsCollection).whereEqualTo("departamento",departamento)
    }




    fun getPostByDepartamentoCategoryRange(departamento: String,category: String,lowerValue:Double, upperValue:Double): Query {
        return db.collection(this.postsCollection).whereEqualTo("departamento",departamento).whereEqualTo("category",category).whereGreaterThanOrEqualTo("area",lowerValue).whereLessThanOrEqualTo("area",upperValue)
    }

    fun sellerPosts(sellerUid:String):Query{
        return db.collection(this.postsCollection).whereEqualTo("seller_uid",sellerUid)
    }

}