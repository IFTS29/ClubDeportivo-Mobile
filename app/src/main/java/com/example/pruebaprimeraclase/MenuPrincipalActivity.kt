package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class MenuPrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        val btnClose = findViewById<ImageButton>(R.id.btnClose)
        btnClose.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btnRegistroClientes = findViewById<LinearLayout>(R.id.btnRegistroClientes)
        btnRegistroClientes.setOnClickListener {
            val intent = Intent(this, RegistrarClientesActivity::class.java)
            startActivity(intent)
        }

        val btnRegistroPagos = findViewById<LinearLayout>(R.id.btnRegistroPagos)
        btnRegistroPagos.setOnClickListener {
            val intent = Intent(this, RegistrarPagosActivity::class.java)
            startActivity(intent)
        }

        val btnListadoVencimientos = findViewById<LinearLayout>(R.id.btnListadoVencimientos)
        btnListadoVencimientos.setOnClickListener {
            val intent = Intent(this, ListarVencimientosDiaActivity::class.java)
            startActivity(intent)
        }

    }
}