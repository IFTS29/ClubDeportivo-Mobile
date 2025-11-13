package com.example.pruebaprimeraclase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "ClubDeportivo.db", null, 1){

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE users("+
                    "userId INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    "userName TEXT NOT NULL, " +
                    "userRole TEXT NOT NULL CHECK(userRole IN ('ADMIN', 'EMPLEADO'))," +
                    "active INTEGER NOT NULL DEFAULT 1)"
        )

        db.execSQL(
                    """CREATE TABLE persons(
                    personId INTEGER PRIMARY KEY AUTOINCREMENT,  
                    firstName TEXT NOT NULL, 
                    lastName TEXT NOT NULL, 
                    docType TEXT NOT NULL UNIQUE, 
                    docNumber TEXT NOT NULL, 
                    birthDate TEXT NOT NULL, 
                    address TEXT NOT NULL, 
                    email TEXT NOT NULL, 
                    phoneNumber TEXT NOT NULL, 
                    medicalCertificate INTEGER NOT NULL DEFAULT 0, 
                    medicalCertExpiry TEXT, 
                    createdAt TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
        )"""
        )

        // Tabla de clientes (referencia a personas)
        db.execSQL(
                """CREATE TABLE clients(
                    clientId INTEGER PRIMARY KEY AUTOINCREMENT,
                    personId INTEGER NOT NULL UNIQUE,
                    clientType TEXT NOT NULL CHECK(clientType IN ('SOCIO', 'NO_SOCIO')),
                    status TEXT NOT NULL DEFAULT 'ACTIVO' CHECK(status IN ('ACTIVO', 'INACTIVO')),
                    registrationDate TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    cardDelivered INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY(personId) REFERENCES persons(personId) ON DELETE CASCADE
        )"""
        )

        db.execSQL(
            """CREATE TABLE activities(
            activityId INTEGER PRIMARY KEY AUTOINCREMENT,
            activityName TEXT NOT NULL,
            activityTime TEXT NOT NULL,
            cost REAL NOT NULL,
            description TEXT
        )"""
        )

        // Tabla de membresías (para socios)
        db.execSQL(
            """CREATE TABLE memberships(
            membershipId INTEGER PRIMARY KEY AUTOINCREMENT,
            clientId INTEGER NOT NULL,
            startDate TEXT NOT NULL,
            expiryDate TEXT NOT NULL,
            monthlyFee REAL NOT NULL,
            status TEXT NOT NULL DEFAULT 'ACTIVA' CHECK(status IN ('ACTIVA', 'VENCIDA', 'CANCELADA')),
            FOREIGN KEY(clientId) REFERENCES clients(clientId) ON DELETE CASCADE
        )"""
        )

        // Tabla de pagos (unificada para socios y no socios)
        db.execSQL(
            """CREATE TABLE payments(
            paymentId INTEGER PRIMARY KEY AUTOINCREMENT,
            clientId INTEGER NOT NULL,
            paymentType TEXT NOT NULL CHECK(paymentType IN ('MENSUAL', 'DIARIA')),
            amount REAL NOT NULL,
            paymentMethod TEXT NOT NULL CHECK(paymentMethod IN ('EFECTIVO', 'TARJETA')),
            installments INTEGER DEFAULT 1 CHECK(installments IN (1, 3, 6)),
            paymentDate TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
            dueDate TEXT,
            membershipId INTEGER,
            activityId INTEGER,
            status TEXT NOT NULL DEFAULT 'PAGADO' CHECK(status IN ('PAGADO', 'PENDIENTE', 'VENCIDO')),
            FOREIGN KEY(clientId) REFERENCES clients(clientId) ON DELETE CASCADE,
            FOREIGN KEY(membershipId) REFERENCES memberships(membershipId) ON DELETE SET NULL,
            FOREIGN KEY(activityId) REFERENCES activities(activityId) ON DELETE SET NULL
        )"""
        )

        // Tabla de asistencias (para registrar qué actividades realiza cada cliente)
        db.execSQL(
            """CREATE TABLE attendances(
            attendanceId INTEGER PRIMARY KEY AUTOINCREMENT,
            clientId INTEGER NOT NULL,
            activityId INTEGER NOT NULL,
            attendanceDate TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
            paymentId INTEGER,
            FOREIGN KEY(clientId) REFERENCES clients(clientId) ON DELETE CASCADE,
            FOREIGN KEY(activityId) REFERENCES activities(activityId) ON DELETE CASCADE,
            FOREIGN KEY(paymentId) REFERENCES payments(paymentId) ON DELETE SET NULL
        )"""
        )

        // Opción simplificada: Solo para no socios
        db.execSQL(
            """CREATE TABLE daily_accesses(
        accessId INTEGER PRIMARY KEY AUTOINCREMENT,
        clientId INTEGER NOT NULL,
        activityId INTEGER NOT NULL,
        accessDate TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
        paymentId INTEGER NOT NULL,
        FOREIGN KEY(clientId) REFERENCES clients(clientId) ON DELETE CASCADE,
        FOREIGN KEY(activityId) REFERENCES activities(activityId) ON DELETE CASCADE,
        FOREIGN KEY(paymentId) REFERENCES payments(paymentId) ON DELETE CASCADE
    )"""
        )

        // Tabla de usuarios (sistema)
        db.execSQL(
            """CREATE TABLE users(
            userId INTEGER PRIMARY KEY AUTOINCREMENT,
            userName TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            userRole TEXT NOT NULL CHECK(userRole IN ('ADMIN', 'EMPLEADO')),
            active INTEGER NOT NULL DEFAULT 1
        )"""
        )

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS productos")
        onCreate(db)
    }


}