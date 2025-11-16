package com.example.pruebaprimeraclase

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView

class RegistrarClientes2Activity : AppCompatActivity() {
    // (Esto es necesario para que las funciones de validación puedan acceder a ellas)
    private lateinit var etDni: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etEmail: EditText
    private lateinit var etDireccion: EditText

    private lateinit var etFechaNacimiento: EditText
    private lateinit var spinnerTipoCliente: Spinner
    private lateinit var cbAptoMedico: CheckBox
    private lateinit var btnContinuar: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_clientes2)

        // Obtener el DNI pasado desde la pantalla anterior
        val dni = intent.getStringExtra("dni")

        // Referencias a los elementos del layout
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        btnContinuar = findViewById<Button>(R.id.btnContinuar)
        spinnerTipoCliente = findViewById<Spinner>(R.id.spinnerTipoCliente)
        etDni = findViewById<EditText>(R.id.etDni)
        etNombre = findViewById<EditText>(R.id.etNombre)
        etApellido = findViewById<EditText>(R.id.etApellido)
        etTelefono = findViewById<EditText>(R.id.etTelefono)
        etEmail = findViewById<EditText>(R.id.etEmail)
        etDireccion = findViewById<EditText>(R.id.etDireccion)
        cbAptoMedico = findViewById<CheckBox>(R.id.cbAptoMedico)
        etFechaNacimiento = findViewById<EditText>(R.id.etFechaNacimiento)

        // Configurar el Spinner con las opciones
        val tiposCliente = arrayOf("Seleccionar tipo Cliente", "Socio", "No Socio")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposCliente)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoCliente.adapter = adapter

        // Funcionalidad del botón Atrás
        btnBack.setOnClickListener {
            finish() // Volver a la pantalla anterior
        }

        // Funcionalidad del botón Menú
        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Rellenar el DNI y desabilitarlo
        if (dni != null) {
            etDni.setText(dni)
            etDni.isEnabled = false
        }

        btnContinuar.isEnabled = false

        //asignar escuchador a los editText
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No es necesario
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No es necesario
            }

            override fun afterTextChanged(s: Editable?) {
                // Cada vez que se termina de escribir, validar el formulario
                validarFormulario()
            }
        }
        // asignar al spinner
        etNombre.addTextChangedListener(textWatcher)
        etApellido.addTextChangedListener(textWatcher)
        etTelefono.addTextChangedListener(textWatcher)
        etEmail.addTextChangedListener(textWatcher)
        etDireccion.addTextChangedListener(textWatcher)

        //navegación bottom
        btnBack.setOnClickListener {
            finish()
        }

        btnMenu.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }

        spinnerTipoCliente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                validarFormulario() // Validar cuando cambia la selección
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                validarFormulario() // Validar si no se selecciona nada
            }
        }

        btnContinuar.setOnClickListener {

            // Recolectar datos de TODOS los campos
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()
            val aptoMedico = cbAptoMedico.isChecked
            val dni = etDni.text.toString().trim()
            val tipoClienteSeleccionado = spinnerTipoCliente.selectedItem.toString()
            val fechaNac = etFechaNacimiento.text.toString().trim()

            // Validaciones
            if (tipoClienteSeleccionado == "Seleccionar tipo Cliente") {
                Toast.makeText(this, "Por favor seleccione un tipo de cliente", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            /*if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || email.isEmpty() || direccion.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }*/
            // Instanciar tu DBHelper
            val dbHelper = DBHelper(this)

            // 4. Intentar guardar el cliente en la BD
            val success = dbHelper.addClient(
                firstName = nombre,
                lastName = apellido,
                docNumber = dni,
                birthDate = fechaNac,
                address = direccion,
                email = email,
                phoneNumber = telefono,
                medicalCertificate = aptoMedico,
                clientType = tipoClienteSeleccionado.uppercase() // "Socio" -> "SOCIO"
            )

            if (success) {
                // Si se guardó con éxito, NAVEGAR a la pantalla 3
                Toast.makeText(this, "Cliente registrado con éxito", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, RegistrarClientes3Activity::class.java)

                // Pasa los datos a la Activity 3 para que los muestre
                intent.putExtra("dni", dni)
                intent.putExtra("nombre", nombre)
                intent.putExtra("apellido", apellido)
                intent.putExtra("tipo_cliente", tipoClienteSeleccionado)
                // Agrega cualquier otro dato que Activity 3 necesite mostrar

                startActivity(intent)

            } else {


                if (tipoClienteSeleccionado == "Seleccionar tipo") {
                    Toast.makeText(
                        this,
                        "Por favor seleccione un tipo de cliente",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // Navegar a la pantalla de confirmación (Registro 3)
                val intent = Intent(this, RegistrarClientes3Activity::class.java)
                intent.putExtra("dni", dni)
                intent.putExtra("tipo_cliente", tipoClienteSeleccionado)
                startActivity(intent)


                // Pasar TODOS los datos recolectados a la siguiente actividad
                intent.putExtra("dni", dni) // El DNI original
                intent.putExtra("nombre", nombre)
                intent.putExtra("apellido", apellido)
                intent.putExtra("telefono", telefono)
                intent.putExtra("email", email)
                intent.putExtra("direccion", direccion)
                intent.putExtra("aptoMedico", aptoMedico) // Pasa el Boolean
                intent.putExtra("tipo_cliente", tipoClienteSeleccionado)

                // NOTA: Aquí también deberías pasar la FECHA DE NACIMIENTO (ver abajo)

                startActivity(intent)
            }

        }
    }
    private fun validarFormulario() {
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        // val fechaNac = etFechaNacimiento.text.toString().trim() // (Agregar)

        // Comprobar que ningún campo de texto esté vacío
        val camposTextoCompletos = nombre.isNotEmpty() &&
                apellido.isNotEmpty() &&
                telefono.isNotEmpty() &&
                email.isNotEmpty() &&
                direccion.isNotEmpty()
        // && fechaNac.isNotEmpty()

        // Comprobar que se haya seleccionado un tipo válido (posición 0 es "Seleccionar...")
        val tipoClienteValido = spinnerTipoCliente.selectedItemPosition > 0

        // El botón solo se activa si AMBAS condiciones son verdaderas
        btnContinuar.isEnabled = camposTextoCompletos && tipoClienteValido
    }
}
