package com.example.pruebaprimeraclase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "ClubDeportivo.db", null, 1){

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Usuarios del Sistema (Admin, Empleado)
        db.execSQL(
            "CREATE TABLE users("+
                    "userId INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    "userName TEXT NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "userRole TEXT NOT NULL CHECK(userRole IN ('ADMIN', 'EMPLEADO'))," +
                    "active INTEGER NOT NULL DEFAULT 1)"
        )

        // 2. Personas (datos personales)
        db.execSQL(
                    """CREATE TABLE persons(
                    personId INTEGER PRIMARY KEY AUTOINCREMENT,  
                    firstName TEXT NOT NULL, 
                    lastName TEXT NOT NULL,
                    docNumber TEXT NOT NULL UNIQUE, 
                    birthDate TEXT NOT NULL, 
                    address TEXT NOT NULL, 
                    email TEXT NOT NULL, 
                    phoneNumber TEXT NOT NULL, 
                    medicalCertificate INTEGER NOT NULL DEFAULT 0
        )"""
        )

        // 3. Clientes registrados
        db.execSQL(
                """CREATE TABLE clients(
                    clientId INTEGER PRIMARY KEY AUTOINCREMENT,
                    personId INTEGER NOT NULL UNIQUE,
                    clientType TEXT NOT NULL CHECK(clientType IN ('SOCIO', 'NO_SOCIO')),
                    clientStatus TEXT CHECK(clientStatus IN ('ACTIVO', 'INACTIVO')), --null para no socio
                    registrationDate TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    cardDelivered INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY(personId) REFERENCES persons(personId) ON DELETE CASCADE
        )"""
        )

        // 4. Membresías de Socio (servicios y accesos)
        db.execSQL(
            """CREATE TABLE memberships(
            membershipId INTEGER PRIMARY KEY AUTOINCREMENT,
            clientId INTEGER NOT NULL,
            startDate TEXT,   --null para 1° cuota ( se setea al pagar)
            expiryDate TEXT , --null (se setea al pagar)
            monthlyFee REAL NOT NULL,
            status TEXT NOT NULL DEFAULT 'PENDIENTE' CHECK(status IN ('PENDIENTE', 'ACTIVA', 'FINALIZADA', 'VENCIDA', 'CANCELADA')),
            FOREIGN KEY(clientId) REFERENCES clients(clientId) ON DELETE CASCADE
        )"""
        )

        // 5. Actividades
        db.execSQL(
            """CREATE TABLE activities(
            activityId INTEGER PRIMARY KEY AUTOINCREMENT,
            activityName TEXT NOT NULL,
            activityTime TEXT NOT NULL,
            cost REAL NOT NULL
        )"""
        )

        // 6. Inscripciones a Actividades para No Socio
        db.execSQL(
            """CREATE TABLE activityRegistrations(
            activityRegistrationId INTEGER PRIMARY KEY AUTOINCREMENT,
            clientId INTEGER NOT NULL,
            activityId INTEGER NOT NULL,
            accessDate TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, -- fecha inscripcion
            FOREIGN KEY(clientId) REFERENCES clients(clientId) ON DELETE CASCADE,
            FOREIGN KEY(activityId) REFERENCES activities(activityId) ON DELETE CASCADE
        )"""
        )

        // 7. Pagos - Facturas
        db.execSQL(
            """CREATE TABLE payments(
            paymentId INTEGER PRIMARY KEY AUTOINCREMENT,
            clientId INTEGER NOT NULL,
            membershipId INTEGER,
            amount REAL NOT NULL,
            paymentType TEXT NOT NULL CHECK(paymentType IN ('MENSUAL', 'DIARIA')),
            paymentMethod TEXT CHECK(paymentMethod IN ('EFECTIVO', 'TARJETA')),
            installments INTEGER DEFAULT 1 CHECK(installments IN (1, 3, 6)),
            paymentDate TEXT,
            dueDate TEXT NOT NULL, -- Fecha límite de pago: fecha actual para no socios y 1° cuota socio
            paymentStatus TEXT NOT NULL DEFAULT 'PENDIENTE' CHECK(paymentStatus IN ('PAGADO', 'PENDIENTE', 'CANCELADO')),
            FOREIGN KEY(clientId) REFERENCES clients(clientId) ON DELETE CASCADE,
            FOREIGN KEY(membershipId) REFERENCES memberships(membershipId) ON DELETE SET NULL
        )"""
        )

        // 8. Detalle de Pago (para pagar muchas actividades juntas)
        db.execSQL(
            """CREATE TABLE paymentDetails(
            paymentDetailId INTEGER PRIMARY KEY AUTOINCREMENT,
            paymentId INTEGER NOT NULL,
            activityRegistrationId INTEGER NOT NULL,
            FOREIGN KEY(paymentId) REFERENCES payments(paymentId) ON DELETE CASCADE,
            FOREIGN KEY(activityRegistrationId) REFERENCES activityRegistrations(activityRegistrationId) ON DELETE CASCADE
        )"""
        )

    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS paymentDetails")
        db.execSQL("DROP TABLE IF EXISTS payments")
        db.execSQL("DROP TABLE IF EXISTS activityRegistrations")
        db.execSQL("DROP TABLE IF EXISTS activities")
        db.execSQL("DROP TABLE IF EXISTS memberships")
        db.execSQL("DROP TABLE IF EXISTS clients")
        db.execSQL("DROP TABLE IF EXISTS persons")
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }


}