package com.example.agrari.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.agrari.MainActivity
import com.example.agrari.ProfileActivity
import com.example.agrari.controller.AuthController
import com.example.agrari.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {
    private var binding: FragmentSettingsBinding?=null
    lateinit var authController: AuthController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authController=AuthController()
        binding= FragmentSettingsBinding.inflate(layoutInflater,container,false)
        binding!!.editarPerfil.setOnClickListener(View.OnClickListener {
            activity?.startActivity(Intent(activity, ProfileActivity::class.java))
        })

        binding!!.cerrarSesion.setOnClickListener(View.OnClickListener {
            authController.signOut()
            val i = Intent(activity, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity?.startActivity(i)
        })
        return binding!!.root
    }


}