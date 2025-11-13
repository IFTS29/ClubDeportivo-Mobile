package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // se captura el usuario, contraseña y botón de login
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)


        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val pass = etPassword.text.toString()

            if(usuario.isEmpty() || pass.isEmpty()){
                Toast.makeText(this, "Complete todos los campos.", Toast.LENGTH_LONG).show()
            } else if(usuario == "admin" && pass == "1234"){
                val intent = Intent(this, MenuPrincipalActivity::class.java)

                // Guarda el valor de la variable usuario (2° parametro) en el Intent,
                // usando la clave "usuario" (1° parametro)
                intent.putExtra("usuario", usuario)
                startActivity(intent)
            } else{
                Toast.makeText(this, "Usuario o contraseña incorrectas", Toast.LENGTH_LONG).show()
            }

        }
    }
}