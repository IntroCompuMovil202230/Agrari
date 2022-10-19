package com.example.agrari.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.agrari.ChatActivity
import com.example.agrari.databinding.FragmentChatBinding


class ChatFragment : Fragment() {

    private var binding: FragmentChatBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentChatBinding.inflate(layoutInflater,container,false)


        val arrayAdapter: ArrayAdapter<String>

        val personas = mutableListOf("Vendedor: Carlos Alfonso\nSe vende terreno plano\n$150.0000","Vendedor: Laura Torres\nSe vende terrreno para vegetales\n$220.000","Vendedor:Diego Montero\nSe vende terreno con buena vista\n$180,000")

        arrayAdapter = ArrayAdapter(requireActivity(),android.R.layout.simple_expandable_list_item_1,personas)
        binding!!.lista.adapter = arrayAdapter

        binding!!.lista.setOnItemClickListener(){parent,view,position,id->
            activity?.startActivity(Intent(activity, ChatActivity::class.java))
        }



        return binding!!.root
    }

}