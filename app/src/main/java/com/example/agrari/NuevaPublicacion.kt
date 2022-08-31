package com.example.agrari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NuevaPublicacion : AppCompatActivity() {

    lateinit var getMentrajeButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_publicacion)
        getMentrajeButton= findViewById(R.id.getMentrajeButton)
        getMentrajeButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, MapaMetraje::class.java))
        })
    }
}