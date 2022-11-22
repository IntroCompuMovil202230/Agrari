package com.example.agrari.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.agrari.AdvanceSearchActivity
import com.example.agrari.CategoryResultActivity
import com.example.agrari.SearchByLocationActivity
import com.example.agrari.SearchByMetrajeActivity
import com.example.agrari.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentHomeBinding.inflate(layoutInflater,container,false)



        binding!!.porUbicacion.setOnClickListener(View.OnClickListener {
            activity?.startActivity(Intent(activity, SearchByLocationActivity::class.java))
        })

        binding!!.porMetraje.setOnClickListener(View.OnClickListener {
            activity?.startActivity(Intent(activity, SearchByMetrajeActivity::class.java))
        })


        binding!!.busqAvanzada.setOnClickListener(View.OnClickListener {
            activity?.startActivity(Intent(activity, AdvanceSearchActivity::class.java))
        })


        binding!!.sandia.setOnClickListener(View.OnClickListener {
            activity?.startActivity(Intent(activity, CategoryResultActivity::class.java))
        })

        return binding!!.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}