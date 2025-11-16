package com.example.pruebaprimeraclase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Locale
import android.content.ContentValues
class DBHelper(context: Context) : SQLiteOpenHelper(context, "ClubDeportivo.db", null, 5){

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
            monthlyFee REAL NOT NULL DEFAULT 50000,
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
        ('Carlos', 'empleado123', 'EMPLEADO', 1)
    """)

        // 2. PERSONAS (datos personales)
        db.execSQL("""
        INSERT INTO persons (firstName, lastName, docNumber, birthDate, address, email, phoneNumber, medicalCertificate) VALUES
        ('Juan', 'Pérez', '12345678', '1990-05-15', 'Av. Colón 123', 'juan.perez@email.com', '351-1234567', 1),
        ('María', 'González', '23456789', '1985-08-20', 'Calle San Martín 456', 'maria.gonzalez@email.com', '351-2345678', 1),
        ('Carlos', 'Rodríguez', '34567890', '1992-03-10', 'Av. Vélez Sarsfield 789', 'carlos.rodriguez@email.com', '351-3456789', 1),
        ('Ana', 'Martínez', '45678901', '1988-11-25', 'Calle Rivadavia 321', 'ana.martinez@email.com', '351-4567890', 1),
        ('Pedro', 'López', '56789012', '1995-07-03', 'Av. Figueroa Alcorta 654', 'pedro.lopez@email.com', '351-5678901', 1),
        ('Laura', 'Fernández', '67890123', '1993-12-08', 'Calle Deán Funes 147', 'laura.fernandez@email.com', '351-6789012', 1),
        ('Diego', 'Sánchez', '78901234', '1987-04-22', 'Av. Rafael Núñez 258', 'diego.sanchez@email.com', '351-7890123', 0),
        ('Sofía', 'Ramírez', '89012345', '1991-09-17', 'Calle Caseros 369', 'sofia.ramirez@email.com', '351-8901234', 1),
        ('Martín', 'Torres', '90123456', '1994-01-30', 'Av. Recta Martinoli 741', 'martin.torres@email.com', '351-9012345', 1),
        ('Valentina', 'Flores', '11223344', '1996-06-12', 'Calle Obispo Trejo 852', 'valentina.flores@email.com', '351-1122334', 0),
        ('Roberto', 'Gómez', '22334455', '1989-03-28', 'Av. Hipólito Yrigoyen 963', 'roberto.gomez@email.com', '351-2233445', 1),
        ('Lucía', 'Castro', '33445566', '1997-11-05', 'Calle Duarte Quirós 159', 'lucia.castro@email.com', '351-3344556', 1)
    """)

        // 3. CLIENTES
        // HOY ES: 15 de Noviembre de 2025
        db.execSQL("""
        INSERT INTO clients (personId, clientType, clientStatus, cardDelivered) VALUES
        (1, 'SOCIO', 'ACTIVO', 1),    -- Juan: Al día (pagó 01/11, vence 01/12)
        (2, 'SOCIO', 'ACTIVO', 1),    -- María: Al día (pagó 15/10, vence HOY 15/11)
        (3, 'SOCIO', 'ACTIVO', 1),    -- Carlos: Al día (pagó 16/10, vence MAÑANA 16/11)
        (4, 'SOCIO', 'ACTIVO', 0),    -- Ana: Deudor (pagó 12/10, venció hace 3 días el 12/11)
        (5, 'SOCIO', 'ACTIVO', 1),    -- Pedro: Deudor grave (pagó 01/10, venció hace 15 días el 31/10)
        (6, 'SOCIO', 'ACTIVO', 1),    -- Laura: Al día (pagó 20/10, vence en 5 días el 20/11)
        (7, 'SOCIO', 'ACTIVO', 1),    -- Roberto: Nuevo (primera cuota PENDIENTE, nunca pagó)
        (8, 'SOCIO', 'ACTIVO', 1),    -- Lucía: Pagó FUERA DE TÉRMINO (vencía 05/11, pagó 08/11)
        (9, 'NO SOCIO', NULL, 0),     -- Diego: No socio
        (10, 'NO SOCIO', NULL, 0),    -- Sofía: No socio
        (11, 'NO SOCIO', NULL, 0),    -- Martín: No socio
        (12, 'NO SOCIO', NULL, 0)     -- Valentina: No socio
    """)

        // 4. ACTIVIDADES
        db.execSQL("""
        INSERT INTO activities (activityName, activityTime, cost) VALUES
        ('Spinning', '18:00', 3000.00),
        ('Yoga', '19:00', 2500.00),
        ('Funcional', '20:00', 3500.00),
        ('Natación', '17:00', 4000.00),
        ('Pilates', '10:00', 3000.00),
        ('CrossFit', '07:00', 4500.00),
        ('Zumba', '20:30', 2800.00)
    """)

        // 5. MEMBRESÍAS (CUOTA = 50000)
        // LÓGICA: Cuando paga se crea ACTIVA + PENDIENTE siguiente
        // HOY: 15 de Noviembre de 2025
        db.execSQL("""
        INSERT INTO memberships (clientId, startDate, expiryDate, monthlyFee, status) VALUES
        
