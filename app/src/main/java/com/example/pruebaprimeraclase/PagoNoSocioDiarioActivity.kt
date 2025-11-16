package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PagoNoSocioDiarioActivity : AppCompatActivity(), ActivityAdapter.OnActivitySelectionListener {

    private lateinit var dbHelper: DBHelper

    // --- Referencias a Vistas ---
    private lateinit var tvClientName: TextView
    private lateinit var tvClientId: TextView
    private lateinit var tvCurrentDate: TextView
    private lateinit var rvActivities: RecyclerView
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnRegister: Button

    // --- Variables de estado ---
    private lateinit var activityAdapter: ActivityAdapter
    private var currentClient: ClientData? = null
    private var currentClientDoc: String? = null // Para pasar a la sig. actividad
    private var currentTotal: Double = 0.0
    private var currentSelectedIds = listOf<Int>()
    private var allActivities: List<ActivityData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_nosocio_diario)

        // 1. Inicializar DBHelper
        dbHelper = DBHelper(this)

        // 2. Referencias a los Views
        // --- ESTO ES LO QUE FALTABA EN MI CÓDIGO ANTERIOR ---
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        // --- FIN DE LA CORRECCIÓN ---

        btnRegister = findViewById(R.id.btnRegister)
        tvClientName = findViewById(R.id.tvClientName)
        tvClientId = findViewById(R.id.tvClientId)
        tvCurrentDate = findViewById(R.id.tvCurrentDate)
        rvActivities = findViewById(R.id.rvActivities)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)

        // 3. Cargar datos del cliente
        loadClientData()

        // 4. Cargar fecha actual
        loadCurrentDate()

        // 5. Configurar RecyclerView
        setupRecyclerView() // Ahora depende de que loadClientData() termine primero

        // Estado inicial del botón deshabilitado
        btnRegister.isEnabled = false
        btnRegister.alpha = 0.5f

        // --- Lógica de botones ---
        btnBack.setOnClickListener {
            finish()
        }

        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            if (currentTotal <= 0 || currentSelectedIds.isEmpty() || currentClient == null) {
                Toast.makeText(this, "Debe seleccionar al menos una actividad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Construir la lista de nombres de actividades seleccionadas
            val selectedActivityNames = allActivities
                .filter { currentSelectedIds.contains(it.activityId) }
                .map { "- ${it.activityName}" }
                .joinToString("\n")

            val totalText = String.format(Locale.US, "%.2f", currentTotal)
            val dialogMessage = "Confirmar inscripción para:\n\n$selectedActivityNames\n\nTotal a pagar: $ $totalText"

            AlertDialog.Builder(this)
                .setTitle("Confirmar Inscripción")
                .setMessage(dialogMessage)
                .setPositiveButton("Sí, registrar") { _, _ ->
                    executeRegistration()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun executeRegistration() {
        // Llama a la función renombrada
        val newPaymentId = dbHelper.registerPendingActivities(
            clientId = currentClient!!.clientId,
            totalAmount = currentTotal,
            selectedActivityIds = currentSelectedIds
        )

        if (newPaymentId != -1L) {
            val intent = Intent(this, PagoNoSocioMetodosActivity::class.java)

            intent.putExtra("PAYMENT_ID", newPaymentId)
            intent.putExtra("TOTAL_AMOUNT", currentTotal)

            // --- LÓGICA AGREGADA ---
            // Pasamos el documento del cliente
            intent.putExtra("CLIENT_DOC", currentClientDoc)
            // ------------------------

            startActivity(intent)
        } else {
            Toast.makeText(this, "Error al registrar la inscripción. Intente de nuevo.", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadClientData() {
        val clientDoc = intent.getStringExtra("CLIENT_DOC")

        // --- LÓGICA AGREGADA ---
        // Guardamos el documento para usarlo después
        this.currentClientDoc = clientDoc
        // ------------------------

        currentClient = clientDoc?.let { dbHelper.getClientByDoc(it) }

        if (currentClient != null) {
            tvClientName.text = "${currentClient!!.firstName.uppercase()} ${currentClient!!.lastName.uppercase()}"
            tvClientId.text = "ID NO SOCIO: ${currentClient!!.clientId}"
        } else {
            tvClientName.text = "CLIENTE NO ENCONTRADO"
            tvClientId.text = "ID NO SOCIO: N/A"
        }
    }

    private fun loadCurrentDate() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        tvCurrentDate.text = "FECHA: $currentDate"
    }

    private fun setupRecyclerView() {
        allActivities = dbHelper.getAllActivities()

        // --- LÓGICA AGREGADA ---
        // Obtenemos los IDs de las actividades en las que ya está inscripto HOY
        // Usamos 'currentClient' que ya fue cargado en loadClientData()
        val todayRegisteredIds = currentClient?.clientId?.let {
            dbHelper.getTodayRegisteredActivityIds(it)
        }?.toSet() ?: emptySet() // Usamos un Set vacío si el cliente es nulo

        // Pasamos la nueva lista de IDs al constructor del Adapter
        activityAdapter = ActivityAdapter(allActivities, todayRegisteredIds, this)
        // ------------------------

        rvActivities.layoutManager = LinearLayoutManager(this)
        rvActivities.adapter = activityAdapter
    }

    // Esta función es OBLIGATORIA
    override fun onSelectionChanged(selectedTotal: Double, selectedIds: List<Int>) {
        currentTotal = selectedTotal
        currentSelectedIds = selectedIds

        val totalText = String.format(Locale.US, "TOTAL A PAGAR: $ %.2f", selectedTotal)
        tvTotalAmount.text = totalText

        // Lógica para habilitar/deshabilitar el botón
        if (currentTotal > 0) {
            btnRegister.isEnabled = true
            btnRegister.alpha = 1.0f
        } else {
            btnRegister.isEnabled = false
            btnRegister.alpha = 0.5f
        }
    }
}