package com.example.agrari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class SettingsActivity : AppCompatActivity() {

    lateinit var editProfile: Button
    lateinit var terms: Button
    lateinit var logout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        editProfile = findViewById(R.id.editarPerfil)
        terms = findViewById(R.id.terminos)
        logout = findViewById(R.id.cerrarSesion)


        logout.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        })

    }
}