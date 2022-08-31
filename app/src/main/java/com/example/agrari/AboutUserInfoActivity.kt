package com.example.agrari

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.InputStream

class AboutUserInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_user_info)
        var stream: InputStream = this.assets.open(("paises.json"))
    }
}