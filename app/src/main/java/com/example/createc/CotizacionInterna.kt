package com.example.createc

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner

class CotizacionInterna : AppCompatActivity() {

    lateinit var sp: Spinner
    lateinit var microInversorCheckBox : CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cotizacion_interna)

        sp = findViewById(R.id.spinner)
        microInversorCheckBox = findViewById(R.id.microInversor)
        val options = arrayOf("microinversor x 3","microinversor x 2","microinversor x 1")
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,options)

        sp.adapter = adapter

        microInversorCheckBox.setOnClickListener{
            if(microInversorCheckBox.isChecked){
                sp.visibility = View.VISIBLE
            }else{
                sp.visibility = View.INVISIBLE
            }
        }

    }
}
