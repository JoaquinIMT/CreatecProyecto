package com.example.createc

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
val CAMERA_REQUEST_CODE = 1001
val IMAGE_CAPTURE_CODE = 1002
var image_rui : Uri? = null
lateinit var textPro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        val scanImage = findViewById<Button>(R.id.escanear_button)
        textPro = findViewById<TextView>(R.id.pruebaTEXT)

        scanImage.setOnClickListener {
            cameraPermissionRequest()

        }
    }

    //función para checar permisos otorgados a la camara
    fun cameraPermissionRequest(){
        val permissionCam = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val permissionWES = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        // cheacamos si la camara obtuvo los permisos

        if (permissionCam != PackageManager.PERMISSION_GRANTED || permissionWES != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }else{
            openCamera()
        }
    }

    fun makeRequest(){

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE),CAMERA_REQUEST_CODE)

    }

    fun openCamera(){
        //Toast.makeText(this,"Abriendo camara",Toast.LENGTH_SHORT).show()
        val valores = ContentValues()

        valores.put(MediaStore.Images.Media.TITLE,"New picture")
        valores.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")

        image_rui = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)



    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){

                }else{
                    openCamera()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            //Toast.makeText(this,"Si entro y definio la variable",Toast.LENGTH_SHORT).show()
            var image: FirebaseVisionImage? = null
            try {
                image = FirebaseVisionImage.fromFilePath(applicationContext,image_rui!!)
                Toast.makeText(this,image_rui.toString(),Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val detector = FirebaseVision.getInstance().cloudDocumentTextRecognizer
            val result = detector.processImage(image!!)
                .addOnSuccessListener { firebaseVisionText ->
                    // Task completed successfully
                    // ...
                    for (block in firebaseVisionText.blocks) {
                        val boundingBox = block.boundingBox
                        val text = block.text
                        textPro.text= text

                    }

                }

                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }


        }
    }
}
