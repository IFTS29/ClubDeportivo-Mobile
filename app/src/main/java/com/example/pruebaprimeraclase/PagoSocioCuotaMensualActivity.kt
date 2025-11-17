package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class PagoSocioCuotaMensualActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private var membershipToPay: MembershipData? = null
    private var currentClient: ClientData? = null

    // Referencias a los Views
    private lateinit var tvClientName: TextView
    private lateinit var tvClientId: TextView
    private lateinit var membershipsList: LinearLayout
    private lateinit var btnContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_socio_cuota)

        dbHelper = DBHelper(this)

        // --- 1. CAPTURAR DATOS DEL INTENT ---
        val docNumber = intent.getStringExtra("CLIENT_DOC")

        if (docNumber == null) {
            finish()
            return
        }

        // --- 2. INICIALIZAR REFERENCIAS ---
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        btnContinue = findViewById(R.id.btnContinue)
        tvClientName = findViewById(R.id.tvClientName)
        tvClientId = findViewById(R.id.tvClientId)
        membershipsList = findViewById(R.id.membershipsList)

        // --- 3. ESTADO INICIAL ---
        btnContinue.isEnabled = false
        btnContinue.alpha = 0.5f

        // --- 4. NAVEGACIÓN ---
        btnBack.setOnClickListener {
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

        // --- 5. CARGAR DATOS ---
        loadClientData(docNumber)

        if (currentClient != null) {
            loadAllMemberships()
        }

        // --- 6. BOTÓN CONTINUAR ---
        btnContinue.setOnClickListener {
            membershipToPay?.let { membership ->
                val intent = Intent(this, PagoSocioMetodosActivity::class.java)
                intent.putExtra("MEMBERSHIP_TO_PAY", membership)
                intent.putExtra("CLIENT_DATA", currentClient)
                startActivityForResult(intent, REQUEST_CODE_PAYMENT)
            }
        }
    }

    private fun loadClientData(docNumber: String) {
        currentClient = dbHelper.getClientByDoc(docNumber)

        if (currentClient != null) {
            tvClientName.text = "${currentClient!!.firstName.uppercase()} ${currentClient!!.lastName.uppercase()}"
            tvClientId.text = "ID SOCIO ${currentClient!!.clientId}"
        } else {
            tvClientName.text = "CLIENTE NO ENCONTRADO"
            tvClientId.text = "Error"
        }
    }

    private fun loadAllMemberships() {
        membershipsList.removeAllViews()

        val clientId = currentClient!!.clientId

        // Obtener solo las membresías relevantes (no mostrar finalizadas ni vencidas antiguas)
        val allMemberships = dbHelper.getRelevantMembershipsForClient(clientId)

        if (allMemberships.isEmpty()) {
            addErrorCard()
            return
        }

        // Limitar a las 3 primeras (ACTIVA + 2 PENDIENTES como máximo)
        val membershipsToShow = allMemberships.take(3)

        // Agregar cada membresía como una card
        membershipsToShow.forEachIndexed { index, membership ->
            addMembershipCard(membership, index == membershipsToShow.size - 1)
        }
    }

    private fun addMembershipCard(membership: MembershipData, isLast: Boolean) {
        val inflater = LayoutInflater.from(this)
        val cardView = inflater.inflate(R.layout.item_membership_card, membershipsList, false)

        val statusIndicator = cardView.findViewById<View>(R.id.statusIndicator)
        val tvMembershipId = cardView.findViewById<TextView>(R.id.tvMembershipId)
        val tvStatus = cardView.findViewById<TextView>(R.id.tvStatus)
        val tvInfo = cardView.findViewById<TextView>(R.id.tvInfo)
        val tvDaysRemaining = cardView.findViewById<TextView>(R.id.tvDaysRemaining)

        // ID de la cuota
        tvMembershipId.text = "CUOTA ID ${membership.membershipId}"

        // [NUEVA LÓGICA CLAVE] Verifica si la cuota PENDIENTE tiene un registro de pago asociado
        val isPaidInAdvance = membership.status == "PENDIENTE" && membership.paymentId != null

        when (membership.status) {
            "ACTIVA" -> {
                // Círculo verde
                statusIndicator.backgroundTintList = ContextCompat.getColorStateList(this, R.color.verde_exito)

                tvStatus.text = "ESTADO: ACTIVA"
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.verde_exito))

                tvInfo.text = "PERIODO: ${membership.formattedPeriod}"

                // Mostrar cuándo termina
                tvDaysRemaining.visibility = View.VISIBLE
                tvDaysRemaining.text = membership.daysRemainingText.replace("Su cuota actual vence", "Termina")
            }

            "PENDIENTE" -> {
                if (isPaidInAdvance) {
                    // CASO 1: CUOTA PENDIENTE PAGADA POR ADELANTADO (paymentId != null)
                    statusIndicator.backgroundTintList = ContextCompat.getColorStateList(this, R.color.verde_exito)

                    // Texto corregido para mostrar el estado correcto
                    tvStatus.text = "ESTADO: PENDIENTE (Pagada por adelantado)"
                    tvStatus.setTextColor(ContextCompat.getColor(this, R.color.verde_exito))

                    tvInfo.text = "Período: ${membership.formattedPeriod}"

                    tvDaysRemaining.visibility = View.VISIBLE
                    tvDaysRemaining.text = "Se activará el ${DateUtils.formatShortDate(membership.startDate)}"

                    // NO se permite pagar de nuevo, se asegura que el botón esté deshabilitado
                    // si no hay una VENCIDA que lo sobrescriba más adelante.

                } else {
                    // CASO 2: CUOTA PENDIENTE SIN PAGAR (Primera cuota o cuota futura sin pago)
                    statusIndicator.backgroundTintList = ContextCompat.getColorStateList(this, R.color.rojo_alerta)

                    tvStatus.text = "ESTADO: PENDIENTE DE PAGO"
                    tvStatus.setTextColor(ContextCompat.getColor(this, R.color.rojo_alerta))

                    tvInfo.text = "Cuota sin pagar. Puede ser pagada anticipadamente."
                    tvDaysRemaining.visibility = View.GONE

                    // Habilitar el pago para esta cuota PENDIENTE.
                    membershipToPay = membership
                    btnContinue.isEnabled = true
                    btnContinue.alpha = 1.0f
                }
            }

            "VENCIDA" -> {
                statusIndicator.backgroundTintList = ContextCompat.getColorStateList(this, R.color.rojo_alerta)

                tvStatus.text = "ESTADO: VENCIDA"
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.rojo_alerta))

                tvInfo.text = membership.daysRemainingText.uppercase()
                tvDaysRemaining.visibility = View.GONE

                // Cuota vencida: habilitar el pago (tiene prioridad).
                membershipToPay = membership
                btnContinue.isEnabled = true
                btnContinue.alpha = 1.0f
            }

            // Se puede añadir un 'else' para cualquier otro estado no esperado (ej. FINALIZADA si se filtra mal).
        }

        membershipsList.addView(cardView)
    }

    private fun addErrorCard() {
        val textView = TextView(this).apply {
            text = "No se encontraron cuotas para este socio"
            textSize = 14f
            setTextColor(ContextCompat.getColor(context, R.color.rojo_alerta))
            setPadding(24, 24, 24, 24)
        }
        membershipsList.addView(textView)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PAYMENT && resultCode == RESULT_OK) {
            if (currentClient != null) {
                loadAllMemberships()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PAYMENT = 1001
    }
}