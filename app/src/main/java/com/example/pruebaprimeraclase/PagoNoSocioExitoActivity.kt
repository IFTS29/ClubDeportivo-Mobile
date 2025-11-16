package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PagoNoSocioExitoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    // Datos que se reciben y luego se pasan a siguiente pantalla
    private var paymentId: Long = -1L
    private var clientDoc: String? = null

    // Referencias de los elementos del Layout
    private lateinit var tvReceiptDate: TextView
    private lateinit var llActivityList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_nosocio_exito)

        dbHelper = DBHelper(this)

        // Se capturan los datos recibidos por pantalla de pago
        paymentId = intent.getLongExtra("PAYMENT_ID", -1L)
        clientDoc = intent.getStringExtra("CLIENT_DOC")

        // Se inicializan las refencias a elementos de la UI
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val btnComprobante = findViewById<Button>(R.id.btnReceipt)
        tvReceiptDate = findViewById(R.id.tvReceiptDate)
        llActivityList = findViewById(R.id.llActivityList)

        // NAVEGACIÓN
        btnBack.setOnClickListener {
            val intent = Intent(this, RegistrarPagosActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        // Botón COMPROBANTE
        btnComprobante.setOnClickListener {
            val intent = Intent(this, PagoNoSocioComprobanteActivity::class.java)

            // se pasan los datos recibidos a la pantalla de Comprobante
            intent.putExtra("PAYMENT_ID", paymentId)
            intent.putExtra("CLIENT_DOC", clientDoc)

            startActivity(intent)
        }

        // se cargan los datos
        loadCurrentDate()
        loadActivities()
    }


    // Se carga la fecha actual
    private fun loadCurrentDate() {
        val formateadorFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        tvReceiptDate.text = formateadorFecha.format(Date())
    }

    // Se carga la lista de acitvidades pagadas
    private fun loadActivities() {
        // Se obtiene la lista de actividades pagadas
        //(usando la función del DBHelper)
        val activities = dbHelper.getActivitiesForPayment(paymentId)

        llActivityList.removeAllViews()

        if (activities.isEmpty()) {
            addActivityText("No se encontraron actividades")
        } else {
            // Se crea un TextView por cada actividad
            for (activity in activities) {
                // Formato: BODY PUMP (18:00)
                val text = "${activity.activityName.uppercase()} (${activity.activityTime})"
                addActivityText(text)
            }
        }
    }

    //Función auxiliar para mostrar la lista de actividades.
    private fun addActivityText(text: String) {
        val textView = TextView(this).apply {
            this.text = text
            textSize = 14f
            setTextColor(ContextCompat.getColor(context, android.R.color.black))
            gravity = Gravity.CENTER

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = (8 * resources.displayMetrics.density).toInt() // 8dp
            layoutParams = params
        }
        llActivityList.addView(textView)
    }
}