package com.example.agrari.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.example.agrari.MapaMetraje
import com.example.agrari.ProfileActivity
import com.example.agrari.R
import com.example.agrari.databinding.FragmentChatBinding
import com.example.agrari.databinding.FragmentUploadBinding
import java.io.File


class UploadFragment : Fragment() {

    companion object {
        private val CAMERA_CODE = 1007;
        private val GALERY_CODE = 1006;
    }

    lateinit var uriCamera: Uri
    private var binding: FragmentUploadBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentUploadBinding.inflate(layoutInflater,container,false)

        binding!!.getMentrajeButton.setOnClickListener(View.OnClickListener {
            activity?.startActivity(Intent(activity, MapaMetraje::class.java))
        })


        binding!!.uploadCameraButton.setOnClickListener(View.OnClickListener {
            this.openCamera()
            Log.i("GALERY-CONTROLLER", "ACCESING GALLERY")

        })
        binding!!.uploadGaleryButton.setOnClickListener(View.OnClickListener {
            Log.i("CAMERA-CONTROLLER", "ACCESING CAMERA")
            this.openGallery()
        })


        return binding!!.root
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            GALERY_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    this.selectImageIntent.launch("image/*")
                }
            }
            CAMERA_CODE ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    var file : File = File(requireActivity().filesDir, "picFromCamera");
                    uriCamera = FileProvider.getUriForFile(requireActivity(), requireActivity().applicationContext.packageName + ".fileprovider", file);
                    this.selectFromCameraIntent.launch(uriCamera)
                }
            }
        }
    }


    ///////GALLERY////////////

    val selectImageIntent = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri ->
        if(uri != null){
            Log.i("GALLERY URI", "The URI IS: $uri")
            binding!!.uploadImage.setImageURI(uri)
        }
    }

    private fun openGallery(){
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        when {
            requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                this.selectImageIntent.launch("image/*")
            }
            /*shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) -> {
                this.showSnackBar("Debes aceptar para acceder a la galería",ContextCompat.getColor(this, R.color.teal_700))
                requestPermissions(permissions, GALERY_CODE)
            }*/
            else -> {
                requestPermissions(permissions, GALERY_CODE)
            }
        }
    }

    ///////--------------////////////

    ///////CAMERA////////////
    private val selectFromCameraIntent =
        registerForActivityResult(ActivityResultContracts.TakePicture()){
                bitmap ->
            if (bitmap){
                Log.i("CAMERA CHOOSER","DATA: ${this.uriCamera}")
                binding!!.uploadImage.setImageURI(uriCamera)
            }
        }

    private fun openCamera(){
        val permissions = arrayOf(Manifest.permission.CAMERA)
        when {
            requireActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                var file : File = File(requireActivity().filesDir, "picFromCamera");
                uriCamera = FileProvider.getUriForFile(requireActivity(), requireActivity().applicationContext.packageName + ".fileprovider", file);
                this.selectFromCameraIntent.launch(uriCamera)
            }
            /*shouldShowRequestPermissionRationale(CAMERA) -> {
                this.showSnackBar("Debes aceptar para acceder a la cámara",ContextCompat.getColor(this, R.color.teal_700))
                requestPermissions(permissions, CAMERA_CODE)
            }*/
            else -> {
                requestPermissions(permissions, CAMERA_CODE)
            }
        }
    }

    ///////--------------////////////

}