package com.example.agrari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton

class HomeVendedor : AppCompatActivity() {
    lateinit var  pub1: ImageButton
    lateinit var  pub2: ImageButton
    lateinit var  pub3: ImageButton
    lateinit var  pub4: ImageButton
    lateinit var  subir: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_vendedor)

        pub1 =  findViewById(R.id.publicacion1)
        
    }
}