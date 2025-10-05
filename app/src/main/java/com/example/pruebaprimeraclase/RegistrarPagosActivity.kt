package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class RegistrarPagosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_pagos)

        val btnBack = findViewById< ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            startActivity(intent)
        }

        val btnContinuarSocio = findViewById< Button>(R.id.btnContinuarSocio)
        btnContinuarSocio.setOnClickListener {
            val intent = Intent(this, PagoSocioCuotaMensualActivity::class.java)
            startActivity(intent)
        }

        val btnContinuarNoSocio = findViewById< Button>(R.id.btnContinuarNoSocio)
        btnContinuarNoSocio.setOnClickListener {
            val intent = Intent(this, PagoNoSocioDiarioActivity::class.java)
            startActivity(intent)
        }
    }
}