        -- Cliente 1 (Juan): Pagó 01/11, vence 01/12 (en 16 días)
        -- Historial:
        (1, '2025-08-01', '2025-08-31', 50000.00, 'FINALIZADA'),
        (1, '2025-09-01', '2025-09-30', 50000.00, 'FINALIZADA'),
        (1, '2025-10-01', '2025-10-31', 50000.00, 'FINALIZADA'),
        -- Cuota actual (pagó 01/11):
        (1, '2025-11-01', '2025-12-01', 50000.00, 'ACTIVA'),     -- Vence 01/12
        (1, '2025-12-01', '2025-12-31', 50000.00, 'PENDIENTE'),  -- Próxima cuota
        
        -- Cliente 2 (María): Pagó 15/10, vence HOY 15/11 (0 días)
        (2, '2025-08-15', '2025-09-14', 50000.00, 'FINALIZADA'),
        (2, '2025-09-15', '2025-10-14', 50000.00, 'FINALIZADA'),
        -- Cuota actual (pagó 15/10):
        (2, '2025-10-15', '2025-11-15', 50000.00, 'ACTIVA'),     -- Vence HOY
        (2, '2025-11-15', '2025-12-15', 50000.00, 'PENDIENTE'),  -- Próxima cuota
        
        -- Cliente 3 (Carlos): Pagó 16/10, vence MAÑANA 16/11 (1 día)
        (3, '2025-08-16', '2025-09-15', 50000.00, 'FINALIZADA'),
        (3, '2025-09-16', '2025-10-15', 50000.00, 'FINALIZADA'),
        -- Cuota actual (pagó 16/10):
        (3, '2025-10-16', '2025-11-16', 50000.00, 'ACTIVA'),     -- Vence mañana
        (3, '2025-11-16', '2025-12-16', 50000.00, 'PENDIENTE'),  -- Próxima cuota
        
        -- Cliente 4 (Ana): DEUDOR - Pagó 12/10, venció hace 3 días (12/11)
        (4, '2025-08-12', '2025-09-11', 50000.00, 'FINALIZADA'),
        (4, '2025-09-12', '2025-10-11', 50000.00, 'FINALIZADA'),
        -- Cuota vencida (no pagó a tiempo):
        (4, '2025-10-12', '2025-11-12', 50000.00, 'VENCIDA'),    -- Venció hace 3 días
        -- NO tiene cuota PENDIENTE porque la actual está VENCIDA
        
        -- Cliente 5 (Pedro): DEUDOR GRAVE - Pagó 01/10, venció hace 15 días (31/10)
        (5, '2025-07-01', '2025-07-31', 50000.00, 'FINALIZADA'),
        (5, '2025-08-01', '2025-08-31', 50000.00, 'FINALIZADA'),
        (5, '2025-09-01', '2025-09-30', 50000.00, 'FINALIZADA'),
        -- Cuota vencida hace tiempo:
        (5, '2025-10-01', '2025-10-31', 50000.00, 'VENCIDA'),    -- Venció hace 15 días
        
