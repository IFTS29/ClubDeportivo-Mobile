    package com.example.pruebaprimeraclase

    import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


    class RegistrarPagosActivity : AppCompatActivity() {

        // --- Variables de Estado (Propiedades) ---
        private lateinit var dbHelper: DBHelper        // lateint para inicializar luego
        private var fetchedClient: ClientData? = null  // inicialmente es nulo

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_registrar_pagos)

            // Se inicializa el dbHelper
            dbHelper = DBHelper(this)

            // Referencias a los elementos del layout
            val btnBack = findViewById<ImageButton>(R.id.btnBack)
            val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
            val btnSearch = findViewById<Button>(R.id.btnSearch)
            val btnContinue = findViewById<Button>(R.id.btnContinue)
            val etDoc = findViewById<EditText>(R.id.etDoc)

            // Referencias a los elementos de la Card
            val messageCard = findViewById<LinearLayout>(R.id.messageCard)
            val tvStatus = findViewById<TextView>(R.id.tvStatusMessage)
            val tvName = findViewById<TextView>(R.id.tvClientName)
            val tvDoc = findViewById<TextView>(R.id.tvClientDoc)
            val tvId = findViewById<TextView>(R.id.tvClientId)
            val tvRegDate = findViewById<TextView>(R.id.tvClientRegDate)
            val tvType = findViewById<TextView>(R.id.tvClientType)

            // --- Estado Inicial ---
            messageCard.visibility = View.GONE
            btnContinue.isEnabled = false
            btnContinue.alpha = 0.5f

            // --- Navegación ---
            btnBack.setOnClickListener {
                finish()
            }

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

            // --- BOTÓN BUSCAR ---
            btnSearch.setOnClickListener {
                val doc = etDoc.text.toString().trim()

                // Resetear estado
                btnContinue.isEnabled = false
                btnContinue.alpha = 0.5f
                messageCard.visibility = View.GONE
                etDoc.error = null
                fetchedClient = null

                // Validar documento
                val docError = UtilidadesValidacion.validarDocumento(doc)
                if (docError != null) {
                    etDoc.error = docError
                    return@setOnClickListener
                }

                // Consultar base de datos
                val client = dbHelper.getClientByDoc(doc)

                if (client == null) {
                    // --- NO EXISTE CLIENTE (FRACASO) ---

                    // --- 1. LÓGICA DE DATOS (FRACASO) ---
                    tvStatus.text = "CLIENTE NO REGISTRADO.\nVUELVA A INGRESAR UN DOCUMENTO."
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

                    // Ocultar campos de datos
                    tvName.visibility = View.GONE
                    tvDoc.visibility = View.GONE
                    tvId.visibility = View.GONE
                    tvRegDate.visibility = View.GONE
                    tvType.visibility = View.GONE

                    // --- 2. ESTILOS DE FRACASO ---
                    tvStatus.setTextColor(ContextCompat.getColor(this, R.color.rojo_alerta))
                    tvStatus.setTypeface(null, Typeface.NORMAL) // 1. SIN negrita
                    tvStatus.gravity = Gravity.CENTER           // 2. Centrado
                    tvStatus.setLineSpacing(0f, 1.3f)           // 3. 30% más de espacio entre líneas

                    messageCard.visibility = View.VISIBLE

                } else {
                    // --- EXISTE CLIENTE (ÉXITO) ---
                    fetchedClient = client

                    // --- 1. LÓGICA DE DATOS (ÉXITO) ---
                    // Mostrar campos de datos
                    tvStatus.visibility = View.VISIBLE
                    tvName.visibility = View.VISIBLE
                    tvDoc.visibility = View.VISIBLE
                    tvId.visibility = View.VISIBLE
                    tvRegDate.visibility = View.VISIBLE
                    tvType.visibility = View.VISIBLE

                    // --- 2. ESTILOS DE ÉXITO ---
                    tvStatus.text = "Cliente registrado"
                    tvStatus.setTextColor(ContextCompat.getColor(this, R.color.verde_exito))
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check_circle, 0, 0, 0)
                    tvStatus.compoundDrawableTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.verde_exito)
                    )

                    tvStatus.setTypeface(null, Typeface.BOLD) // 1. CON negrita
                    tvStatus.gravity = Gravity.START           // 2. Alineado a la izquierda
                    tvStatus.setLineSpacing(0f, 1.0f)           // 3. Espaciado normal

                    //Asignar datos
                    tvName.text = "${client.firstName.uppercase()} ${client.lastName.uppercase()}"
                    tvDoc.text = "DOC. ${client.docNumber}"
                    tvId.text = "ID CLIENTE ${client.clientId}"
                    tvRegDate.text = "FECHA INSCRIPCIÓN ${client.registrationDate}"
                    tvType.text = "CLIENTE ${client.clientType.uppercase()}"

                    // Colores de texto
                    tvName.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                    tvDoc.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                    tvId.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                    tvRegDate.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                    tvType.setTextColor(ContextCompat.getColor(this, R.color.violeta))

                    messageCard.visibility = View.VISIBLE

                    // Habilitar el botón Continuar
                    btnContinue.isEnabled = true
                    btnContinue.alpha = 1.0f
                }
            }

            // --- BOTON CONTINUAR ---
            btnContinue.setOnClickListener {
                if (fetchedClient != null) {
                    if (fetchedClient!!.clientType == "SOCIO") {
                        val intent = Intent(this, PagoSocioCuotaMensualActivity::class.java)
                        intent.putExtra("CLIENT_DOC", fetchedClient!!.docNumber) // Pasa el doc
                        startActivity(intent)
                    } else { // "NO SOCIO"
                        val intent = Intent(this, PagoNoSocioDiarioActivity::class.java)
                        intent.putExtra("CLIENT_DOC", fetchedClient!!.docNumber) // Pasa el doc
                        startActivity(intent)
                    }
                }
            }


        }
    }