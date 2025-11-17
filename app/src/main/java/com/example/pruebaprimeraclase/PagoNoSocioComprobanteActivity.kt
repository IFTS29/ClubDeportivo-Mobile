package com.example.pruebaprimeraclase

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PagoNoSocioComprobanteActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    // Datos recibidos
    private var paymentId: Long = -1L
    private var clientDoc: String? = null

    // Referencias de la UI
    private lateinit var tvReceiptDate: TextView
    private lateinit var tvReceiptNumber: TextView
    private lateinit var tvClientName: TextView
    private lateinit var tvClientId: TextView
    private lateinit var tvPaymentDate: TextView
    private lateinit var tvPaymentMethod: TextView
    private lateinit var llActivityList: LinearLayout
    private lateinit var tvTotalAmount: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_nosocio_comprobante)

        dbHelper = DBHelper(this)

        // --- 1. CAPTURAR DATOS DEL INTENT ---
        paymentId = intent.getLongExtra("PAYMENT_ID", -1L)
        clientDoc = intent.getStringExtra("CLIENT_DOC")

        if (paymentId == -1L || clientDoc == null) {
            Toast.makeText(this, "Error al cargar datos del comprobante", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // --- 2. INICIALIZAR REFERENCIAS ---
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val btnPrint = findViewById<Button>(R.id.btnPrint) // ID del XML

        tvReceiptDate = findViewById(R.id.tvReceiptDate)
        tvReceiptNumber = findViewById(R.id.tvReceiptNumber)
        tvClientName = findViewById(R.id.tvClientName)
        tvClientId = findViewById(R.id.tvClientId)
        tvPaymentDate = findViewById(R.id.tvPaymentDate)
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod)
        llActivityList = findViewById(R.id.llActivityList)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)

        // --- 3. NAVEGACIÓN ---
        btnBack.setOnClickListener {
            // Vuelve a la pantalla de "Éxito" (que mantiene sus datos)
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

        // --- 4. BOTÓN IMPRIMIR ---
        btnPrint.setOnClickListener {
            // 1. Muestra el mensaje
            Toast.makeText(this, "Impresión exitosa", Toast.LENGTH_LONG).show()

            // 2. Redirige al Menú Principal (limpiando el historial de pago)
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        // --- 5. CARGAR DATOS ---
        loadData()
    }

    /**
     * Carga todos los datos del cliente, pago y actividades en la pantalla.
     */
    private fun loadData() {
        // Formateador de moneda
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
        formatter.maximumFractionDigits = 0

        // 1. Cargar Datos del Cliente (usando el DNI)
        val client = dbHelper.getClientByDoc(clientDoc!!)
        if (client != null) {
            tvClientName.text = "${client.firstName.uppercase()} ${client.lastName.uppercase()}"
            tvClientId.text = client.clientId.toString()
        } else {
            tvClientName.text = "CLIENTE NO ENCONTRADO"
            tvClientId.text = "N/A"
        }

        // 2. Cargar Datos del Pago (usando el PaymentID)
        val payment = dbHelper.getPaymentById(paymentId)
        if (payment != null) {
            tvReceiptDate.text = formatDisplayDate(payment.paymentDate ?: "")
            tvReceiptNumber.text = "Nro: ${payment.paymentId}"
            tvPaymentDate.text = formatDisplayDate(payment.paymentDate ?: "")
            tvPaymentMethod.text = payment.paymentMethod ?: "N/A"
            tvTotalAmount.text = "Monto: ${formatter.format(payment.amount)}"
        } else {
            // Fallback
            tvReceiptDate.text = formatDisplayDate(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
            tvReceiptNumber.text = "Nro: ${paymentId}"
            tvTotalAmount.text = "Monto: Error"
        }

        // 3. Cargar Lista de Actividades (usando el PaymentID)
        val activities = dbHelper.getActivitiesForPayment(paymentId)
        llActivityList.removeAllViews() // Limpia el contenedor (no borra el título)

        if (activities.isEmpty()) {
            addActivityItem("- Sin Actividades -") // Muestra un item de fallback
        } else {
            for (activity in activities) {
                // --- NUEVO FORMATO DE TEXTO ---
                val actText = "- ${activity.activityName} ${activity.activityTime}hs"
                addActivityItem(actText)
            }
        }
    }

    /**
     * Función auxiliar para crear los TextViews de la lista de actividades.
     * (Modificada para un solo parámetro)
     */
    private fun addActivityItem(activityText: String) {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            // Margen izquierdo para indentar el guion
            leftMargin = (8 * resources.displayMetrics.density).toInt() // 8dp
            bottomMargin = (4 * resources.displayMetrics.density).toInt() // 4dp (menos espacio)
        }

        // TextView para el nombre de la actividad
        val tvAct = TextView(this).apply {
            text = activityText
            textSize = 14f
            setTextColor(ContextCompat.getColor(context, android.R.color.black))
            setTypeface(null, Typeface.NORMAL) // SIN negrita
            layoutParams = params
        }

        llActivityList.addView(tvAct)
    }

    /**
     * Formatea una fecha de "YYYY-MM-DD" a "dd/MM/yyyy".
     */
    private fun formatDisplayDate(dateString: String): String {
        if (dateString.isEmpty()) return "N/A"
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatter.format(parser.parse(dateString)!!)
        } catch (e: Exception) {
            dateString // Devuelve el original si falla
        }
    }
}