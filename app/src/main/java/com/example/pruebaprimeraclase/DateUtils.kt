package com.example.pruebaprimeraclase

import java.text.SimpleDateFormat
import java.util.*

/**
 * Clase Singleton para funciones de utilidad relacionadas con fechas.
 * No requiere instancia, se llama como DateUtils.getTodayDateString()
 */

object DateUtils {
    /**
     * Obtiene la fecha actual en formato "yyyy-MM-dd".
     */
    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

        /**
     * Función auxiliar para sumar días a una fecha en formato "yyyy-MM-dd"
     */
    fun addDaysToDate(dateString: String, days: Int): String {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val date = sdf.parse(dateString)!!
            val calendar = java.util.Calendar.getInstance()
            calendar.time = date
            calendar.add(java.util.Calendar.DAY_OF_MONTH, days)
            sdf.format(calendar.time)
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Formatea fecha completa: "2025-11-15" → "15/11/2025"
     */
    fun formatFullDate(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatter.format(parser.parse(dateString)!!)
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Formatea fecha corta: "2025-11-15" → "15/11"
     */
    fun formatShortDate(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
            formatter.format(parser.parse(dateString)!!)
        } catch (e: Exception) {
            dateString
        }

    }
}