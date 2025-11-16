package com.example.pruebaprimeraclase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


// Modelo de datos para UNA sola membresía (tabla 'memberships').
// Contiene lógica para formatear sus propios datos.
// @Parcelize permite pasar este objeto entre Activities

@Parcelize
data class MembershipData(
    val membershipId: Int,
    val clientId: Int,
    val startDate: String, // Formato "YYYY-MM-DD"
    val expiryDate: String, // Formato "YYYY-MM-DD"
    val monthlyFee: Double,
    val status: String,
    val paymentId: Int? = null // <--- ¡CAMPO AGREGADO!
) : Parcelable {

    // Función interna para formatear las fechas a dd/MM/yyyy.
    private fun formatDate(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatter.format(parser.parse(dateString)!!)
        } catch (e: Exception) {
            dateString // Devuelve el original si falla
        }
    }


    // Devuelve el período formateado, ej: "01/11/2024 - 30/11/2024"
    val formattedPeriod: String
        get() = "${formatDate(startDate)} - ${formatDate(expiryDate)}"


    // Propiedad calculada: Calcula los días restantes (positivos o negativos) hasta el vencimiento.
    private val daysRemaining: Long
        get() {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val expiry = parser.parse(expiryDate)!!

                // Compara la fecha de vencimiento con el INICIO del día de hoy
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = sdf.parse(sdf.format(Date()))!!

                val diffInMillis = expiry.time - today.time
                TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
            } catch (e: Exception) {
                0L
            }
        }

    // Para generar el texto de vencimiento (ej: "Vence en 5 días" o "Venció hace 3 días")
    val daysRemainingText: String
        get() {
            val days = daysRemaining
            return when {
                days < -1 -> "Venció hace ${-days} días"
                days == -1L -> "Venció ayer"
                days == 0L -> "Vence hoy"
                days == 1L -> "Vence mañana"
                days > 1 -> "Su cuota actual vence en $days días"
                else -> "Vence hoy" // Caso por defecto
            }
        }
}

// Contenedor para transportar la membresía actual y la próxima.

data class MembershipList(
    val currentMembership: MembershipData?,
    val nextMembership: MembershipData?
)