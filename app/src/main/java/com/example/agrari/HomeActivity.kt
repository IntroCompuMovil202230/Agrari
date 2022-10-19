package com.example.agrari

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.agrari.databinding.ActivityHomeBinding
import com.example.agrari.fragments.ChatFragment
import com.example.agrari.fragments.HomeFragment
import com.example.agrari.fragments.SettingsFragment


class HomeActivity : AppCompatActivity() {


    lateinit var  binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.navegationBar.setOnItemReselectedListener {
            when(it.itemId){
                R.id.homeIcon -> replaceFragment(HomeFragment())
                R.id.MensajesIcon -> replaceFragment(ChatFragment())
                R.id.settingsIcon -> replaceFragment(SettingsFragment())
            }
        }

    }

    private  fun replaceFragment(fragment: Fragment){
        val fragmentManager= supportFragmentManager
        val fragmentTransaction= fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.layoutNavigatorVendedor,fragment)
        fragmentTransaction.commit()
    }


}