package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PagoNoSocioDiarioActivity : AppCompatActivity(), ActivityAdapter.OnActivitySelectionListener {
    private lateinit var dbHelper: DBHelper

    // Referencias a los elementos del layout
    private lateinit var tvClientName: TextView
    private lateinit var tvClientId: TextView
    private lateinit var tvCurrentDate: TextView
    private lateinit var rvActivities: RecyclerView
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnContinue: Button // <-- CAMBIO DE NOMBRE

    // Variables de estado
    private lateinit var activityAdapter: ActivityAdapter
    private var currentClient: ClientData? = null
    private var currentClientDoc: String? = null
    private var currentTotal: Double = 0.0
    private var currentSelectedIds = listOf<Int>()
    private var allActivities: List<ActivityData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_nosocio_diario)

        dbHelper = DBHelper(this)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        btnContinue = findViewById(R.id.btnContinue) // <-- CAMBIO DE ID
        tvClientName = findViewById(R.id.tvClientName)
        tvClientId = findViewById(R.id.tvClientId)
        tvCurrentDate = findViewById(R.id.tvCurrentDate)
        rvActivities = findViewById(R.id.rvActivities)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)

        // Se cargan los datos (No Socio y fecha actual)
        loadClientData()
        loadCurrentDate()

        // Estado inicial del botón deshabilitado
        btnContinue.isEnabled = false
        btnContinue.alpha = 0.5f

        //  Botones: Volver, Menú
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

        // --- BOTÓN CONTINUAR (Lógica simplificada) ---
        btnContinue.setOnClickListener {
            // El chequeo de currentTotal > 0 no es necesario
            // porque el botón ya está deshabilitado si es 0.

            // Chequeo de seguridad simple
            if (currentClient == null) {
                Toast.makeText(this, "Error: No se pudo cargar el cliente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Esta pantalla YA NO registra nada.
            // Solo junta la información y la pasa a la siguiente.

            val intent = Intent(this, PagoNoSocioMetodosActivity::class.java)

            // 1. Pasa el Total
            intent.putExtra("TOTAL_AMOUNT", currentTotal)
            // 2. Pasa el Documento del Cliente
            intent.putExtra("CLIENT_DOC", currentClientDoc)
            // 3. Pasa la lista de IDs de actividades seleccionadas
            intent.putIntegerArrayListExtra("SELECTED_ACTIVITY_IDS", ArrayList(currentSelectedIds))

            startActivity(intent)
        }
    }

    // Se ejecuta cada vez que la pantalla vuelve a estar visible
    // (por si el usuario vuelve apra atrás desde pantalla de metodos pago)
    override fun onResume() {
        super.onResume()
        // Se refresca la lista de actividades por si algo cambió
        setupRecyclerView()

        // Se resetea el total (se deshabilita el botón)
        onSelectionChanged(0.0, emptyList())
    }


    private fun loadClientData() {
        // Obtenemos el CLIENT_DOC solo si es la primera vez (no está seteado)
        if (currentClientDoc == null) {
            currentClientDoc = intent.getStringExtra("CLIENT_DOC")
        }

        currentClient = currentClientDoc?.let { dbHelper.getClientByDoc(it) }

        if (currentClient != null) {
            tvClientName.text = "${currentClient!!.firstName.uppercase()} ${currentClient!!.lastName.uppercase()}"
            tvClientId.text = "ID NO SOCIO: ${currentClient!!.clientId}"
        } else {
            tvClientName.text = "CLIENTE NO ENCONTRADO"
            tvClientId.text = "ID NO SOCIO: N/A"
        }
    }

    private fun loadCurrentDate() {
        val formateadorFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = formateadorFecha.format(Date())
        tvCurrentDate.text = "FECHA: $fechaActual"
    }

    private fun setupRecyclerView() {
        allActivities = dbHelper.getAllActivities()

        val todayRegisteredIds = if (currentClient != null) {
            dbHelper.getTodayRegisteredActivityIds(currentClient!!.clientId).toSet()
        } else {
            emptySet()
        }

        activityAdapter = ActivityAdapter(allActivities, todayRegisteredIds, this)

        rvActivities.layoutManager = LinearLayoutManager(this)
        rvActivities.adapter = activityAdapter
    }

    // Manejar cambios del estado del botón y monto total
    override fun onSelectionChanged(selectedTotal: Double, selectedIds: List<Int>) {
        currentTotal = selectedTotal
        currentSelectedIds = selectedIds

        val totalText = String.format(Locale.US, "TOTAL A PAGAR: $ %.2f", selectedTotal)
        tvTotalAmount.text = totalText

        // Lógica para habilitar/deshabilitar el botón
        if (currentTotal > 0) {
            btnContinue.isEnabled = true
            btnContinue.alpha = 1.0f
        } else {
            btnContinue.isEnabled = false
            btnContinue.alpha = 0.5f
        }
    }
}