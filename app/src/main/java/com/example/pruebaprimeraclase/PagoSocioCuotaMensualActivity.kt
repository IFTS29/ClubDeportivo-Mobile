package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class PagoSocioCuotaMensualActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private var membershipToPay: MembershipData? = null // Guardamos el objeto completo

    // Guardamos el objeto cliente completo (lo obtenemos usando el DNI)
    private var currentClient: ClientData? = null

    // Referencias a los Views de Card 1
    private lateinit var tvClientName: TextView
    private lateinit var tvClientId: TextView

    // Referencias a los Views de Card 2
    private lateinit var tvCurrentTitle: TextView
    private lateinit var tvCurrentId: TextView
    private lateinit var tvCurrentStatus: TextView
    private lateinit var tvCurrentPeriodInfo: TextView
    private lateinit var divider: View
    private lateinit var nextFeeLayout: LinearLayout
    private lateinit var tvNextTitle: TextView
    private lateinit var tvNextId: TextView
    private lateinit var tvNextStatus: TextView
    private lateinit var tvNextDueDate: TextView
    private lateinit var btnContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_socio_cuota)

        dbHelper = DBHelper(this)

        // --- 1. CAPTURAR DATOS DEL INTENT ---
        // Recibe el DNI de la pantalla anterior, usando la clave "CLIENT_DOC"
        val docNumber = intent.getStringExtra("CLIENT_DOC")

        if (docNumber == null) {
            finish() // No se puede continuar sin un DNI
            return
        }

        // --- 2. INICIALIZAR REFERENCIAS ---
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        btnContinue = findViewById(R.id.btnContinue)
        tvClientName = findViewById(R.id.tvClientName)
        tvClientId = findViewById(R.id.tvClientId)
        tvCurrentTitle = findViewById(R.id.tvCurrentTitle)
        tvCurrentId = findViewById(R.id.tvCurrentId)
        tvCurrentStatus = findViewById(R.id.tvCurrentStatus)
        tvCurrentPeriodInfo = findViewById(R.id.tvCurrentPeriodInfo)
        divider = findViewById(R.id.divider)
        nextFeeLayout = findViewById(R.id.nextFeeLayout)
        tvNextTitle = findViewById(R.id.tvNextTitle)
        tvNextId = findViewById(R.id.tvNextId)
        tvNextStatus = findViewById(R.id.tvNextStatus)
        tvNextDueDate = findViewById(R.id.tvNextDueDate)

        // --- 3. ESTADO INICIAL ---
        btnContinue.isEnabled = false
        btnContinue.alpha = 0.5f

        // --- 4. NAVEGACIÓN ---
        btnBack.setOnClickListener {
            finish()
        }
        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        // --- 5. CARGAR DATOS ---

        // Cargar Card 1 (consultando la BD con el DNI)
        loadClientData(docNumber)

        // Cargar Card 2 (SOLO si el cliente se cargó bien)
        if (currentClient != null) {
            loadMembershipData()
        }

        // --- 6. BOTÓN CONTINUAR ---
        btnContinue.setOnClickListener {
            membershipToPay?.let { membership ->
                val intent = Intent(this, PagoSocioMetodosActivity::class.java)
                intent.putExtra("MEMBERSHIP_TO_PAY", membership) // Pasamos el objeto completo
                intent.putExtra("CLIENT_DATA", currentClient) // También pasamos el cliente
                startActivityForResult(intent, REQUEST_CODE_PAYMENT)
            }
        }
    }

    // Recargar datos cuando volvemos de la pantalla de pago
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PAYMENT && resultCode == RESULT_OK) {
            // Recargar los datos del cliente y membresía
            if (currentClient != null) {
                loadMembershipData()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PAYMENT = 1001
    }

    /**
     * Carga los datos del socio en la Card 1.
     * USA LA FUNCIÓN 'getClientByDoc' (que ya existe en tu DBHelper)
     */
    private fun loadClientData(docNumber: String) {
        // Llama a la función existente del DBHelper usando el DNI
        currentClient = dbHelper.getClientByDoc(docNumber)

        if (currentClient != null) {
            tvClientName.text = "${currentClient!!.firstName.uppercase()} ${currentClient!!.lastName.uppercase()}"
            tvClientId.text = "ID SOCIO ${currentClient!!.clientId}"
        } else {
            tvClientName.text = "CLIENTE NO ENCONTRADO"
            tvClientId.text = "Error"
        }
    }

    /**
     * Carga los datos de membresía en la Card 2.
     * Esta función usa el 'clientId' que obtuvimos en 'loadClientData'.
     */
    private fun loadMembershipData() {
        // Usa el ID del cliente que ya buscamos
        val clientId = currentClient!!.clientId
        val membershipList = dbHelper.getSocioMembershipList(clientId)

        val current = membershipList.currentMembership
        val next = membershipList.nextMembership

        if (current == null) {
            // Caso 1: Error, no se encontró membresía
            tvCurrentTitle.text = "ERROR"
            tvCurrentId.text = "No se encontró membresía para este socio."
            tvCurrentStatus.visibility = View.GONE
            tvCurrentPeriodInfo.visibility = View.GONE
            divider.visibility = View.GONE
            nextFeeLayout.visibility = View.GONE
            return
        }

        // Cargar datos de la Membresía "Vigente"
        tvCurrentId.text = "ID ${current.membershipId}"
        tvCurrentStatus.text = "ESTADO: ${current.status}"

        if (current.status == "ACTIVA") {
            // --- CASO 2: SOCIO AL DÍA (ACTIVA + PENDIENTE) ---
            tvCurrentStatus.setTextColor(ContextCompat.getColor(this, R.color.verde_exito))
            tvCurrentPeriodInfo.text = "PERIODO: ${current.formattedPeriod}"

            if (next != null) {
                divider.visibility = View.VISIBLE
                nextFeeLayout.visibility = View.VISIBLE

                tvNextId.text = "ID CUOTA: ${next.membershipId}"

                // Mostrar estado más claro del pago
                if (next.startDate.isEmpty() || next.startDate == "null") {
                    tvNextStatus.text = "PENDIENTE DE PAGO"
                    tvNextStatus.setTextColor(ContextCompat.getColor(this, R.color.rojo_alerta))
                } else {
                    tvNextStatus.text = "PAGADA - Vigente desde ${DateUtils.formatShortDate(next.startDate)}"
                    tvNextStatus.setTextColor(ContextCompat.getColor(this, R.color.verde_exito))
                }

                // ✅ CORREGIDO: Mostrar el vencimiento de la cuota ACTUAL (ACTIVA), no la PENDIENTE
                tvNextDueDate.text = current.daysRemainingText

                membershipToPay = next // Guardamos el objeto completo
                btnContinue.isEnabled = true
                btnContinue.alpha = 1.0f
            } else {
                divider.visibility = View.GONE
                nextFeeLayout.visibility = View.GONE
            }

        } else if (current.status == "VENCIDA") {
            // --- CASO 3: SOCIO DEUDOR (SOLO VENCIDA) ---
            tvCurrentStatus.setTextColor(ContextCompat.getColor(this, R.color.rojo_alerta))
            tvCurrentPeriodInfo.text = current.daysRemainingText.uppercase()

            divider.visibility = View.GONE
            nextFeeLayout.visibility = View.GONE

            membershipToPay = current // Guardamos el objeto completo
            btnContinue.isEnabled = true
            btnContinue.alpha = 1.0f

        } else if (current.status == "PENDIENTE") {
            // --- CASO 4: SOCIO NUEVO (Aún no paga la 1ra cuota) ---
            tvCurrentTitle.text = "PRIMERA CUOTA"
            tvCurrentStatus.text = "ESTADO: PENDIENTE"
            tvCurrentStatus.setTextColor(ContextCompat.getColor(this, R.color.rojo_alerta))
            tvCurrentPeriodInfo.text = "PENDIENTE DE PAGO INICIAL"

            divider.visibility = View.GONE
            nextFeeLayout.visibility = View.GONE

            membershipToPay = current // Guardamos el objeto completo
            btnContinue.isEnabled = true
            btnContinue.alpha = 1.0f
        }
    }
}