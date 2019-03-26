package com.example.createc

import android.content.Intent
import android.icu.text.IDNA
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ClienteDatos : AppCompatActivity() {

    lateinit var next2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_datos)

        next2 = findViewById(R.id.next2)

        next2.setOnClickListener {

            val intent = Intent(applicationContext,InformacionConsumo::class.java)
            startActivity(intent)

        }

    }
}