        -- Cliente 6 (Laura): Pagó 20/10, vence en 5 días (20/11)
        (6, '2025-08-20', '2025-09-19', 50000.00, 'FINALIZADA'),
        (6, '2025-09-20', '2025-10-19', 50000.00, 'FINALIZADA'),
        -- Cuota actual (pagó 20/10):
        (6, '2025-10-20', '2025-11-20', 50000.00, 'ACTIVA'),     -- Vence en 5 días
        (6, '2025-11-20', '2025-12-20', 50000.00, 'PENDIENTE'),  -- Próxima cuota
        
        -- Cliente 7 (Roberto): NUEVO SOCIO - Primera cuota sin pagar
        (7, NULL, NULL, 50000.00, 'PENDIENTE'),                  -- Primera cuota PENDIENTE
        
        -- Cliente 8 (Lucía): Pagó FUERA DE TÉRMINO
        -- Vencía 05/11, pero pagó 08/11 (3 días tarde)
        (8, '2025-08-05', '2025-09-04', 50000.00, 'FINALIZADA'),
        (8, '2025-09-05', '2025-10-05', 50000.00, 'FINALIZADA'),
        -- La cuota anterior venció:
        (8, '2025-10-05', '2025-11-05', 50000.00, 'VENCIDA'),    -- Venció el 05/11
        -- Pagó el 08/11, entonces nueva fecha = 08/11 + 30 = 08/12
        (8, '2025-11-08', '2025-12-08', 50000.00, 'ACTIVA'),     -- Pagó fuera de término
        (8, '2025-12-08', '2026-01-07', 50000.00, 'PENDIENTE')   -- Próxima cuota
    """)

        // 6. INSCRIPCIONES A ACTIVIDADES (Noviembre 2025)
        db.execSQL("""
        INSERT INTO activityRegistrations (clientId, activityId, accessDate) VALUES
        -- No socios
        (9, 1, '2025-11-10'),   -- Diego: Spinning
        (9, 2, '2025-11-12'),   -- Diego: Yoga
        (10, 3, '2025-11-11'),  -- Sofía: Funcional
        (10, 5, '2025-11-13'),  -- Sofía: Pilates
        (11, 4, '2025-11-08'),  -- Martín: Natación
        (11, 6, '2025-11-09'),  -- Martín: CrossFit
        (11, 7, '2025-11-10'),  -- Martín: Zumba
        (12, 1, '2025-11-14'),  -- Valentina: Spinning
        
        -- Socios también pueden inscribirse
        (1, 4, '2025-11-13'),   -- Juan: Natación
        (2, 1, '2025-11-13')    -- María: Spinning
    """)

        // 7. PAGOS - Cuotas mensuales
        // IMPORTANTE: Solo hay PAGOS para cuotas en estado ACTIVA o FINALIZADA
        db.execSQL("""
        INSERT INTO payments (clientId, membershipId, amount, paymentType, paymentMethod, installments, paymentDate, dueDate, paymentStatus) VALUES
        
        -- Cliente 1 (Juan) - Historial completo
        (1, 1, 50000.00, 'MENSUAL', 'EFECTIVO', 1, '2025-08-01', '2025-08-10', 'PAGADO'),
        (1, 2, 50000.00, 'MENSUAL', 'TARJETA', 1, '2025-09-01', '2025-09-10', 'PAGADO'),
        (1, 3, 50000.00, 'MENSUAL', 'EFECTIVO', 1, '2025-10-01', '2025-10-10', 'PAGADO'),
        (1, 4, 50000.00, 'MENSUAL', 'TARJETA', 1, '2025-11-01', '2025-11-10', 'PAGADO'),  -- Cuota actual
        
        -- Cliente 2 (María) - Vence HOY
        (2, 6, 50000.00, 'MENSUAL', 'EFECTIVO', 1, '2025-08-15', '2025-08-25', 'PAGADO'),
        (2, 7, 50000.00, 'MENSUAL', 'TARJETA', 1, '2025-09-15', '2025-09-25', 'PAGADO'),
        (2, 8, 50000.00, 'MENSUAL', 'EFECTIVO', 1, '2025-10-15', '2025-10-25', 'PAGADO'),  -- Vence hoy
        
