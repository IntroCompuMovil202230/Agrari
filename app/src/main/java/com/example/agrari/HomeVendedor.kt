package com.example.agrari

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.agrari.databinding.ActivityHomeVendedorBinding
import com.example.agrari.fragments.ChatFragment
import com.example.agrari.fragments.HomeVendedorFragment
import com.example.agrari.fragments.SettingsFragment
import com.example.agrari.fragments.UploadFragment

class HomeVendedor : AppCompatActivity() {

    lateinit var  binding: ActivityHomeVendedorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityHomeVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeVendedorFragment())

        binding.navegationBarVendedor.setOnItemReselectedListener {
            when(it.itemId){
                R.id.homeIconVendedor -> replaceFragment(HomeVendedorFragment())
                R.id.uploadIconVendedor -> replaceFragment(UploadFragment())
                R.id.MensajesIconVendedor -> replaceFragment(ChatFragment())
                R.id.settingsIconVendedor -> replaceFragment(SettingsFragment())
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