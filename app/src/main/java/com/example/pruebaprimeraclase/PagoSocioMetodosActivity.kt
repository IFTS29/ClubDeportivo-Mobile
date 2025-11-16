package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog


class PagoSocioMetodosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_socio_metodos)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        btnMenu.setOnClickListener {
            // 1. Crear el cuadro de diálogo de confirmación
            AlertDialog.Builder(this)
                .setTitle("Volver al Menú")
                .setMessage("¿Desea volver al menú principal?")

                // 2. Botón Positivo ("Sí") - Ejecuta la acción
                .setPositiveButton("Sí") { _, _ ->
                    // Lógica de navegación original:
                    val intent = Intent(this, MenuPrincipalActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                }

                // 3. Botón Negativo ("No") - No hace nada (simplemente cierra el diálogo)
                .setNegativeButton("No", null)

                // 4. Mostrar el diálogo
                .show()
        }

        val btnPagar = findViewById<Button>(R.id.btnPagar)
        btnPagar.setOnClickListener {
            val intent = Intent(this, PagoSocioExitoActivity::class.java)
            startActivity(intent)
        }

    }
}