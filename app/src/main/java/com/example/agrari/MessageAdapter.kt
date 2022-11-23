package com.example.agrari

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.agrari.Model.Message


class MessageAdapter (private  val mContext: Context, private val messagesList: List<Message>):
    ArrayAdapter<Message>(mContext,0,messagesList) {
    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val ly= LayoutInflater.from(mContext).inflate(R.layout.message_item,parent,false)
        var message= messagesList[position]
        ly.findViewById<TextView>(R.id.authorId).text=message.uid_author
        ly.findViewById<LinearLayout>(R.id.messageBox).setBackgroundColor(ContextCompat.getColor(mContext, R.color.DarkGreen))
        ly.findViewById<TextView>(R.id.messageText).text=message.text
        return ly
    }
}