package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

class PagoSocioCarnetActivity : AppCompatActivity() {

    private lateinit var clientData: ClientData
    private lateinit var membershipData: MembershipData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_socio_carnet)

        // --- 1. RECUPERAR DATOS DEL INTENT ---
        clientData = intent.getParcelableExtra("CLIENT_DATA") ?: run {
            Toast.makeText(this, "Error al cargar datos del cliente", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        membershipData = intent.getParcelableExtra("MEMBERSHIP_DATA") ?: run {
            Toast.makeText(this, "Error al cargar datos de la membresía", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // --- 2. INICIALIZAR VISTAS ---
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val btnImprimir = findViewById<Button>(R.id.btnImprimir)

        // Referencias a los campos del carnet
        val tvNombreSocio = findViewById<TextView>(R.id.txt_nombre_socio)
        val tvNroSocio = findViewById<TextView>(R.id.txt_nro_socio)
        val tvFechaInicio = findViewById<TextView>(R.id.txt_fecha_inicio)
        val tvFechaFin = findViewById<TextView>(R.id.txt_fecha_fin)

        // --- 3. POBLAR DATOS EN EL CARNET ---
        // (Usamos los datos de los objetos recibidos)

        tvNombreSocio.text = "${clientData.firstName} ${clientData.lastName}"
        tvNroSocio.text = "Nro de Socio: ${clientData.clientId}"

        // LOGICA DE FECHAS
        // 1. Obtener la fecha de hoy (ej: "2025-11-16")
        val fechaInicioVigencia = DateUtils.getTodayDateString()

        // 2. Calcular la fecha de vencimiento (ej: "2025-12-16")
        val fechaFinVigencia = DateUtils.addDaysToDate(fechaInicioVigencia, 30)

        // 3. Mostrar las fechas formateadas (ej: "16/11/2025")
        // (Quitamos el "D:" de depuración)
        tvFechaInicio.text = "Desde ${DateUtils.formatFullDate(fechaInicioVigencia)}"
        tvFechaFin.text = "Hasta ${DateUtils.formatFullDate(fechaFinVigencia)}"

        // --- 4. LISTENERS DE NAVEGACIÓN ---
        btnBack.setOnClickListener {
            // Vuelve a la pantalla de Éxito
            finish()
        }

        btnMenu.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Volver al Menú")
                .setMessage("¿Desea volver al menú principal?")
                .setPositiveButton("Sí") { _, _ ->
                    val intent = Intent(this, MenuPrincipalActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                }
                .setNegativeButton("No", null)
                .show()
        }

        btnImprimir.setOnClickListener {
            // Lógica de impresión (esto es complejo, por ahora solo simula)
            Toast.makeText(this, "Función de imprimir no implementada", Toast.LENGTH_SHORT).show()
            // Opcionalmente, navegar al menú principal
            // val intent = Intent(this, MenuPrincipalActivity::class.java)
            // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            // startActivity(intent)
        }
    }
}