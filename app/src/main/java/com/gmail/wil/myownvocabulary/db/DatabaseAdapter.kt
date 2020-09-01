package com.gmail.wil.myownvocabulary.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseAdapter(context: Context) {
    private val databaseHelper: DictionariesDatabaseHelper
    private var db: SQLiteDatabase? = null

    //init se ejecuta como un constructor, automaticamente se ejecutara al cargar la clase
    //en este caso una subclase de abajo
    init {
        databaseHelper = DictionariesDatabaseHelper(context)
    }

    fun abrir() {
        //abrir la bd
        db = databaseHelper.writableDatabase
    }

    fun cerrar() {
        //cerrar la bd
        databaseHelper.close()
    }

    fun addWord(name: String): Long {
        val contentValues = ContentValues()
        //contentValues.put(Constants.ID_WORD, id)
        contentValues.put(Constants.NAME_WORD, name)
        //utilizar los metodos de android de insert, update, etc.. aunque tambien se puede
        //hacer de la manera tradicional con db.exeSQL(INSEET....)
        //contentValues valores temporales de atributos cargado en Constants
        //indicamos la tabla a la cual haremos en insert
        return db!!.insert(Constants.WORD_TABLE, null, contentValues)
    }

//    fun actualizarPersona(id: Long, nombre: String, telefono: Long,
//                          correo: String, genero: String): Int {
//        val contentValues = ContentValues()
//        contentValues.put(Constants.NOMBRE, nombre)
//        contentValues.put(Constants.TELEFONO, telefono)
//        contentValues.put(Constants.CORREO, correo)
//        contentValues.put(Constants.GENERO, genero)
//
//        //necesario rescatar el id para que vaya en el where del update
//        return db!!.update(Constants.TABLA_PERSONA, contentValues, "${Constants.ID}=?", arrayOf(id.toString() + ""))
//    }

//    fun eliminarPersona(id: Long): Boolean {
//        //las condiciones del argumento del where se pueden concatenar para hacer mas flitro del query
//        return db!!.delete(Constants.TABLA_PERSONA, "${Constants.ID}=$id", null) > 0
//    }
//
//    fun obtenerPersona(id: Long): Cursor {
//        return db!!.query(Constants.TABLA_PERSONA,
//            arrayOf(Constants.ID, Constants.NOMBRE, Constants.TELEFONO, Constants.CORREO, Constants.GENERO),
//            "${Constants.ID}=?", arrayOf(id.toString() + ""), null, null, null)
//    }


    //el objeto del tipo Cursor es una tabla en formato kt SQL
    fun getAllWords(): Cursor {
        //para hacer consultas utilizamos el metodo query
        //en sQLIte no hay el * debemos poner todos los campos y colocarlos en un array of
        return db!!.query(Constants.WORD_TABLE,
            arrayOf(Constants.ID, Constants.NAME_WORD), null, null, null, null, null)
    }

    //clase interna, herada de SQLite para acceder a la sintax de SQLite
    //creara la bd dbpersonas.db
    private class DictionariesDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "dbdictionaries.db", null, 1) {

        override fun onCreate(db: SQLiteDatabase) {
            //crear la tabla llamando los atributos de la clase object de Constants
            //para ejecutar consultas
            db.execSQL("CREATE TABLE ${Constants.WORD_TABLE} (" +
                    "${Constants.ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "${Constants.NAME_WORD} TEXT NOT NULL) ")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS ${Constants.WORD_TABLE}")
            onCreate(db)
        }
    }
}