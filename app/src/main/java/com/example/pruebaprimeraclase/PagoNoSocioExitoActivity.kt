package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PagoNoSocioExitoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_nosocio_exito)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, PagoNoSocioMetodosActivity::class.java)
            startActivity(intent)
        }

        val btnComprobante = findViewById<Button>(R.id.btnComprobante)
        btnComprobante.setOnClickListener {
            val intent = Intent(this, PagoNoSocioComprobanteActivity::class.java)
            startActivity(intent)
        }

    }
}