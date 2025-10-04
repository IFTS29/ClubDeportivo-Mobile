package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class RegistrarClientes2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_clientes_2)

        // Obtener el DNI pasado desde la pantalla anterior
        val dni = intent.getStringExtra("dni")

        // Referencias a los elementos del layout
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val btnContinuar = findViewById<Button>(R.id.btnContinuar)
        val spinnerTipoCliente = findViewById<Spinner>(R.id.spinnerTipoCliente)

        // Configurar el Spinner con las opciones
        val tiposCliente = arrayOf("Seleccionar tipo", "Socio", "No Socio")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposCliente)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoCliente.adapter = adapter

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

        // Funcionalidad del botón Continuar
        btnContinuar.setOnClickListener {
            val tipoClienteSeleccionado = spinnerTipoCliente.selectedItem.toString()
            
            if (tipoClienteSeleccionado == "Seleccionar tipo") {
                Toast.makeText(this, "Por favor seleccione un tipo de cliente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Navegar a la pantalla de confirmación (Registro 3)
            val intent = Intent(this, RegistrarClientes3Activity::class.java)
            intent.putExtra("dni", dni)
            intent.putExtra("tipo_cliente", tipoClienteSeleccionado)
            startActivity(intent)
        }
    }
}