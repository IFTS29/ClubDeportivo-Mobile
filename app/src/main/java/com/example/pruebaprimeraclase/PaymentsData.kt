package com.example.pruebaprimeraclase

data class PaymentData(
    val paymentId: Int,
    val clientId: Int,
    val membershipId: Int?, // Puede ser NULL si paymentType es 'DIARIA'
    val amount: Double,
    val paymentType: String, // 'MENSUAL' o 'DIARIA'
    val paymentMethod: String?, // 'EFECTIVO' o 'TARJETA'. Puede ser null si el estado es PENDIENTE.
    val installments: Int, // 1, 3, o 6
    val paymentDate: String?, // Formato "YYYY-MM-DD" (puede ser null si es PENDIENTE)
    val dueDate: String, // Formato "YYYY-MM-DD"
    val paymentStatus: String // 'PAGADO', 'PENDIENTE', 'CANCELADO'
)

