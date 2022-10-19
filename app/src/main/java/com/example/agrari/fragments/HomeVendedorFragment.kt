package com.example.agrari.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import com.example.agrari.PublicacionAdapter
import com.example.agrari.PublicacionController
import com.example.agrari.R
import com.example.agrari.VerPublicacionActivity
import com.example.agrari.databinding.FragmentChatBinding
import com.example.agrari.databinding.FragmentHomeVendedorBinding


class HomeVendedorFragment : Fragment() {
    var terrenosController: PublicacionController = PublicacionController()
    private var binding: FragmentHomeVendedorBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeVendedorBinding.inflate(layoutInflater,container,false)
        var terrenos= terrenosController.getTerrenos(activity?.assets!!.open(("terrenos.json")))
        binding!!.terrenosGridxHomeVendedor.adapter= PublicacionAdapter(requireActivity(),terrenos)
        binding!!.terrenosGridxHomeVendedor.setOnItemClickListener { parent, view, position, id ->
            var intent= Intent(activity, VerPublicacionActivity::class.java)
            intent.putExtra("publicacion",terrenos.elementAt(position))
            startActivity(intent)
        }
        return binding!!.root
    }

}