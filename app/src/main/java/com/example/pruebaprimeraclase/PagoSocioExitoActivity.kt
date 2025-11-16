package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback // <-- IMPORTACIÓN REQUERIDA
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class PagoSocioExitoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_socio_exito)

        // --- SOLUCIÓN AL ERROR DE MÉTODO DE RETROCESO ---
        // Implementación del OnBackPressedCallback (debe ir dentro de onCreate)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Lógica de navegación segura al presionar el botón físico 'Atrás'
                val intent = Intent(this@PagoSocioExitoActivity, MenuPrincipalActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                this@PagoSocioExitoActivity.finish()
            }
        })
        // ----------------------------------------------------

        // --- EL RESTO DE LA INICIALIZACIÓN DEBE IR AQUÍ ---
        // Corregido: El dbHelper y todos los findViewById ahora están dentro de onCreate.
        dbHelper = DBHelper(this)

        // Obtener referencias a los Views
        val tvPaymentId = findViewById<TextView>(R.id.tvPaymentId)
        val tvAmount = findViewById<TextView>(R.id.tvAmount)
        val tvPaymentMethod = findViewById<TextView>(R.id.tvPaymentMethod)
        val tvInstallments = findViewById<TextView>(R.id.tvInstallments)
        val tvPaymentDate = findViewById<TextView>(R.id.tvPaymentDate)
        val tvMembershipPeriod = findViewById<TextView>(R.id.tvMembershipPeriod)
        val tvNextDueDate = findViewById<TextView>(R.id.tvNextDueDate)
        val btnVolverMenu = findViewById<Button>(R.id.btnVolverMenu)
        val btnNuevoPago = findViewById<Button>(R.id.btnNuevoPago)
        val btnGenerarCarnet = findViewById<Button>(R.id.btnGenerarCarnet)
        val layoutCarnetMessage = findViewById<LinearLayout>(R.id.layoutCarnetMessage)

        // Obtener el CLIENT_ID del Intent
        val clientId = intent.getIntExtra("CLIENT_ID", -1)
        val membershipId = intent.getIntExtra("MEMBERSHIP_ID", -1)

        if (clientId == -1 || membershipId == -1) {
            // Si no hay datos, volver al menú
            navigateToMenu()
            return
        }

        // Cargar datos del último pago del cliente desde la BD
        val payment = dbHelper.getLastPaymentByClient(clientId)
        val membership = dbHelper.getSocioMembershipList(clientId)

        if (payment == null) {
            navigateToMenu()
            return
        }

        // Formatear y mostrar datos
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
        formatter.maximumFractionDigits = 0

        // ID del pago
        tvPaymentId.text = "Nº ${payment.paymentId}"

        // Monto
        tvAmount.text = formatter.format(payment.amount)

        // Método de pago
        val methodText = when (payment.paymentMethod) {
            "EFECTIVO" -> "Efectivo"
            "TARJETA" -> "Tarjeta de Crédito"
            else -> payment.paymentMethod ?: "N/A"
        }
        tvPaymentMethod.text = methodText

        // Cuotas (solo mostrar si es más de 1)
        if (payment.installments > 1) {
            tvInstallments.text = "${payment.installments} cuotas"
        } else {
            tvInstallments.text = "Pago único"
        }

        // Fecha de pago
        tvPaymentDate.text = formatDate(payment.paymentDate ?: "")

        // Período de la membresía activa
        membership.currentMembership?.let { current ->
            tvMembershipPeriod.text = current.formattedPeriod
        }

        // Próximo vencimiento
        membership.nextMembership?.let { next ->
            tvNextDueDate.text = formatDate(next.expiryDate)
        } ?: run {
            tvNextDueDate.text = "N/A"
        }

        // Verificar si es primera cuota (mostrar opción de generar carnet)
        // Nota: Mantenemos la consulta a la BD que tenías en el código.
        val isFirstPayment = payment.membershipId?.let { _ ->
            val allPayments = dbHelper.readableDatabase.rawQuery(
                "SELECT COUNT(*) FROM payments WHERE clientId = ? AND paymentType = 'MENSUAL'",
                arrayOf(clientId.toString())
            )
            allPayments.moveToFirst()
            val count = allPayments.getInt(0)
            allPayments.close()
            count == 1
        } ?: false

        if (isFirstPayment) {
            layoutCarnetMessage.visibility = View.VISIBLE
            btnGenerarCarnet.visibility = View.VISIBLE
        } else {
            layoutCarnetMessage.visibility = View.GONE
            btnGenerarCarnet.visibility = View.GONE
        }

        // Botones
        btnVolverMenu.setOnClickListener {
            navigateToMenu()
        }

        btnNuevoPago.setOnClickListener {
            // Limpiar el back stack y volver a RegistrarPagosActivity
            val intent = Intent(this, RegistrarPagosActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        btnGenerarCarnet.setOnClickListener {
            // TODO: Implementar generación de carnet
            // Por ahora solo mostrar mensaje
            Toast.makeText(this, "Función de generar carnet en desarrollo", Toast.LENGTH_SHORT).show()
            // Aquí iría la navegación a la actividad de generar/imprimir carnet
        }
    } // <-- onCreate() cierra correctamente aquí

    private fun formatDate(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatter.format(parser.parse(dateString)!!)
        } catch (e: Exception) {
            dateString
        }
    }

    private fun navigateToMenu() {
        val intent = Intent(this, MenuPrincipalActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    // Corregido: El método obsoleto onBackPressed() se eliminó
}