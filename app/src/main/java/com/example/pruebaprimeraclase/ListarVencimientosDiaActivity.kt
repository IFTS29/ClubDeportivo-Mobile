package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ListarVencimientosDiaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listar_vencimientos_dia)

        // Referencias a los elementos del layout
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val btnPeriodoVencimiento = findViewById<Button>(R.id.btnPeriodoVencimiento)
        val vencimientoListCard = findViewById<LinearLayout>(R.id.vencimiento_list_card)

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

        // Funcionalidad del botón Período de Vencimiento
        btnPeriodoVencimiento.setOnClickListener {
            Toast.makeText(this, "Seleccionar período de vencimiento", Toast.LENGTH_SHORT).show()
            // Aquí se podría implementar un diálogo para seleccionar el período
        }

        // Configurar clicks en los elementos de la lista
        setupListItemClicks(vencimientoListCard)
    }

    private fun setupListItemClicks(parentLayout: LinearLayout) {
        // Buscar el LinearLayout que contiene los elementos include
        for (i in 0 until parentLayout.childCount) {
            val child = parentLayout.getChildAt(i)

            // Si es un LinearLayout (que contiene los elementos de la lista)
            if (child is LinearLayout) {
                // Configurar click listener para cada elemento hijo
                for (j in 0 until child.childCount) {
                    val listItem = child.getChildAt(j)

                    // Configurar click listener para cada elemento
                    listItem.setOnClickListener {
                        // Obtener el ID del socio desde el TextView
                        val socioIdTextView = listItem.findViewById<android.widget.TextView>(R.id.tvSocioId)
                        val socioId = socioIdTextView?.text?.toString() ?: "43474085"

                        // Navegar a la pantalla de detalle de vencimiento
                        val intent = Intent(this, VencimientoDetalleActivity::class.java)
                        intent.putExtra("socio_id", socioId)
                        intent.putExtra("apellido_nombre", "APELLIDO NOMBRE")
                        intent.putExtra("documento", socioId)
                        intent.putExtra("fecha_inscripcion", "XX/XX/XX")
                        intent.putExtra("periodo", "SEPT 2025")
                        intent.putExtra("importe", "XXXXXXX")
                        intent.putExtra("estado", "IMPAGO")
                        startActivity(intent)
                    }
                }
            }
        }
    }
}