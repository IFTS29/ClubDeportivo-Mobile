package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrarClientesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_clientes)

        // Referencias a los elementos del layout
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        val btnContinuar = findViewById<Button>(R.id.btnContinuar)
        val etDni = findViewById<EditText>(R.id.etDni)
        val errorCard = findViewById<LinearLayout>(R.id.error_card)

        // Inicialmente ocultar la tarjeta de error
        errorCard.visibility = LinearLayout.GONE

        // Funcionalidad del botón Atrás
        btnBack.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Funcionalidad del botón Menú
        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Funcionalidad del botón Buscar
        btnBuscar.setOnClickListener {
            val dni = etDni.text.toString().trim()

            if (dni.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese un DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar si el DNI ya está registrado (mock: 43474085)
            if (dni == "43474085") {
                // Cliente ya registrado - mostrar tarjeta de error
                errorCard.visibility = LinearLayout.VISIBLE
                Toast.makeText(this, "El cliente ya está registrado", Toast.LENGTH_SHORT).show()
            } else {
                // Cliente no registrado - ocultar tarjeta de error
                errorCard.visibility = LinearLayout.GONE
                Toast.makeText(this, "Cliente no encontrado. Puede continuar con el registro", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar botón Continuar
        btnContinuar.setOnClickListener {
            val dni = etDni.text.toString().trim()

            if (dni.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese un DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Navegar a RegistrarClientes2Activity con el DNI
            val intent = Intent(this, RegistrarClientes2Activity::class.java)
            intent.putExtra("dni", dni)
            startActivity(intent)
        }
    }
}