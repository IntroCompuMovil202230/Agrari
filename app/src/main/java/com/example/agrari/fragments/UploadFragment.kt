package com.example.agrari.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.agrari.ChoosePostLocationActivity
import com.example.agrari.Model.AgrariPost
import com.example.agrari.databinding.FragmentUploadBinding
import com.example.agrari.services.AuthService
import com.example.taller3_compu_movil.controller.ImageEncodingController
import java.io.File


class UploadFragment : Fragment() {

    companion object {
        private val CAMERA_CODE = 1007;
        private val GALERY_CODE = 1006;
    }

    lateinit var uriCamera: Uri
    private var binding: FragmentUploadBinding?=null
    var pictureUri: Uri?=null
    lateinit var authService: AuthService
    var imageEncodingController: ImageEncodingController = ImageEncodingController()
    lateinit var currentPost: AgrariPost
    lateinit var currentCategory: String

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentUploadBinding.inflate(layoutInflater,container,false)

        authService=AuthService()


        binding!!.uploadCameraButton.setOnClickListener(View.OnClickListener {
            this.openCamera()
            Log.i("GALERY-CONTROLLER", "ACCESING GALLERY")

        })
        binding!!.uploadGaleryButton.setOnClickListener(View.OnClickListener {
            Log.i("CAMERA-CONTROLLER", "ACCESING CAMERA")
            this.openGallery()
        })

        binding!!.Subir.setOnClickListener {
            try {
                Log.i("POST-DATA", "" +
                        """ 
                        Seller_id: ${authService.getCurrentUser()!!.uid},
                        Price: ${binding!!.precioInput.text}
                        Title: ${binding!!.titleInput.text},
                           
                        """
                )
                this.currentPost=AgrariPost(
                    authService.getCurrentUser()!!.uid,
                    binding!!.titleInput.text.toString(),
                    binding!!.precioInput.text.toString().toInt(),
                    this.imageEncodingController.encodeImage(it.context.contentResolver, this.pictureUri),
                    this.currentCategory
                )

                var intent = Intent(it.context,ChoosePostLocationActivity::class.java)
                intent.putExtra("newPost",this.currentPost)
                startActivity(intent)

            }catch (e:Exception){
                Toast.makeText(it.context,e.toString().substring(21), Toast.LENGTH_LONG).show()
                Log.i("ERROR", "$e")
            }
        }

        binding!!.categoryUploadSpinnerChooser.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                    val item: Any = parent.getItemAtPosition(pos).toString()
                    Log.i("SPINNER-CHOOSER", "THE ITEM VALUE IS: $item")
                    currentCategory=item.toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

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
            this.pictureUri=uri
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
                this.pictureUri=uriCamera
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