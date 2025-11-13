package com.example.pruebaprimeraclase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "ClubDeportivo.db", null, 1){

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE usuarios("+
                    "userId INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    "userName TEXT NOT NULL, " +
                    "userRol TEXT NOT NULL," +
                    "active INTEGER NOT NULL)"
        )

        db.execSQL(
            "CREATE TABLE persons("+
                    "personId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "personName TEXT NOT NULL," +
                    "personLastName TEXT NOT NULL," +
                    "docType TEXT NOT NULL," +
                    "docNumber TEXT NOT NULL," +
                    "birthDate TEXT NOT NULL," +
                    "address TEXT NOT NULL," +
                    "email TEXT NOT NULL," +
                    "phoneNumber TEXT NOT NULL)"
        )

        
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS productos")
        onCreate(db)
    }


}