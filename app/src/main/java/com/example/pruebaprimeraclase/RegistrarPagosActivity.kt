package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        val btnContinuar = findViewById< Button>(R.id.btnContinuar)
        btnContinuar.setOnClickListener {
            val intent = Intent(this, PagarCuotaMensualActivity::class.java)
            startActivity(intent)
        }
    }
}