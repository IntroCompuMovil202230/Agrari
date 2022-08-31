package com.example.agrari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class MessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)


        val arrayAdapter: ArrayAdapter<String>

        val personas = mutableListOf("Vendedor: Carlos Alfonso\nSe vende terreno plano\n$150.0000","Vendedor: Laura Torres\nSe vende terrreno para vegetales\n$220.000","Vendedor:Diego Montero\nSe vende terreno con buena vista\n$180,000")
        val lista =  findViewById<ListView>(R.id.lista)



        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,personas)
        lista.adapter = arrayAdapter

        lista.setOnItemClickListener(){parent,view,position,id->
            startActivity(Intent(this,ChatActivity::class.java))
        }


    }
}