package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class RegistrarClientes3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_clientes3)

        // Obtener datos pasados desde la pantalla anterior
        val dni = intent.getStringExtra("dni") ?: ""
        val tipoCliente = intent.getStringExtra("tipo_cliente") ?: ""

        // Referencias a los elementos del layout
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val btnPagar = findViewById<Button>(R.id.btnPagar)
        val btnMenuPrincipal = findViewById<Button>(R.id.btnMenuPrincipal)
        val tvNombreApellido = findViewById<TextView>(R.id.tvNombreApellido)
        val tvDocumento = findViewById<TextView>(R.id.tvDocumento)
        val tvSocioNumero = findViewById<TextView>(R.id.tvSocioNumero)

        // Actualizar los datos en la pantalla
        updateRegistroData(dni, tipoCliente, tvNombreApellido, tvDocumento, tvSocioNumero)

        // Funcionalidad del botón Atrás
        btnBack.setOnClickListener {
            finish() // Volver a la pantalla anterior
        }

        // Funcionalidad del botón Menú
        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Funcionalidad del botón Pagar
        btnPagar.setOnClickListener {
            // Navegar a la pantalla de registro de pagos
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

    private fun updateRegistroData(dni: String, tipoCliente: String, tvNombreApellido: TextView, tvDocumento: TextView, tvSocioNumero: TextView) {
        // Datos mock para mostrar
        val nombresMock = arrayOf("Roberto Ignacio", "María Elena", "Carlos Alberto", "Ana Sofía", "Luis Fernando")
        val apellidosMock = arrayOf("Alvarez", "González", "Rodríguez", "Martínez", "López")

        val nombreCompleto = "${nombresMock.random()} ${apellidosMock.random()}"
        val numeroSocio = Random.nextInt(1000, 9999)

        tvNombreApellido.text = nombreCompleto
        tvDocumento.text = "DOC: $dni"

        if (tipoCliente == "Socio") {
            tvSocioNumero.text = "SOCIO NRO: $numeroSocio"
        } else {
            tvSocioNumero.text = "CLIENTE NO SOCIO"
        }
    }
}