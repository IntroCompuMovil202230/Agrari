package com.example.agrari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import com.google.android.material.bottomnavigation.BottomNavigationItemView

class HomeVendedor : AppCompatActivity() {
    lateinit var  pub1: ImageButton
    lateinit var uploadButton: BottomNavigationItemView
    lateinit var settingsButton: BottomNavigationItemView
    lateinit var chatButton: BottomNavigationItemView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_vendedor)

        pub1 =  findViewById(R.id.publicacion1)
        uploadButton= findViewById(R.id.uploadIconVendedor)
        settingsButton= findViewById(R.id.settingsIconVendedor)
        chatButton= findViewById(R.id.MensajesIconVendedor)

        uploadButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, NuevaPublicacion::class.java))
        })

        settingsButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        })


        chatButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, MessagesActivity::class.java))
        })


    }
}