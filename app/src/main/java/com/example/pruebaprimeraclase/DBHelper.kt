package com.example.pruebaprimeraclase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Locale

class DBHelper(context: Context) : SQLiteOpenHelper(context, "ClubDeportivo.db", null, 4){

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
                    clientType TEXT NOT NULL CHECK(clientType IN ('SOCIO', 'NO SOCIO')),
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

        // inserto datos de base
        insertTestData(db)

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


    private fun insertTestData(db: SQLiteDatabase) {

        // 1. USUARIOS DEL SISTEMA
        db.execSQL("""
            INSERT INTO users (userName, password, userRole, active) VALUES
            ('admin', '1234', 'ADMIN', 1), 
            ('Mariana', '12345', 'EMPLEADO', 1),
            ('empleado2', 'emp456', 'EMPLEADO', 1)
        """)


        // 2. PERSONAS (datos personales)
        db.execSQL("""
            INSERT INTO persons (firstName, lastName, docNumber, birthDate, address, email, phoneNumber, medicalCertificate) VALUES
            ('Juan', 'Pérez', '12345678', '1990-05-15', 'Av. Colón 123', 'juan.perez@email.com', '351-1234567', 1),
            ('María', 'González', '23456789', '1985-08-20', 'Calle San Martín 456', 'maria.gonzalez@email.com', '351-2345678', 1),
            ('Carlos', 'Rodríguez', '34567890', '1992-03-10', 'Av. Vélez Sarsfield 789', 'carlos.rodriguez@email.com', '351-3456789', 0),
            ('Ana', 'Martínez', '45678901', '1988-11-25', 'Calle Rivadavia 321', 'ana.martinez@email.com', '351-4567890', 1),
            ('Pedro', 'López', '56789012', '1995-07-03', 'Av. Figueroa Alcorta 654', 'pedro.lopez@email.com', '351-5678901', 0)
        """)

        // 3. CLIENTES (socios y no socios)
        db.execSQL("""
            INSERT INTO clients (personId, clientType, clientStatus, cardDelivered) VALUES
            (1, 'SOCIO', 'ACTIVO', 1),
            (2, 'SOCIO', 'ACTIVO', 1),
            (3, 'SOCIO', 'INACTIVO', 0),
            (4, 'NO SOCIO', NULL, 0),
            (5, 'NO SOCIO', NULL, 0)
        """)

        // 4. MEMBRESÍAS (solo para socios - IDs de cliente 1, 2, 3)
        db.execSQL("""
            INSERT INTO memberships (clientId, startDate, expiryDate, monthlyFee, status) VALUES
            (1, '2024-11-01', '2024-11-30', 15000.00, 'ACTIVA'),
            (2, '2024-10-15', '2024-11-14', 18000.00, 'ACTIVA'),
            (3, '2024-09-01', '2024-10-01', 15000.00, 'VENCIDA')
        """)

        // 5. ACTIVIDADES
        db.execSQL("""
            INSERT INTO activities (activityName, activityTime, cost) VALUES
            ('Spinning', '18:00', 3000.00),
            ('Yoga', '19:00', 2500.00),
            ('Funcional', '20:00', 3500.00),
            ('Natación', '17:00', 4000.00),
            ('Pilates', '10:00', 3000.00)
        """)

        // 6. INSCRIPCIONES A ACTIVIDADES (para no socios 4 y 5, y socios 1 y 2)
        db.execSQL("""
            INSERT INTO activityRegistrations (clientId, activityId, accessDate) VALUES
            (4, 1, '2024-11-10'),
            (4, 2, '2024-11-12'),
            (5, 3, '2024-11-11'),
            (1, 4, '2024-11-13'),
            (2, 1, '2024-11-13')
        """)

        // 7. PAGOS - Cuotas mensuales de socios (IDs de cliente 1, 2, 3)
        db.execSQL("""
            INSERT INTO payments (clientId, membershipId, amount, paymentType, paymentMethod, installments, paymentDate, dueDate, paymentStatus) VALUES
            (1, 1, 15000.00, 'MENSUAL', 'EFECTIVO', 1, '2024-11-01', '2024-11-10', 'PAGADO'),
            (2, 2, 18000.00, 'MENSUAL', 'TARJETA', 1, '2024-10-15', '2024-10-15', 'PAGADO'),
            (3, 3, 15000.00, 'MENSUAL', NULL, 1, NULL, '2024-09-10', 'PENDIENTE')
        """)

        // 8. PAGOS - Actividades de no socios (IDs de cliente 4, 5)
        db.execSQL("""
            INSERT INTO payments (clientId, membershipId, amount, paymentType, paymentMethod, installments, paymentDate, dueDate, paymentStatus) VALUES
            (4, NULL, 5500.00, 'DIARIA', 'EFECTIVO', 1, '2024-11-10', '2024-11-10', 'PAGADO'),
            (5, NULL, 3500.00, 'DIARIA', 'TARJETA', 1, '2024-11-11', '2024-11-11', 'PAGADO')
        """)

        // 9. DETALLE DE PAGOS
        // El Pago 4 (de 5500) corresponde a las inscripciones 1 y 2 (Spinning + Yoga)
        // El Pago 5 (de 3500) corresponde a la inscripción 3 (Funcional)
        db.execSQL("""
            INSERT INTO paymentDetails (paymentId, activityRegistrationId) VALUES
            (4, 1),
            (4, 2),
            (5, 3)
        """)
    }


    // Verifica si credenciales de Usuario son correctas
    fun validateUser(usuario: String, pass: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE userName = ? AND password = ? AND active = 1",
            arrayOf(usuario, pass)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }


