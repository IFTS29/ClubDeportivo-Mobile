package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog


class PagoSocioExitoActivity : AppCompatActivity() {

    private lateinit var clientData: ClientData
    private lateinit var membershipData: MembershipData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_socio_exito)

        // --- 1. RECUPERAR DATOS DEL INTENT ---
        clientData = intent.getParcelableExtra("CLIENT_DATA") ?: run {
            Toast.makeText(this, "Error al cargar datos del cliente", Toast.LENGTH_LONG).show()
            finish() // No se puede continuar sin datos
            return
        }

        membershipData = intent.getParcelableExtra("MEMBERSHIP_DATA") ?: run {
            Toast.makeText(this, "Error al cargar datos de la membresía", Toast.LENGTH_LONG).show()
            finish() // No se puede continuar sin datos
            return
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val btnCarnet = findViewById<Button>(R.id.btnCarnet)
        val btnComprobante = findViewById<Button>(R.id.btnComprobante)
        val tvCuotaNumero = findViewById<TextView>(R.id.tvCuotaNumero)
        val tvCuotaPeriodo = findViewById<TextView>(R.id.tvCuotaPeriodo)

        // --- 3. ACTUALIZAR TEXTVIEWS ---
        tvCuotaNumero.text = "CUOTA N° ${membershipData.membershipId}"
        // Asumo que 'membershipData' tiene un campo 'dueDate' tipo String "YYYY-MM-DD"
        // Si el campo se llama diferente (ej: 'period'), ajústalo aquí.
        tvCuotaPeriodo.text = membershipData.formattedPeriod

        // --- 4. NAVEGACIÓN ---
        btnBack.setOnClickListener {
            // Esta actividad es final, 'finish()' debería volver
            // a la actividad que inició el flujo de pago
            finish()
        }

        // Botón Menú
        btnMenu.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Volver al Menú")
                .setMessage("¿Desea volver al menú principal? Se finalizará el flujo de pago.")
                .setPositiveButton("Sí") { _, _ ->
                    val intent = Intent(this, MenuPrincipalActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                }
                .setNegativeButton("No", null)
                .show()
        }

        // Botón Carnet
        btnCarnet.setOnClickListener {
            val intent = Intent(this, PagoSocioCarnetActivity::class.java)
            // Pasa CLIENT_DATA
            intent.putExtra("CLIENT_DATA", clientData)
            // ¡¡Y también pasa MEMBERSHIP_DATA!!
            intent.putExtra("MEMBERSHIP_DATA", membershipData)
            startActivity(intent)
        }

        // Botón Comprobante
        btnComprobante.setOnClickListener {
            val intent = Intent(this, PagoSocioComprobanteActivity::class.java)
            // Pasamos ambos objetos para generar el comprobante
            intent.putExtra("CLIENT_DATA", clientData)
            intent.putExtra("MEMBERSHIP_DATA", membershipData)
            // Aca pasar los datos del pago (monto, cuotas)
            // intent.putExtra("FINAL_AMOUNT", intent.getDoubleExtra("FINAL_AMOUNT", 0.0))
            startActivity(intent)
        }

    }


}