        -- Cliente 3 (Carlos) - Vence MAÑANA
        (3, 10, 50000.00, 'MENSUAL', 'TARJETA', 1, '2025-08-16', '2025-08-26', 'PAGADO'),
        (3, 11, 50000.00, 'MENSUAL', 'EFECTIVO', 1, '2025-09-16', '2025-09-26', 'PAGADO'),
        (3, 12, 50000.00, 'MENSUAL', 'TARJETA', 1, '2025-10-16', '2025-10-26', 'PAGADO'),  -- Vence mañana
        
        -- Cliente 4 (Ana) - DEUDOR (última cuota pagada, la actual está VENCIDA)
        (4, 14, 50000.00, 'MENSUAL', 'EFECTIVO', 1, '2025-08-12', '2025-08-22', 'PAGADO'),
        (4, 15, 50000.00, 'MENSUAL', 'TARJETA', 1, '2025-09-12', '2025-09-22', 'PAGADO'),
        (4, 16, 50000.00, 'MENSUAL', 'EFECTIVO', 1, '2025-10-12', '2025-10-22', 'PAGADO'),
        -- Membresía 16 está VENCIDA, NO tiene pago
        
        -- Cliente 5 (Pedro) - DEUDOR GRAVE
        (5, 17, 50000.00, 'MENSUAL', 'TARJETA', 1, '2025-07-01', '2025-07-11', 'PAGADO'),
        (5, 18, 50000.00, 'MENSUAL', 'EFECTIVO', 1, '2025-08-01', '2025-08-11', 'PAGADO'),
        (5, 19, 50000.00, 'MENSUAL', 'TARJETA', 1, '2025-09-01', '2025-09-11', 'PAGADO'),
        (5, 20, 50000.00, 'MENSUAL', 'EFECTIVO', 1, '2025-10-01', '2025-10-11', 'PAGADO'),
        -- Membresía 20 está VENCIDA, NO tiene pago
        
        -- Cliente 6 (Laura) - Al día
        (6, 22, 50000.00, 'MENSUAL', 'TARJETA', 1, '2025-08-20', '2025-08-30', 'PAGADO'),
        (6, 23, 50000.00, 'MENSUAL', 'EFECTIVO', 1, '2025-09-20', '2025-09-30', 'PAGADO'),
        (6, 24, 50000.00, 'MENSUAL', 'TARJETA', 1, '2025-10-20', '2025-10-30', 'PAGADO'),
        
        -- Cliente 7 (Roberto): NUEVO - NO tiene pagos
        
        -- Cliente 8 (Lucía) - Pagó FUERA DE TÉRMINO
        (8, 27, 50000.00, 'MENSUAL', 'EFECTIVO', 1, '2025-08-05', '2025-08-15', 'PAGADO'),
        (8, 28, 50000.00, 'MENSUAL', 'TARJETA', 1, '2025-09-05', '2025-09-15', 'PAGADO'),
        (8, 29, 50000.00, 'MENSUAL', 'EFECTIVO', 1, '2025-10-05', '2025-10-15', 'PAGADO'),
        -- Membresía 29 venció el 05/11, NO pagó a tiempo
        (8, 30, 50000.00, 'MENSUAL', 'TARJETA', 1, '2025-11-08', '2025-11-18', 'PAGADO')   -- Pagó 3 días tarde
    """)

        // 8. PAGOS - Actividades de no socios (Noviembre 2025)
        db.execSQL("""
        INSERT INTO payments (clientId, membershipId, amount, paymentType, paymentMethod, installments, paymentDate, dueDate, paymentStatus) VALUES
        
        -- Diego (cliente 9) - Pagó 2 actividades juntas
        (9, NULL, 5500.00, 'DIARIA', 'EFECTIVO', 1, '2025-11-10', '2025-11-10', 'PAGADO'),
        
