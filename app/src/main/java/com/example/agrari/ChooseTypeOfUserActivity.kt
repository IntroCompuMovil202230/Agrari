package com.example.agrari

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton

class ChooseTypeOfUserActivity : AppCompatActivity() {

    lateinit var  buyerButton: ImageButton
    lateinit var  sellerButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_type_of_user)
        buyerButton= findViewById(R.id.buyerButton)
        sellerButton= findViewById(R.id.sellerButton)

        buyerButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,AboutUserInfoActivity::class.java))
        })

        sellerButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,HomeVendedor::class.java))
        })

    }
}