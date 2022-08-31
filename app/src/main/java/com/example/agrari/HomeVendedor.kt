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

    override fun onCreate(savedInstanceState: Bundle?) {

        pub1 =  findViewById(R.id.publicacion1)
        pub2 =  findViewById(R.id.publicacion2)
        pub3 =  findViewById(R.id.publicacion3)
        pub4 =  findViewById(R.id.publicacion4)
        pub1.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, Publicacion::class.java))
        })
        pub2.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, Publicacion::class.java))
        })
        pub3.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, Publicacion::class.java))
        })
        pub4.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, Publicacion::class.java))
        })



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_vendedor)
    }
}