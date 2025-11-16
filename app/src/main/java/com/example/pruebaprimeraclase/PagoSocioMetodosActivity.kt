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

class PagoSocioMetodosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private var membershipIdToPay: Int = -1
    private var baseFee: Double = 0.0

    // Referencias a los Views
    private lateinit var rgMetodosPago: RadioGroup
    private lateinit var opEfectivo: RadioButton
    private lateinit var opTarjeta: RadioButton
    private lateinit var cardCantidadCuotas: LinearLayout
    private lateinit var cardTotalPagar: LinearLayout
    private lateinit var llCuota1: LinearLayout
    private lateinit var llCuota3: LinearLayout
    private lateinit var llCuota6: LinearLayout
    private lateinit var tvPrecioCuota1: TextView
    private lateinit var tvPrecioCuota3: TextView
    private lateinit var tvPrecioCuota6: TextView
    private lateinit var tvTotalPagar: TextView
    private lateinit var btnPagar: Button

    // Variable para almacenar la cantidad de cuotas seleccionada
    private var selectedInstallments: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_socio_metodos)

        dbHelper = DBHelper(this)

        // --- 1. CAPTURAR MEMBERSHIP_ID ---
        membershipIdToPay = intent.getIntExtra("MEMBERSHIP_ID_TO_PAY", -1)

        if (membershipIdToPay == -1) {
            Toast.makeText(this, "Error: No se pudo cargar la información de la cuota", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // --- 2. OBTENER PRECIO BASE DE LA MEMBRESÍA ---
        baseFee = getMembershipFee(membershipIdToPay)

        // --- 3. INICIALIZAR REFERENCIAS ---
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        rgMetodosPago = findViewById(R.id.rgMetodosPago)
        opEfectivo = findViewById(R.id.opEfectivo)
        opTarjeta = findViewById(R.id.opTarjeta)
        cardCantidadCuotas = findViewById(R.id.cardCantidadCuotas)
        cardTotalPagar = findViewById(R.id.cardTotalPagar)
        tvTotalPagar = findViewById(R.id.tvTotalPagar)
        btnPagar = findViewById(R.id.btnPagar)

        // Obtener referencias a los LinearLayout de las cuotas
        llCuota1 = cardCantidadCuotas.getChildAt(1) as LinearLayout
        llCuota3 = cardCantidadCuotas.getChildAt(3) as LinearLayout
        llCuota6 = cardCantidadCuotas.getChildAt(5) as LinearLayout

        // Obtener referencias a los TextViews de precios
        tvPrecioCuota1 = llCuota1.getChildAt(1) as TextView
        tvPrecioCuota3 = llCuota3.getChildAt(1) as TextView
        tvPrecioCuota6 = llCuota6.getChildAt(1) as TextView

        // --- 4. ESTADO INICIAL ---
        cardCantidadCuotas.visibility = View.GONE
        cardTotalPagar.visibility = View.GONE

        updateInstallmentPrices()
        selectedInstallments = 1

        // --- 5. NAVEGACIÓN ---
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

        // --- 6. LISTENER PARA MÉTODO DE PAGO ---
        rgMetodosPago.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.opEfectivo -> {
                    // Ocultar opciones de cuotas
                    cardCantidadCuotas.visibility = View.GONE
                    // Mostrar total
                    cardTotalPagar.visibility = View.VISIBLE
                    selectedInstallments = 1
                    resetInstallmentSelection()
                    updateTotalDisplay()
                }
                R.id.opTarjeta -> {
                    // Mostrar opciones de cuotas
                    cardCantidadCuotas.visibility = View.VISIBLE
                    cardTotalPagar.visibility = View.VISIBLE
                    // Por defecto seleccionar 1 cuota
                    selectedInstallments = 1
                    selectInstallment(1)
                    updateTotalDisplay()
                }
            }
        }

        // --- 7. HACER CLICKEABLES LOS LINEARAYOUT DE CUOTAS ---
        llCuota1.setOnClickListener {
            if (opTarjeta.isChecked) {
                selectedInstallments = 1
                selectInstallment(1)
                updateTotalDisplay()
            }
        }

        llCuota3.setOnClickListener {
            if (opTarjeta.isChecked) {
                selectedInstallments = 3
                selectInstallment(3)
                updateTotalDisplay()
            }
        }

        llCuota6.setOnClickListener {
            if (opTarjeta.isChecked) {
                selectedInstallments = 6
                selectInstallment(6)
                updateTotalDisplay()
            }
        }

        // --- 8. BOTÓN PAGAR ---
        btnPagar.setOnClickListener {
            procesarPago()
        }
    }

    /**
     * Obtiene el precio base de la membresía desde la BD
     */
    private fun getMembershipFee(membershipId: Int): Double {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT monthlyFee FROM memberships WHERE membershipId = ?",
            arrayOf(membershipId.toString())
        )

        var fee = 50000.0 // Valor por defecto
        if (cursor.moveToFirst()) {
            fee = cursor.getDouble(0)
        }
        cursor.close()
        return fee
    }

    /**
     * Actualiza los precios mostrados según las cuotas y recargos
     * RECARGOS:
     * - 1 cuota: +5%
     * - 3 cuotas: +10%
     * - 6 cuotas: +18%
     */
    private fun updateInstallmentPrices() {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
        formatter.maximumFractionDigits = 0

        // 1 cuota: 5% recargo
        val price1 = baseFee * 1.05
        tvPrecioCuota1.text = "${formatter.format(price1)}"

        // 3 cuotas: 10% recargo total, dividido en 3
        val price3Total = baseFee * 1.10
        val price3Each = price3Total / 3
        tvPrecioCuota3.text = "3 x ${formatter.format(price3Each)}"

        // 6 cuotas: 18% recargo total, dividido en 6
        val price6Total = baseFee * 1.18
        val price6Each = price6Total / 6
        tvPrecioCuota6.text = "6 x ${formatter.format(price6Each)}"
    }

    /**
     * Actualiza el total a pagar según método y cuotas seleccionadas
     */
    private fun updateTotalDisplay() {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
        formatter.maximumFractionDigits = 0

        val total = when {
            opEfectivo.isChecked -> baseFee
            opTarjeta.isChecked && selectedInstallments == 1 -> baseFee * 1.05
            opTarjeta.isChecked && selectedInstallments == 3 -> baseFee * 1.10
            opTarjeta.isChecked && selectedInstallments == 6 -> baseFee * 1.18
            else -> baseFee
        }

        tvTotalPagar.text = formatter.format(total)
    }

    /**
     * Marca visualmente la cantidad de cuotas seleccionada
     */
    private fun selectInstallment(installments: Int) {
        // Resetear todos
        resetInstallmentSelection()

        // Marcar el seleccionado
        val selectedLayout = when (installments) {
            1 -> llCuota1
            3 -> llCuota3
            6 -> llCuota6
            else -> llCuota1
        }

        // Cambiar fondo y color de texto
        selectedLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.violeta_claro))
        selectedLayout.setPadding(16, 16, 16, 16)

        // Cambiar color de ambos TextViews dentro del LinearLayout
        for (i in 0 until selectedLayout.childCount) {
            val child = selectedLayout.getChildAt(i)
            if (child is TextView) {
                child.setTextColor(ContextCompat.getColor(this, R.color.violeta))
            }
        }
    }

    /**
     * Resetea la selección visual de cuotas
     */
    private fun resetInstallmentSelection() {
        val defaultBg = ContextCompat.getColor(this, android.R.color.transparent)
        val defaultText = ContextCompat.getColor(this, android.R.color.black)

        // Resetear los 3 LinearLayouts
        listOf(llCuota1, llCuota3, llCuota6).forEach { layout ->
            layout.setBackgroundColor(defaultBg)
            layout.setPadding(0, 0, 0, 0)

            // Resetear color de los TextViews
            for (i in 0 until layout.childCount) {
                val child = layout.getChildAt(i)
                if (child is TextView) {
                    child.setTextColor(defaultText)
                }
            }
        }
    }

    /**
     * Procesa el pago según el método seleccionado
     */
    private fun procesarPago() {
        // Validar que se haya seleccionado un método de pago
        if (!opEfectivo.isChecked && !opTarjeta.isChecked) {
            Toast.makeText(this, "Por favor seleccione un método de pago", Toast.LENGTH_LONG).show()
            return
        }

        // Determinar método de pago
        val paymentMethod = when {
            opEfectivo.isChecked -> "EFECTIVO"
            opTarjeta.isChecked -> "TARJETA"
            else -> "EFECTIVO"
        }

        val installments = if (opTarjeta.isChecked) selectedInstallments else 1

        // Calcular monto final con recargos
        val finalAmount = when {
            opEfectivo.isChecked -> baseFee
            opTarjeta.isChecked && installments == 1 -> baseFee * 1.05
            opTarjeta.isChecked && installments == 3 -> baseFee * 1.10
            opTarjeta.isChecked && installments == 6 -> baseFee * 1.18
            else -> baseFee
        }

        // TODO: Aquí iría la lógica para:
        // 1. Insertar el pago en la tabla 'payments'
        // 2. Actualizar el estado de la membresía
        // 3. Crear la siguiente cuota si corresponde

        // Por ahora, navegar a pantalla de éxito
        val intent = Intent(this, PagoSocioExitoActivity::class.java)
        intent.putExtra("PAYMENT_AMOUNT", finalAmount)
        intent.putExtra("PAYMENT_METHOD", paymentMethod)
        intent.putExtra("INSTALLMENTS", installments)
        startActivity(intent)
    }
}