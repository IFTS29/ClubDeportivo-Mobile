package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MenuPrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        val btnRegistroClientes = findViewById<Button>(R.id.btnRegistroClientes)
        btnRegistroClientes.setOnClickListener {
            val intent = Intent(this, RegistrarClientesActivity::class.java)
            startActivity(intent)
        }

        val btnRegistroPagos = findViewById<Button>(R.id.btnRegistroPagos)
        btnRegistroPagos.setOnClickListener {
            val intent = Intent(this, RegistrarPagosActivity::class.java)
            startActivity(intent)
        }

        val btnListadoVencimientos = findViewById<Button>(R.id.btnListadoVencimientos)
        btnListadoVencimientos.setOnClickListener {
            val intent = Intent(this, ListarVencimientosDiaActivity::class.java)
            startActivity(intent)
        }

    }
}