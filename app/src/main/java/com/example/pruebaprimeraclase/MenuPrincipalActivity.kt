package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar


class MenuPrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        // capturo el msj de bienvenida, creo la variable usuario
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)

        // Recupera el nombre de usuario de la pantalla anterior.
        // Si no se envió ninguno, usa "Usuario" como valor predeterminado.
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        tvBienvenida.text = getString(R.string.mensaje_bienvenida, usuario)

        // Aparece mensaje Sesión Iniciada en parte inferior de la página
        // (revisa si la bandera que enviamos desde MainActivity es 'true')
        val showSnackbar = intent.getBooleanExtra("SHOW_WELCOME_SNACKBAR", false)
        // (solo muestra el Snackbar si la bandera es 'true')
        if (showSnackbar) {
            Snackbar.make(findViewById(android.R.id.content), "Sesión iniciada...", Snackbar.LENGTH_LONG)
                .show()
        }

        val btnClose = findViewById<ImageButton>(R.id.btnClose)
        btnClose.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Desea cerrar la sesión?")
                .setPositiveButton("Sí") {_,_ ->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        }

        val btnRegistroClientes = findViewById<LinearLayout>(R.id.btnRegistroClientes)
        btnRegistroClientes.setOnClickListener {
            val intent = Intent(this, RegistrarClientesActivity::class.java)
            startActivity(intent)
        }

        val btnRegistroPagos = findViewById<LinearLayout>(R.id.btnRegistroPagos)
        btnRegistroPagos.setOnClickListener {
            val intent = Intent(this, RegistrarPagosActivity::class.java)
            startActivity(intent)
        }

        val btnListadoVencimientos = findViewById<LinearLayout>(R.id.btnListadoVencimientos)
        btnListadoVencimientos.setOnClickListener {
            val intent = Intent(this, ListarVencimientosDiaActivity::class.java)
            startActivity(intent)
        }

    }
}