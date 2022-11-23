package com.example.agrari

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.agrari.Model.Chat



class ChatAdapter (private  val mContext: Context, private val chatsList: List<Chat>):
    ArrayAdapter<Chat>(mContext,0,chatsList) {
    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val ly= LayoutInflater.from(mContext).inflate(R.layout.chat_item,parent,false)
        var chat= chatsList[position]
        ly.findViewById<TextView>(R.id.userIdChat).text= "Usuario: ${chat.uid_user}"
        ly.findViewById<TextView>(R.id.sellerIdChat).text="Vendedor: ${chat.uid_seller}"
        return ly
    }
}