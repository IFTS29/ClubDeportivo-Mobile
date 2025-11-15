package com.example.pruebaprimeraclase

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class RegistrarClientesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_clientes)
        val dbHelper = DBHelper(this)


        // Referencias a los elementos del layout
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val btnContinue = findViewById<Button>(R.id.btnContinue)
        val etDoc = findViewById<EditText>(R.id.etDoc)
        val messageCard = findViewById<LinearLayout>(R.id.messageCard)
        val tvCardMessage = findViewById<TextView>(R.id.tvCardMessage)


        // Estado incial: ocultar la tarjeta de mensaje
        messageCard.visibility = LinearLayout.GONE

        // 2. Estado incial: deshabilitar botón "Continuar"
        btnContinue.isEnabled = false
        btnContinue.alpha = 0.5f // Efecto visual de deshabilitado


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

        // Funcionalidad del botón Buscar
        btnSearch.setOnClickListener {
            val doc = etDoc.text.toString().trim()

            // 1. Resetear
            btnContinue.isEnabled = false
            btnContinue.alpha = 0.5f
            messageCard.visibility = View.GONE
            etDoc.error = null

            // 2. Validar Documento vacío
            if (doc.isEmpty()) {
                etDoc.error = "Por favor ingrese un DNI"
                return@setOnClickListener
            }

            // 3. Consultar a la base de datos
            if (dbHelper.validateClientByDoc(doc)) {
                // SÍ EXISTE --> Muestra mensaje de ERROR
                val clientInfoMessage = dbHelper.getClientByDoc(doc)

                tvCardMessage.text = clientInfoMessage

                // --- ¡CAMBIOS DE ESTILO (ERROR)! ---
                // 1. Tinta el fondo (preserva bordes) con tu color de alerta
                messageCard.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.rojo_alerta_claro2_bg) // Usa un rojo claro para fondo
                )
                // 2. Pone el TEXTO en tu color de alerta oscuro
                tvCardMessage.setTextColor(ContextCompat.getColor(this, R.color.rojo_alerta))

                messageCard.visibility = View.VISIBLE
            } else {
                // NO EXISTE --> Muestra mensaje de ÉXITO
                tvCardMessage.text = "Documento disponible.\nPuede continuar con el registro."

                // --- ¡CAMBIOS DE ESTILO (ÉXITO)! ---
                // 1. Tinta el fondo (preserva bordes) con un verde claro
                messageCard.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.verde_exito_claro_bg) // Usa un verde claro para fondo
                )
                // 2. Pone el TEXTO en tu color de éxito oscuro
                tvCardMessage.setTextColor(ContextCompat.getColor(this, R.color.verde_exito))

                messageCard.visibility = View.VISIBLE

                // Habilitamos el botón "Continuar"
                btnContinue.isEnabled = true
                btnContinue.alpha = 1.0f
            }
        }



        // Funcionalidad del boton Continuar
        btnContinue.setOnClickListener {
            val dni = etDoc.text.toString().trim()
            val intent = Intent(this, RegistrarClientes2Activity::class.java)
            intent.putExtra("dni", dni)
            startActivity(intent)
        }
    }
}