package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.DatePickerDialog
import java.util.Calendar

class ListarVencimientosDiaActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var rvVencimientos: RecyclerView
    private lateinit var adapter: DatoAdapter
    private var vencimientos: List<MembershipData> = emptyList()
    private var clients: List<ClientData> = emptyList()
    private var startDate: String = ""
    private var endDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listar_vencimientos_dia)

        dbHelper = DBHelper(this)
        rvVencimientos = findViewById(R.id.rvVencimientos)
        rvVencimientos.layoutManager = LinearLayoutManager(this)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val btnPeriodoVencimiento = findViewById<Button>(R.id.btnPeriodoVencimiento)
        val tvFechaSeleccionada = findViewById<android.widget.TextView>(R.id.tvFechaSeleccionada)

        btnBack.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnPeriodoVencimiento.setOnClickListener {
            showDateRangePicker { start, end ->
                startDate = start
                endDate = end
                updateFechaSeleccionada(tvFechaSeleccionada)
                loadVencimientos()
            }
        }

        loadClients()
        setDefaultDates()
        updateFechaSeleccionada(tvFechaSeleccionada)
        loadVencimientos()
    }

    private fun setDefaultDates() {
        val cal = Calendar.getInstance()
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        startDate = sdf.format(cal.time)
        endDate = sdf.format(cal.time)
    }

    private fun updateFechaSeleccionada(tv: android.widget.TextView) {
        val sdfIn = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val sdfOut = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault())
        val fechaInicio = try { sdfOut.format(sdfIn.parse(startDate)!!) } catch (e: Exception) { startDate }
        val fechaFin = try { sdfOut.format(sdfIn.parse(endDate)!!) } catch (e: Exception) { endDate }
        if (startDate == endDate) {
            tv.text = "Fecha: Día de hoy"
        } else {
            tv.text = "Fecha: $fechaInicio / $fechaFin"
        }
    }

    private fun loadClients() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM clients INNER JOIN persons ON clients.personId = persons.personId", null)
        val list = mutableListOf<ClientData>()
        while (cursor.moveToNext()) {
            list.add(ClientData(
                personId = cursor.getInt(cursor.getColumnIndexOrThrow("personId")),
                firstName = cursor.getString(cursor.getColumnIndexOrThrow("firstName")),
                lastName = cursor.getString(cursor.getColumnIndexOrThrow("lastName")),
                docNumber = cursor.getString(cursor.getColumnIndexOrThrow("docNumber")),
                birthDate = cursor.getString(cursor.getColumnIndexOrThrow("birthDate")),
                address = cursor.getString(cursor.getColumnIndexOrThrow("address")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phoneNumber")),
                medicalCertificate = cursor.getInt(cursor.getColumnIndexOrThrow("medicalCertificate")) == 1,
                clientId = cursor.getInt(cursor.getColumnIndexOrThrow("clientId")),
                clientType = cursor.getString(cursor.getColumnIndexOrThrow("clientType")),
                clientStatus = cursor.getString(cursor.getColumnIndexOrThrow("clientStatus")),
                registrationDate = cursor.getString(cursor.getColumnIndexOrThrow("registrationDate")),
                cardDelivered = cursor.getInt(cursor.getColumnIndexOrThrow("cardDelivered")) == 1
            ))
        }
        cursor.close()
        clients = list
    }

    private fun loadVencimientos() {
        vencimientos = dbHelper.getMembershipsByExpiryPeriod(startDate, endDate)
        adapter = DatoAdapter(vencimientos, ::getClientById) { vencimiento, client ->
            val intent = Intent(this, VencimientoDetalleActivity::class.java)
            intent.putExtra("socio_id", client?.docNumber ?: "")
            intent.putExtra("apellido_nombre", "${client?.lastName ?: ""} ${client?.firstName ?: ""}")
            intent.putExtra("documento", client?.docNumber ?: "")
            intent.putExtra("fecha_inscripcion", client?.registrationDate ?: "")
            intent.putExtra("periodo", vencimiento.formattedPeriod)
            intent.putExtra("importe", vencimiento.monthlyFee.toString())
            intent.putExtra("estado", vencimiento.status)
            startActivity(intent)
        }
        rvVencimientos.adapter = adapter
    }

    private fun getClientById(clientId: Int): ClientData? {
        return clients.find { it.clientId == clientId }
    }

    private fun showDateRangePicker(onDatesSelected: (String, String) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val start = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            DatePickerDialog(this, { _, year2, month2, dayOfMonth2 ->
                val end = String.format("%04d-%02d-%02d", year2, month2 + 1, dayOfMonth2)
                onDatesSelected(start, end)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }
}