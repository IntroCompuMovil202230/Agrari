package com.example.agrari

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    lateinit var locationButton: Button
    lateinit var metrajeButton: Button
    lateinit var advanceSearchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        locationButton= findViewById(R.id.porUbicacion)
        metrajeButton= findViewById(R.id.porMetraje)
        advanceSearchButton= findViewById(R.id.busqAvanzada)

        locationButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, SearchByLocationActivity::class.java))
        })
        metrajeButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, SearchByMetrajeActivity::class.java))
        })
        advanceSearchButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, AdvanceSearchActivity::class.java))
        })
    }
}