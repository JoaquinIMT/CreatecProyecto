package com.example.createc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
val CAMERA_REQUEST_CODE = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scanImage = findViewById<Button>(R.id.escanear_button)

        scanImage.setOnClickListener {
            cameraPermissionRequest()

        }
    }

    //función para checar permisos otorgados a la camara
    fun cameraPermissionRequest(){
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        // cheacamos si la camara obtuvo los permisos

        if (permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()

        }else{
            Toast.makeText(this,"Permiso Concedido",Toast.LENGTH_SHORT).show()

        }
    }

    fun makeRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){


                }else{
                    Toast.makeText(this,"Permiso concedido",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
