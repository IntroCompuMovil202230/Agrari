package com.example.agrari.Model

import com.google.firebase.firestore.QueryDocumentSnapshot

class Chat {

    lateinit var uid: String
    var uid_user: String
    var uid_seller: String
    lateinit var messages:MutableList<Message>

    constructor(uid_user: String, uid_seller: String) {
        this.uid_user = uid_user
        this.uid_seller = uid_seller
    }



    constructor(doc: QueryDocumentSnapshot){
        this.uid= doc.getString("uid")!!
        this.uid_user= doc.getString("uid_user")!!
        this.uid_seller= doc.getString("uid_seller")!!
        this.messages= mutableListOf<Message>()
        this.messages = deserializeMessages(doc.get("messages") as ArrayList<HashMap<String, Any>>)
        println("The class of the messages is: ${doc.get("messages")!!.javaClass}");
    }


    fun toJson():HashMap<String, Any>{
        return  hashMapOf(
            "uid" to this.uid,
            "uid_user" to this.uid_user,
            "uid_seller" to this.uid_seller,
            "messages" to  serializeMessages(),
        )
    }

    private fun serializeMessages(): List<HashMap<String, Any>>{
        var serializedMess= mutableListOf<HashMap<String, Any>>()
        println("NEW MESSAGE SIZE: ${this.messages.size}")
        for (message in this.messages){
            println("NEW MESSAGE INFO: ${message.uid_author}:${message.text}")
            serializedMess.add(message.toJson())
        }
        return serializedMess
    }


    private fun deserializeMessages(messgs: ArrayList<HashMap<String, Any>>): MutableList<Message>{

        var serializedMess= mutableListOf<Message>()
        println("NEW MESSAGE SIZE: ${this.messages.size}")
        for (messageData in messgs){
            var m = Message(messageData)
            serializedMess.add(m)
        }
        return serializedMess
    }

}