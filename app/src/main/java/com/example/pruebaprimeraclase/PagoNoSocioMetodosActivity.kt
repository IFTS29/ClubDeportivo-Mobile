package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.NumberFormat
import java.util.Locale

class PagoNoSocioMetodosActivity : AppCompatActivity() {

    // Se inicializa DBHelper
    private lateinit var dbHelper: DBHelper

    // Referencias a elementos del layout (propiedades lateinit)
    private lateinit var rgPaymentMethods: RadioGroup
    private lateinit var cardInstallments: LinearLayout
    private lateinit var cardTotalAmount: LinearLayout
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnPay: Button
    private lateinit var tvInstallmentPrice1: TextView
    private lateinit var tvInstallmentPrice3: TextView
    private lateinit var tvInstallmentPrice6: TextView
    private lateinit var llInstallment1: LinearLayout
    private lateinit var llInstallment3: LinearLayout
    private lateinit var llInstallment6: LinearLayout
    private lateinit var allActivities: List<ActivityData>


    //  Datos Recibidos
    private var totalAmount: Double = 0.0
    private var clientDoc: String? = null
    private var selectedActivityIds = listOf<Int>()

    // Se guarda la cuota seleccionada
    private var selectedInstallments: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_nosocio_metodos)

        // Se inicializa DBHelper
        dbHelper = DBHelper(this)

        // Se carga la lista de actividades (para los nombres en el diálogo)
        allActivities = dbHelper.getAllActivities()

        // Se reciben los datos de la actividad anterior
        totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0)
        clientDoc = intent.getStringExtra("CLIENT_DOC")
        selectedActivityIds = intent.getIntegerArrayListExtra("SELECTED_ACTIVITY_IDS")?.toList() ?: emptyList()

        // Referencias a los elementos del layout
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        btnPay = findViewById(R.id.btnPay)
        rgPaymentMethods = findViewById(R.id.rgPaymentMethods)
        cardInstallments = findViewById(R.id.cardInstallments)
        cardTotalAmount = findViewById(R.id.cardTotalAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvInstallmentPrice1 = findViewById(R.id.tvInstallmentPrice1)
        tvInstallmentPrice3 = findViewById(R.id.tvInstallmentPrice3)
        tvInstallmentPrice6 = findViewById(R.id.tvInstallmentPrice6)

        // Se inicializan las referencias de los LinearLayouts
        llInstallment1 = findViewById(R.id.llInstallment1)
        llInstallment3 = findViewById(R.id.llInstallment3)
        llInstallment6 = findViewById(R.id.llInstallment6)


        // ESTADO INICIAL
        cardInstallments.visibility = View.GONE
        cardTotalAmount.visibility = View.GONE
        btnPay.isEnabled = false
        btnPay.alpha = 0.5f

        // NAVEGACIÓN
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


        // Formatear el precio
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
        formatter.maximumFractionDigits = 0

        // Asignar los montos
        tvInstallmentPrice1.text = formatter.format(totalAmount)
        tvInstallmentPrice3.text = "3 x ${formatter.format(totalAmount / 3)}"
        tvInstallmentPrice6.text = "6 x ${formatter.format(totalAmount / 6)}"
        tvTotalAmount.text = formatter.format(totalAmount)

        // Lógica de UI
        rgPaymentMethods.setOnCheckedChangeListener { _, _ ->
            btnPay.isEnabled = true
            btnPay.alpha = 1.0f
            cardTotalAmount.visibility = View.VISIBLE

            val radioTarjeta = findViewById<RadioButton>(R.id.rbCard)
            val radioEfectivo = findViewById<RadioButton>(R.id.rbCash)

            if (radioTarjeta.isChecked) {
                cardInstallments.visibility = View.VISIBLE
                selectedInstallments = 1 // 1 cuota por defecto
                selectInstallment(1)
            } else if (radioEfectivo.isChecked) {
                cardInstallments.visibility = View.GONE
                selectedInstallments = 1 // Resetea a 1 cuota
                resetInstallmentSelection() // Limpia la selección visual
            }
        }

        // Para poder seleccionar la cantidad de cuotas
        llInstallment1.setOnClickListener {
            selectedInstallments = 1
            selectInstallment(1)
        }

        llInstallment3.setOnClickListener {
            selectedInstallments = 3
            selectInstallment(3)
        }

        llInstallment6.setOnClickListener {
            selectedInstallments = 6
            selectInstallment(6)
        }


        // BOTÓN PAGAR
        btnPay.setOnClickListener {

            // Se guardan los nombres de las actividades seleccionadas
            val selectedActivityNames = allActivities
                .filter { selectedActivityIds.contains(it.activityId) }
                .map { "- ${it.activityName}" }
                .joinToString("\n")

            // Se formatea el precio total
            val totalText = formatter.format(totalAmount)

            // Se crea el mensaje
            val dialogMessage = "Confirmar pago para:\n\n$selectedActivityNames\n\nTotal a pagar: $totalText"

            // Se muestra el cuadro de diálogo
            AlertDialog.Builder(this)
                .setTitle("Confirmar Pago")
                .setMessage(dialogMessage)
                .setPositiveButton("Sí, pagar") { _, _ ->

                    // 1. Determinar medio de pago y cantidad cuotas
                    val radioTarjeta = findViewById<RadioButton>(R.id.rbCard)
                    val paymentMethod = if (radioTarjeta.isChecked) "TARJETA" else "EFECTIVO"
                    val installments = if (radioTarjeta.isChecked) selectedInstallments else 1

                    // Se valida el Doc cliente
                    val client = clientDoc?.let { dbHelper.getClientByDoc(it) }
                    if (client == null) {
                        Toast.makeText(this, "Error fatal: No se pudo verificar el cliente.", Toast.LENGTH_LONG).show()
                        return@setPositiveButton // Se aborta el pago
                    }

                    // Se ejecuta el pago en la BD
                    val newPaymentId = dbHelper.payDailyRegistration(
                        clientId = client.clientId,
                        totalAmount = totalAmount,
                        selectedActivityIds = selectedActivityIds,
                        paymentMethod = paymentMethod,
                        installments = installments
                    )

                    // Navegación según el resultado
                    if (newPaymentId != -1L) {
                        val intent = Intent(this, PagoNoSocioExitoActivity::class.java)

                        // Información para la pantalla de éxito
                        intent.putExtra("PAYMENT_ID", newPaymentId)
                        intent.putExtra("CLIENT_DOC", clientDoc)

                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Error al procesar el pago. Intente de nuevo.", Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }


    // FUNCIONES AUXILIARES

    // Se marcan visualmente la opcion de cuotas seleccioanda
    private fun selectInstallment(installments: Int) {
        // se resetea el estilo
        resetInstallmentSelection()

        // se marca la opcion seleccionada
        val selectedLayout = when (installments) {
            1 -> llInstallment1
            3 -> llInstallment3
            6 -> llInstallment6
            else -> llInstallment1
        }

        // Cambia el fondo y color
        selectedLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.verde_exito_claro_bg))

        for (i in 0 until selectedLayout.childCount) {
            val child = selectedLayout.getChildAt(i)
            if (child is TextView) {
                child.setTextColor(ContextCompat.getColor(this, R.color.verde_exito))
            }
        }
    }

    // Se resetea visualmente la opcion de cuotas seleccioanda
    private fun resetInstallmentSelection() {
        val defaultBg = ContextCompat.getColor(this, android.R.color.transparent)
        val defaultText = ContextCompat.getColor(this, android.R.color.black)

        // Se resetean las 3 opciones de cuota
        listOf(llInstallment1, llInstallment3, llInstallment6).forEach { layout ->
            layout.setBackgroundColor(defaultBg)

            for (i in 0 until layout.childCount) {
                val child = layout.getChildAt(i)
                if (child is TextView) {
                    child.setTextColor(defaultText)
                }
            }
        }
    }
}