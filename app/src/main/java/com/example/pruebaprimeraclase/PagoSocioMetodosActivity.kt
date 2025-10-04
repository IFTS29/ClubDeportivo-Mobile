package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PagoSocioMetodosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_socio_metodos)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, PagoSocioCuotaMensualActivity::class.java)
            startActivity(intent)
        }

        val btnPagar = findViewById<Button>(R.id.btnPagar)
        btnPagar.setOnClickListener {
            val intent = Intent(this, PagoSocioExitoActivity::class.java)
            startActivity(intent)
        }

    }
}