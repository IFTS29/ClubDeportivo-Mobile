package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class RegistrarClientes3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_clientes3)

        //  Obtener datos pasados desde la pantalla anterior
        val clientId = intent.getLongExtra("clientId", -1L)
        val dni = intent.getStringExtra("dni") ?: ""
        val nombre = intent.getStringExtra("nombre") ?: "Nombre"
        val apellido = intent.getStringExtra("apellido") ?: "Apellido"
        val tipoCliente = intent.getStringExtra("tipo_cliente") ?: ""

        // Referencias a los elementos del layout
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val btnPagar = findViewById<Button>(R.id.btnPagar)
        val btnMenuPrincipal = findViewById<Button>(R.id.btnMenuPrincipal)
        val tvNombreApellido = findViewById<TextView>(R.id.tvNombreApellido)
        val tvDocumento = findViewById<TextView>(R.id.tvDocumento)
        val tvSocioNumero = findViewById<TextView>(R.id.tvSocioNumero)

        // ⭐ Actualizar los datos en la pantalla (ahora con clientId)
        updateRegistroData(
            dni = dni,
            tipoCliente = tipoCliente,
            nombre = nombre,
            apellido = apellido,
            clientId = clientId, //
            tvNombreApellido = tvNombreApellido,
            tvDocumento = tvDocumento,
            tvSocioNumero = tvSocioNumero
        )

        // Funcionalidad del botón Atrás
        btnBack.setOnClickListener {
            finish()
        }

        // Funcionalidad del botón Menú
        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Funcionalidad del botón Pagar
        btnPagar.setOnClickListener {
            val intent = Intent(this, RegistrarPagosActivity::class.java)
            intent.putExtra("dni", dni)
            intent.putExtra("tipo_cliente", tipoCliente)
            startActivity(intent)
        }

        // Funcionalidad del botón Menú Principal
        btnMenuPrincipal.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // ⭐ Función actualizada con clientId
    private fun updateRegistroData(
        dni: String,
        tipoCliente: String,
        nombre: String,
        apellido: String,
        clientId: Long,
        tvNombreApellido: TextView,
        tvDocumento: TextView,
        tvSocioNumero: TextView
    ) {
        val nombreCompleto = "$nombre $apellido"

        tvNombreApellido.text = nombreCompleto
        tvDocumento.text = "DOC: $dni"

        // ⭐ Usar clientId REAL en lugar de número random
        if (tipoCliente == "Socio") {
            tvSocioNumero.text = "SOCIO NRO: $clientId"
        } else {
            tvSocioNumero.text = "CLIENTE NO SOCIO"
        }
    }
}