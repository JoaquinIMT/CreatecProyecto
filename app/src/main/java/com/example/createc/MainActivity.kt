package com.example.createc

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.*
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


class MainActivity : AppCompatActivity() {
    val CAMERA_REQUEST_CODE = 1001
    val IMAGE_CAPTURE_CODE = 1002
    var image_rui : Uri? = null
    var paragraphComplete: String = ""
    var nombreDeUsuario: String = ""
    var noServicioUsuario: String = ""
    var direccion: String = ""
    var tipoRecibo: String = ""
    var tipoContrato: String = ""

    lateinit var textPro: TextView
    lateinit var scanImage: Button
    lateinit var usuarioActivity: Button
    lateinit var confirmationImage: ImageView
    lateinit var tomarFoto: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        scanImage = findViewById(R.id.escanear_button)
        textPro = findViewById(R.id.bigText)
        usuarioActivity = findViewById(R.id.next)
        confirmationImage = findViewById(R.id.ticket_image)
        tomarFoto = findViewById(R.id.picture)


        scanImage.setOnClickListener {
            scantext()

        }

        usuarioActivity.setOnClickListener {
            val intent = Intent(applicationContext,ClienteDatos::class.java)
            startActivity(intent)
        }

        tomarFoto.setOnClickListener {
            cameraPermissionRequest()
        }

    }

    //función para checar permisos otorgados a la camara
    private fun cameraPermissionRequest(){
        val permissionCam = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val permissionWES = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        // cheacamos si la camara obtuvo los permisos

        if (permissionCam != PackageManager.PERMISSION_GRANTED || permissionWES != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }else{
            openCamera()
        }
    }

    private fun makeRequest(){

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE),CAMERA_REQUEST_CODE)

    }

    private fun openCamera(){
        //Toast.makeText(this,"Abriendo camara",Toast.LENGTH_SHORT).show()
        val valores = ContentValues()

        valores.put(MediaStore.Images.Media.TITLE,"New picture")
        valores.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")

        image_rui = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores)
        paragraphComplete = ""
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

        if (resultCode == Activity.RESULT_OK) {
            setImage()
        }
    }

    //Implementar en un boton

    private fun scantext(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Cargando...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        var image: FirebaseVisionImage? = null

        try {
            image = FirebaseVisionImage.fromFilePath(applicationContext,image_rui!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }


        val detector = FirebaseVision.getInstance().cloudDocumentTextRecognizer


        detector.processImage(image!!)
            .addOnSuccessListener {
                textExtractor(it)
                progressDialog.dismiss()
            }

            .addOnFailureListener {
                progressDialog.dismiss()
                // Task failed with an exception
                // ...
            }

    }

    private  fun setImage(){
            if(scanImage.visibility == View.GONE) scanImage .visibility = View.VISIBLE
            confirmationImage.setImageURI(image_rui)

    }

    private  fun textExtractor(result: FirebaseVisionDocumentText?){

        if(result != null) {

            val resultText = result.text

            var linesOfText = resultText.lines()
            var linesOfTextMutable = linesOfText.toMutableList()
            val errores = listOf("Suministrador de","CFE","CFE. Suministrador de",
                "CFE Suministrador de","Servicios Básicos","Servicios Básicos."
                ,"CFE Suministrador de Servicios Básicos"
            ,"Av. Juárez, Del. Cuauhtémoc, C.P. 06600, Ciudad de México.")

            for (error in errores){
                linesOfTextMutable.remove(error)
            }


            //textPro.text = linesOfText.toString()
            textPro.text = resultText

            var chequeoPos = false

            /*for (lines in linesOfText ) {
                val long = lines.split(" ")
                val index = linesOfText.indexOf(lines)
                if (long.size < 3 && index<6){
                    linesOfTextMutable.removeAt(index)
                }

            }*/
            val textprueba = linesOfTextMutable[0]+"\n\n\n"+linesOfText.toString()
            textPro.text = textprueba
            /*if (lines.equals("RFC: CSS160330CP7")) {
                    val index = linesOfText.indexOf(lines)
                    if (linesOfText[index + 1] == "TOTAL A PAGAR:") {

                        campos(linesOfText[index + 2], "nombre")
                        chequeoPos = true
                        //textPro.text = linesOfText[index+2]
                        break
                    } else {
                        //textPro.text = linesOfText[index+1]
                        break
                    }

                }
            }
            if(chequeoPos){
                val indexNoServicio = linesOfText.indexOf("CULIACAN. Sin")

                campos(linesOfText[indexNoServicio+3],type="noServicio")

                //textPro.text = linesOfText[indexNoServicio+3]

            }else{

                val indexNoServicio = linesOfText.indexOf("CULIACAN. Sin")

                campos(linesOfText[indexNoServicio+4],type="noServicio")
                //textPro.text = linesOfText[indexNoServicio+3]
            }*/
        }else{
            textPro.text = null
            Toast.makeText(this,"No se reconoce texto",Toast.LENGTH_LONG).show()
        }
    }

    private fun campos(component : String = "", type: String =""){

        when(type){

            "nombre" ->  nombreDeUsuario = component

            "noServicio" -> noServicioUsuario = component

            "direccion" -> direccion = component

            "tiRecibo" -> tipoRecibo = component

            "tiContrato" -> tipoContrato = component
        }
    }
}