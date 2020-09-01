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

    fun addWord(name: String, learned: Int): Long {
        val contentValues = ContentValues()
        //contentValues.put(Constants.ID_WORD, id)
        contentValues.put(Constants.VOCABULARY_NAME_ITEM, name)
        contentValues.put(Constants.VOCABULARY_LEARNED_ITEM, learned)
        //utilizar los metodos de android de insert, update, etc.. aunque tambien se puede
        //hacer de la manera tradicional con db.exeSQL(INSEET....)
        //contentValues valores temporales de atributos cargado en Constants
        //indicamos la tabla a la cual haremos en insert
        return db!!.insert(Constants.ITEMS_VOCABULARY_TABLE, null, contentValues)
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
    fun getAllItemsVocabulary(): Cursor {
        //para hacer consultas utilizamos el metodo query
        //en sQLIte no hay el * debemos poner todos los campos y colocarlos en un array of
        //return db!!.query(Constants.ITEM_VOCABULARY_TABLE,
            //arrayOf(Constants.ID, Constants.NAME_ITEM, Constants.LEARNED_ITEM), null, null, null, null, null)

        val query =
            " SELECT ${Constants.VOCABULARY_ID_ITEM}, ${Constants.VOCABULARY_NAME_ITEM}, " +
            " ${Constants.VOCABULARY_LEARNED_ITEM} " +
            " FROM  ${Constants.ITEMS_VOCABULARY_TABLE} " +
            " WHERE ${Constants.VOCABULARY_LEARNED_ITEM} = 0 "

        val data = db!!.rawQuery(query,null)
        return data
    }

    //clase interna, herada de SQLite para acceder a la sintax de SQLite
    //creara la bd dbpersonas.db
    private class DictionariesDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "dbvocabularies.db", null, 1) {

        override fun onCreate(db: SQLiteDatabase) {
            //crear la tabla llamando los atributos de la clase object de Constants
            //para ejecutar consultas
            db.execSQL(
                "CREATE TABLE ${Constants.ITEMS_VOCABULARY_TABLE} (" +
                    "${Constants.VOCABULARY_ID_ITEM} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "${Constants.VOCABULARY_NAME_ITEM} TEXT NOT NULL, " +
                    "${Constants.VOCABULARY_LEARNED_ITEM} INTEGER )"
            )
            db.execSQL(
                "CREATE TABLE ${Constants.MEANINGS_TABLE} (" +
                    "${Constants.MEANING_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "${Constants.MEANING_VOCABULARY_ID_ITEM_} INTEGER, " +
                    "${Constants.MEANING_DESC_ONE} TEXT NOT NULL, " +
                    "${Constants.MEANING_DESC_TWO} TEXT NOT NULL )"
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS ${Constants.ITEMS_VOCABULARY_TABLE}")
            db.execSQL("DROP TABLE IF EXISTS ${Constants.MEANINGS_TABLE}")
            onCreate(db)
        }
    }
}