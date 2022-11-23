package com.example.agrari.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.agrari.ChatActivity
import com.example.agrari.ChatAdapter
import com.example.agrari.MessageAdapter
import com.example.agrari.Model.Chat
import com.example.agrari.databinding.FragmentChatBinding
import com.example.agrari.services.AuthService
import com.example.agrari.services.DB_Service


class ChatFragment : Fragment() {

    private var binding: FragmentChatBinding?=null
    lateinit var authService: AuthService
    private  lateinit var dbService: DB_Service

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentChatBinding.inflate(layoutInflater,container,false)

        val activity: Activity? = activity
        if(isAdded && activity != null){


            authService= AuthService()
            dbService= DB_Service()


            if(authService.getCurrentUser()!!.displayName!!.contains("_AgrariJustUser")){
                dbService.getUserChats(authService.getCurrentUser()!!.uid).addSnapshotListener { value, error ->

                    Log.w("LISTENER-DATA", "Listening the data...")

                    if (error != null) {
                        Log.w("LISTENER-ERROR", "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    var chats = mutableListOf<Chat>()
                    for (doc in value!!) {
                        Log.i("CHAT-DATA:", "USER_ID: ${doc.getString("uid_user")} SELLER_ID: ${doc.getString("uid_seller")} ")
                        chats.add(Chat(doc))
                    }

                    binding!!.lista.adapter = ChatAdapter(activity,chats)
                    binding!!.lista.setOnItemClickListener(){parent,view,position,id->
                        var intent = Intent(activity, ChatActivity::class.java)
                        intent.putExtra("user_uid",authService.getCurrentUser()!!.uid)
                        intent.putExtra("seller_uid",chats.get(position).uid_seller)
                        startActivity(intent)
                    }
                }
            }else{
                dbService.getSellerChats(authService.getCurrentUser()!!.uid).addSnapshotListener { value, error ->

                    Log.w("LISTENER-DATA", "Listening the data...")

                    if (error != null) {
                        Log.w("LISTENER-ERROR", "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    var chats = mutableListOf<Chat>()
                    for (doc in value!!) {
                        Log.i("CHAT-DATA:", "USER_ID: ${doc.getString("uid_user")} SELLER_ID: ${doc.getString("uid_seller")} ")
                        chats.add(Chat(doc))
                    }

                    binding!!.lista.adapter = ChatAdapter(activity,chats)
                    binding!!.lista.setOnItemClickListener(){parent,view,position,id->
                        var intent = Intent(activity, ChatActivity::class.java)
                        intent.putExtra("seller_uid",authService.getCurrentUser()!!.uid)
                        intent.putExtra("user_uid",chats.get(position).uid_user)
                        startActivity(intent)
                    }
                }
            }


        }

        return binding!!.root
    }

}