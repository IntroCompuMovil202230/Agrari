package com.example.agrari.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import com.example.agrari.Model.AgrariPost
import com.example.agrari.PublicacionAdapter
import com.example.agrari.PublicacionController
import com.example.agrari.R
import com.example.agrari.VerPublicacionActivity
import com.example.agrari.databinding.FragmentChatBinding
import com.example.agrari.databinding.FragmentHomeVendedorBinding
import com.example.agrari.services.AuthService
import com.example.agrari.services.DB_Service


class HomeVendedorFragment : Fragment() {
    var terrenosController: PublicacionController = PublicacionController()
    private var binding: FragmentHomeVendedorBinding?=null
    private  var dbService: DB_Service =DB_Service()
    private  var authService:AuthService=AuthService()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeVendedorBinding.inflate(layoutInflater,container,false)

        this.dbService.sellerPosts(authService.getCurrentUser()!!.uid).addSnapshotListener { value, error ->

            Log.w("LISTENER-DATA", "Listening the data...")

            if (error != null) {
                Log.w("LISTENER-ERROR", "Listen failed.", error)
                return@addSnapshotListener
            }

            var posts= mutableListOf<AgrariPost>()
            println(value!!.size())

            for (doc in value!!) {
                Log.i("USERS-COLLECTION-DATA", "NAME: ${doc.getString("title")}")
                posts.add(AgrariPost(doc))
            }


            binding!!.terrenosGridxHomeVendedor.adapter= PublicacionAdapter(requireActivity(),posts)
            binding!!.terrenosGridxHomeVendedor.setOnItemClickListener { parent, view, position, id ->
                var intent= Intent(activity,VerPublicacionActivity::class.java)
                intent.putExtra("post",posts.elementAt(position))
                startActivity(intent)
            }


        }



        return binding!!.root
    }

}