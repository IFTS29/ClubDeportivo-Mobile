package com.example.pruebaprimeraclase

data class ClientData(
    // Datos de 'persons'
    val personId: Int, // ID de la tabla persons
    val firstName: String,
    val lastName: String,
    val docNumber: String,
    val birthDate: String,
    val address: String,
    val email: String,
    val phoneNumber: String,
    val medicalCertificate: Boolean, // convertido de Int


    // Datos de 'clients'
    val clientId: Int, // ID de la tabla clients
    val clientType: String,
    val clientStatus: String?, // puede ser null para "NO SOCIO"
    val registrationDate: String,
    val cardDelivered: Boolean // convertido de Int
)