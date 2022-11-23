package com.example.agrari.Model

import com.google.firebase.firestore.QueryDocumentSnapshot

class Message {

    var uid_author: String
    var text: String


    constructor(uid_author: String, text: String) {
        this.uid_author = uid_author
        this.text = text
    }




   constructor(data : HashMap<String, Any>){
        this.uid_author= data["uid_author"].toString()
        this.text= data["text"].toString()

    }


    fun toJson():HashMap<String, Any>{
        return  hashMapOf(
            "uid_author" to this.uid_author,
            "text" to this.text,
        )
    }



}