    // Verifica si un cliente existe (por su Doc).
    fun validateClientByDoc(docNumber: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM persons p " +
                    "INNER JOIN clients c ON p.personId = c.personId " +
                    "WHERE p.docNumber = ? LIMIT 1",
            arrayOf(docNumber)
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // Obtiene datos del Cliente (por su Doc).
    fun getClientByDoc(docNumber: String): ClientData? {
        val db = this.readableDatabase

        // Consulta que une la tabla Personas y Clientes, y trae todos los campos
        val cursor = db.rawQuery(
            """SELECT 
                   p.personId, p.firstName, p.lastName, p.docNumber, p.birthDate, p.address, p.email, p.phoneNumber, p.medicalCertificate,
                   c.clientId, c.clientType, c.clientStatus, c.registrationDate, c.cardDelivered
               FROM persons p 
               INNER JOIN clients c ON p.personId = c.personId 
               WHERE p.docNumber = ?""",
            arrayOf(docNumber)
        )

        if (cursor.moveToFirst()) {
            // 2. Mapea los datos del cursor a la data class
            val client = ClientData(
                // Datos de Persons
                personId = cursor.getInt(0),
                firstName = cursor.getString(1).lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) },
                lastName = cursor.getString(2).lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) },
                docNumber = cursor.getString(3),
                birthDate = cursor.getString(4),
                address = cursor.getString(5),
                email = cursor.getString(6),
                phoneNumber = cursor.getString(7),
                medicalCertificate = cursor.getInt(8) == 1, // Convierte Int a Boolean

                // Datos de Clients
                clientId = cursor.getInt(9),
                clientType = cursor.getString(10),
                clientStatus = cursor.getString(11), // Obtiene el String (puede ser null)
                registrationDate = cursor.getString(12),
                cardDelivered = cursor.getInt(13) == 1 // Convierte Int a Boolean
            )

            cursor.close()
            return client // 3. Devuelve el objeto completo
        }

        cursor.close()
        return null // No se encontró el cliente
    }

    // Obtiene la membresía "vigente" (ACTIVA o VENCIDA) y, SOLO SI APLICA, la "próxima" (PENDIENTE).
    // Esta función implementa la lógica de negocio de pagos.
    fun getSocioMembershipList(clientId: Int): MembershipList {
        val db = this.readableDatabase
        var current: MembershipData? = null
        var next: MembershipData? = null

        // 1. Obtener la membresía "vigente".
        // Es la más reciente que ya ha comenzado (startDate <= hoy) Y no está finalizada.
        // Esto encontrará 'ACTIVA' o 'VENCIDA'.
        val cursorCurrent = db.rawQuery(
            """SELECT membershipId, clientId, startDate, expiryDate, status
               FROM memberships
               WHERE clientId = ? AND status != 'FINALIZADA' AND status != 'CANCELADA'
               AND date(startDate) <= date('now')
               ORDER BY date(startDate) DESC
               LIMIT 1""",
            arrayOf(clientId.toString())
        )

        if (cursorCurrent.moveToFirst()) {
            current = MembershipData(
                membershipId = cursorCurrent.getInt(0),
                clientId = cursorCurrent.getInt(1),
                startDate = cursorCurrent.getString(2) ?: "", // Maneja NULL
                expiryDate = cursorCurrent.getString(3) ?: "", // Maneja NULL
                monthlyFee = 0.0, // No se usa en este caso
                status = cursorCurrent.getString(4)
            )
        }
        cursorCurrent.close()

        // 2. Si no encontramos una 'vigente', buscar la 'PENDIENTE' inicial
        if (current == null) {
            val cursorPending = db.rawQuery(
                """SELECT membershipId, clientId, startDate, expiryDate, status
               FROM memberships
               WHERE clientId = ? AND status = 'PENDIENTE'
               ORDER BY date(startDate) ASC 
               LIMIT 1""",
                arrayOf(clientId.toString())
            )
            if (cursorPending.moveToFirst()) {
                current = MembershipData(
                    membershipId = cursorPending.getInt(0),
                    clientId = cursorPending.getInt(1),
                    startDate = cursorPending.getString(2) ?: "", // Maneja NULL
                    expiryDate = cursorPending.getString(3) ?: "", // Maneja NULL
                    monthlyFee = cursorPending.getDouble(4),
                    status = cursorPending.getString(5)
                )
            }
            cursorPending.close()
        }


        // 3. Solo buscar la próxima membresía (PENDIENTE futura)
        //    SI la membresía "vigente" es 'ACTIVA'.
        if (current != null && current.status == "ACTIVA") {

            val cursorNext = db.rawQuery(
                """SELECT membershipId, clientId, startDate, expiryDate, monthlyFee, status
                   FROM memberships
                   WHERE clientId = ? AND status = 'PENDIENTE'
                   AND date(startDate) > date('now')
                   ORDER BY date(startDate) ASC 
                   LIMIT 1""",
                arrayOf(clientId.toString())
            )

            if (cursorNext.moveToFirst()) {
                next = MembershipData(
                    membershipId = cursorNext.getInt(0),
                    clientId = cursorNext.getInt(1),
                    startDate = cursorNext.getString(2),
                    expiryDate = cursorNext.getString(3),
                    monthlyFee = cursorNext.getDouble(4),
                    status = cursorNext.getString(5)
                )
            }
            cursorNext.close()
        }

        // Devuelve el contenedor.
        return MembershipList(current, next)
    }



}