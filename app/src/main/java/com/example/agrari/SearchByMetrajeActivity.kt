package com.example.agrari

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.slider.RangeSlider

class SearchByMetrajeActivity : AppCompatActivity() {

    lateinit var metrajeRange: RangeSlider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_metraje)
        metrajeRange= findViewById(R.id.metrajeRangeSlider)
    }
}