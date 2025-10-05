package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class VencimientoDetalleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vencimiento_detalle)

        // Obtener los datos pasados desde la pantalla anterior
        val socioId = intent.getStringExtra("socio_id") ?: "43474085"
        val apellidoNombre = intent.getStringExtra("apellido_nombre") ?: "APELLIDO NOMBRE"
        val documento = intent.getStringExtra("documento") ?: socioId
        val fechaInscripcion = intent.getStringExtra("fecha_inscripcion") ?: "XX/XX/XX"
        val periodo = intent.getStringExtra("periodo") ?: "SEPT 2025"
        val importe = intent.getStringExtra("importe") ?: "XXXXXXX"
        val estado = intent.getStringExtra("estado") ?: "IMPAGO"

        // Referencias a los elementos del layout
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val btnPagar = findViewById<Button>(R.id.btnPagar)

        // Actualizar los TextViews con los datos recibidos
        updateVencimientoDetails(apellidoNombre, documento, socioId, fechaInscripcion, periodo, importe, estado)

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
            Toast.makeText(this, "Procesando pago para socio: $socioId", Toast.LENGTH_LONG).show()

            // Navegar a la pantalla de registro de pagos
            val intent = Intent(this, RegistrarPagosActivity::class.java)
            intent.putExtra("socio_id", socioId)
            startActivity(intent)
        }
    }

    private fun updateVencimientoDetails(apellidoNombre: String, documento: String, socioId: String,
                                         fechaInscripcion: String, periodo: String, importe: String, estado: String) {
        // Buscar y actualizar los TextViews en el layout
        val cardLayout = findViewById<android.widget.LinearLayout>(R.id.vencimiento_detail_card)
            ?: return // Si no encuentra el card, salir

        // Actualizar cada TextView con los datos correspondientes
        for (i in 0 until cardLayout.childCount) {
            val child = cardLayout.getChildAt(i)
            if (child is TextView) {
                when (i) {
                    0 -> child.text = apellidoNombre
                    1 -> child.text = "DOC: $documento"
                    2 -> child.text = "SOCIO: $socioId"
                    3 -> child.text = "FECHA INSCRIPCIÓN: $fechaInscripcion"
                    4 -> child.text = "PERÍODO: $periodo"
                    5 -> child.text = "Nro: $importe"
                    6 -> child.text = "ESTADO: $estado"
                }
            }
        }
    }
}