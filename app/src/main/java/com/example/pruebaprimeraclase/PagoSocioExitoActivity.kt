package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PagoSocioExitoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_socio_exito)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, PagoSocioMetodosActivity::class.java)
            startActivity(intent)
        }

        val btnCarnet = findViewById<Button>(R.id.btnCarnet)
        btnCarnet.setOnClickListener {
            val intent = Intent(this, PagoSocioCarnetActivity::class.java)
            startActivity(intent)
        }

        val btnComprobante = findViewById<Button>(R.id.btnComprobante)
        btnComprobante.setOnClickListener {
            val intent = Intent(this, PagoSocioComprobanteActivity::class.java)
            startActivity(intent)
        }

    }


}