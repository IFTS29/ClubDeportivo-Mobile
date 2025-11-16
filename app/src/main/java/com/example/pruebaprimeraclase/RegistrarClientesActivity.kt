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

        // --- Estado Inicial ---
        //  ocultar la tarjeta y deshabilitar botón Continuar
        messageCard.visibility = LinearLayout.GONE
        btnContinue.isEnabled = false
        btnContinue.alpha = 0.5f // Efecto visual de deshabilitado


        // --- Navegación ---
        // BOTÓN Atrás y Menú
        btnBack.setOnClickListener {
            finish()
        }

        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }


        // --- BOTÓN BUSCAR ---
        btnSearch.setOnClickListener {
            val doc = etDoc.text.toString().trim()

            // Resetear estado
            btnContinue.isEnabled = false
            btnContinue.alpha = 0.5f
            messageCard.visibility = View.GONE
            etDoc.error = null

            // Validar Documento (usamos la clase UtilidadesValidacion)
            val docError  = UtilidadesValidacion.validarDocumento(doc)
            if (docError  != null) {
                // Si hay un error (el resultado NO es null),
                // muestra el error en el EditText y termina la función.
                etDoc.error = docError
                return@setOnClickListener
            }

            // Consultar base de datos
            if (dbHelper.validateClientByDoc(doc)) {

                // --- EXISTE CLIENTE (Fracaso) ---
                // LlamaR a función getClientByDoc
                val client: ClientData? = dbHelper.getClientByDoc(doc)

                // ValidaR que el objeto no sea nulo
                if (client != null) {

                    // Usa los campos que necesita: firstName, lastName, clientType, clientId
                    val clientInfoMessage = "Cliente registrado\n\nNombre: ${client.firstName} ${client.lastName}\n\n${client.clientType.uppercase()} Nro: ${client.clientId}"

                    tvCardMessage.text = clientInfoMessage

                    // Asignar estilos
                    messageCard.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.rojo_alerta_claro2_bg)
                    )
                    tvCardMessage.setTextColor(ContextCompat.getColor(this, R.color.rojo_alerta))
                    messageCard.visibility = View.VISIBLE
                }

            } else {
                // --- NO EXISTE (Éxito) ---
                tvCardMessage.text = "Documento disponible.\n\nPuede continuar con el registro."
                messageCard.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.verde_exito_claro_bg)
                )
                tvCardMessage.setTextColor(ContextCompat.getColor(this, R.color.verde_exito))
                messageCard.visibility = View.VISIBLE

                btnContinue.isEnabled = true
                btnContinue.alpha = 1.0f
            }
        }



        // --- BOTÓN CONTINUAR ---
        btnContinue.setOnClickListener {
            val dni = etDoc.text.toString().trim()
            val intent = Intent(this, RegistrarClientes2Activity::class.java)
            intent.putExtra("dni", dni)
            startActivity(intent)
        }
    }
}