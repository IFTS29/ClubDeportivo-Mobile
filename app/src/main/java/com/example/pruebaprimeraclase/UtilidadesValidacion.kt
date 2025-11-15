package com.example.pruebaprimeraclase
/**
 'object' es una clase de utilidad (Singleton), que no necesita ser instanciada para usarse.
 Usamos esta clase para centralizar todas las validaciones de UI de la app.
 */

object UtilidadesValidacion {

    fun validarDocumento(doc: String): String? {
        if (doc.isEmpty()) {
            return "Por favor ingrese un DNI"
        }
        if (doc.length < 7 || doc.length > 9) {
            return "El DNI debe tener entre 7 y 9 dígitos"
        }

        // Si pasa todas las validaciones, no hay error.
        return null
    }

    /*
    // Acá podemos agregar más validaciones a futuro:

    fun validarEmail(email: String): String? {
        if (email.isEmpty()) {
            return "El email no puede estar vacío"
        }
        // ... etc ...
        return null
    }
    */
}