        -- Sofía (cliente 10) - Pagó 2 actividades separadas
        (10, NULL, 3500.00, 'DIARIA', 'TARJETA', 1, '2025-11-11', '2025-11-11', 'PAGADO'),
        (10, NULL, 3000.00, 'DIARIA', 'EFECTIVO', 1, '2025-11-13', '2025-11-13', 'PAGADO'),
        
        -- Martín (cliente 11) - Pagó 3 actividades juntas
        (11, NULL, 12000.00, 'DIARIA', 'TARJETA', 3, '2025-11-08', '2025-11-08', 'PAGADO'),
        
        -- Valentina (cliente 12) - Pago reciente
        (12, NULL, 3000.00, 'DIARIA', 'EFECTIVO', 1, '2025-11-14', '2025-11-14', 'PAGADO')
    """)

        // 9. DETALLE DE PAGOS - Relaciona pagos con actividades
        db.execSQL("""
        INSERT INTO paymentDetails (paymentId, activityRegistrationId) VALUES
        -- Pago 23: Diego pagó Spinning + Yoga
        (23, 1),
        (23, 2),
        
        -- Pago 24: Sofía pagó Funcional
        (24, 3),
        
        -- Pago 25: Sofía pagó Pilates
        (25, 4),
        
        -- Pago 26: Martín pagó Natación + CrossFit + Zumba
        (26, 5),
        (26, 6),
        (26, 7),
        
        -- Pago 27: Valentina pagó Spinning
        (27, 8)
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

    // ====== CODIGO nahuw ======


    fun addClient(
        firstName: String,
        lastName: String,
        docNumber: String,
        birthDate: String, // Formato "YYYY-MM-DD"
        address: String,
        email: String,
        phoneNumber: String,
        medicalCertificate: Boolean,
        clientType: String // "SOCIO" o "NO SOCIO"
    ): Boolean {

        val db = this.writableDatabase
        db.beginTransaction()
        var success = false

        try {
            // --- 1. Insertar en la tabla 'persons' ---
            val personValues = ContentValues().apply {
                put("firstName", firstName)
                put("lastName", lastName)
                put("docNumber", docNumber)
                put("birthDate", birthDate) // Asumimos que la fecha ya está en formato YYYY-MM-DD
                put("address", address)
                put("email", email)
                put("phoneNumber", phoneNumber)
                put("medicalCertificate", if (medicalCertificate) 1 else 0)
            }

            // insert() devuelve el ID de la fila nueva, o -1 si hubo un error
            val personId = db.insert("persons", null, personValues)

            if (personId == -1L) {
                // Si falla la inserción de persona, no continuar
                throw Exception("Error al insertar en la tabla persons")
            }

            // --- 2. Insertar en la tabla 'clients' usando el personId ---
            val clientValues = ContentValues().apply {
                put("personId", personId)
                put("clientType", clientType)

                // Lógica de negocio:
                // Si es SOCIO, su estado inicial es ACTIVO (o INACTIVO si debe pagar primero)
                // Si es NO SOCIO, el estado es NULL (como definiste en tu schema)
                if (clientType == "SOCIO") {
                    // Puedes ponerlo "INACTIVO" hasta que pague la 1ra cuota,
                    // o "ACTIVO" si la inscripción ya lo activa. Usaremos "ACTIVO" por simpleza.
                    put("clientStatus", "ACTIVO")
                }
                // registrationDate y cardDelivered usan sus valores DEFAULT, no es necesario ponerlos
            }

            val clientId = db.insert("clients", null, clientValues)

            if (clientId == -1L) {
                // Si falla la inserción de cliente, hacer rollback
                throw Exception("Error al insertar en la tabla clients")
            }

            // Si ambas inserciones fueron exitosas, marcar la transacción como exitosa
            db.setTransactionSuccessful()
            success = true

        } catch (e: Exception) {
            // En caso de error, la transacción no se marcará como exitosa y se revertirá
            // (Puedes loggear el error e.message)
        } finally {
            // Finalizar la transacción. Se comitea si fue successful, o se revierte si no.
            db.endTransaction()
        }

        // Devolver true si todo salió bien, false si hubo un error
        return success
    // ===== FIN CODIGO nahuew =========
        }
}