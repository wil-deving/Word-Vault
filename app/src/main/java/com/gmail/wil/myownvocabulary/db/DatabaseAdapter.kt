package com.gmail.wil.myownvocabulary.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseAdapter(context: Context) {
    private val databaseHelper: DictionariesDatabaseHelper
    private var db: SQLiteDatabase? = null

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

    fun addItemVocabulary(idItem: String, name: String, learned: Int) : Long {
        val contentValues = ContentValues()
        contentValues.put(Constants.VOCABULARY_ID_ITEM, idItem)
        contentValues.put(Constants.VOCABULARY_NAME_ITEM, name)
        contentValues.put(Constants.VOCABULARY_LEARNED_ITEM, learned)
        return db!!.insert(Constants.ITEMS_VOCABULARY_TABLE, null, contentValues)
    }

    fun addMeaning(idMeaning: String, idItemVocabulary: String,
                   descOne: String, descTwo: String) : Long {
        val contentValues = ContentValues()
        contentValues.put(Constants.MEANING_ID, idMeaning)
        contentValues.put(Constants.MEANING_VOCABULARY_ID_ITEM, idItemVocabulary)
        contentValues.put(Constants.MEANING_DESC_ONE, descOne)
        contentValues.put(Constants.MEANING_DESC_TWO, descTwo)
        return db!!.insert(Constants.MEANINGS_TABLE, null, contentValues)
    }

    fun updateMeaning(idMeaning: String, idItemVoc: String, descOrigin: String,
                      descSecundary: String) : Int {
        val contentValues = ContentValues()
        contentValues.put(Constants.MEANING_DESC_ONE, descOrigin)
        contentValues.put(Constants.MEANING_DESC_TWO, descSecundary)
        //necesario rescatar el id para que vaya en el where del update
        return db!!.update(Constants.MEANINGS_TABLE, contentValues, "${Constants.MEANING_ID}=?", arrayOf(idMeaning))
    }

    fun deleteMeaning(id: String) : Boolean {
        //las condiciones del argumento del where se pueden concatenar para hacer mas flitro del query
        return db!!.delete(Constants.MEANINGS_TABLE, "${Constants.MEANING_ID}='$id'", null) > 0
    }

    fun getItemsLookLike(nameInput: String = "") : Cursor {
        val query =
            " SELECT ${Constants.VOCABULARY_NAME_ITEM} " +
            " FROM ${Constants.ITEMS_VOCABULARY_TABLE} " +
            " WHERE ${Constants.VOCABULARY_NAME_ITEM} LIKE '%$nameInput%' "
        val data = db!!.rawQuery(query,null)
        return data
    }

    fun updateTypeItemVocabulary(id: String, learned: Int) : Int {
        val contentValues = ContentValues()
        contentValues.put(Constants.VOCABULARY_LEARNED_ITEM, learned)
        //necesario rescatar el id para que vaya en el where del update
        return db!!.update(Constants.ITEMS_VOCABULARY_TABLE, contentValues, "${Constants.VOCABULARY_ID_ITEM}=?", arrayOf(id))
    }

    fun updateDataItemVocabulary(id: String, newNameItemV: String) : Int {
        val contentValues = ContentValues()
        contentValues.put(Constants.VOCABULARY_NAME_ITEM, newNameItemV)
        //necesario rescatar el id para que vaya en el where del update
        return db!!.update(Constants.ITEMS_VOCABULARY_TABLE, contentValues, "${Constants.VOCABULARY_ID_ITEM}=?", arrayOf(id))
    }

    fun deleteItemVocabulary(id: String) : Boolean {
        //las condiciones del argumento del where se pueden concatenar para hacer mas flitro del query
        return db!!.delete(Constants.ITEMS_VOCABULARY_TABLE, "${Constants.VOCABULARY_ID_ITEM}='$id'", null) > 0
    }

    fun getMeaningsByItem(idItemVocabulary: String = "") : Cursor {
        val query =
            " SELECT m.${Constants.MEANING_ID}, i.${Constants.VOCABULARY_ID_ITEM}, " +
            " m.${Constants.MEANING_DESC_ONE}, m.${Constants.MEANING_DESC_TWO} " +
            " FROM ${Constants.MEANINGS_TABLE} m INNER JOIN ${Constants.ITEMS_VOCABULARY_TABLE} i " +
            " ON m.${Constants.MEANING_VOCABULARY_ID_ITEM} = i.${Constants.VOCABULARY_ID_ITEM} " +
            " WHERE m.${Constants.MEANING_VOCABULARY_ID_ITEM} = '$idItemVocabulary' "
        val data = db!!.rawQuery(query,null)
        return data
    }

    fun getDistinctMeaningsByItem(idItemVocabulary: String = "") : Cursor {
        val query =
            " SELECT m.${Constants.MEANING_ID}, i.${Constants.VOCABULARY_ID_ITEM}, " +
                    " m.${Constants.MEANING_DESC_ONE}, m.${Constants.MEANING_DESC_TWO} " +
                    " FROM ${Constants.MEANINGS_TABLE} m INNER JOIN ${Constants.ITEMS_VOCABULARY_TABLE} i " +
                    " ON m.${Constants.MEANING_VOCABULARY_ID_ITEM} = i.${Constants.VOCABULARY_ID_ITEM} " +
                    " WHERE m.${Constants.MEANING_VOCABULARY_ID_ITEM} != '$idItemVocabulary' "
        val data = db!!.rawQuery(query,null)
        return data
    }

    fun getItemsVocabularyFiltered(filterType: String, textSearch: String): Cursor {
        var query =
            " SELECT ${Constants.VOCABULARY_ID_ITEM}, ${Constants.VOCABULARY_NAME_ITEM}, " +
            " ${Constants.VOCABULARY_LEARNED_ITEM} " +
            " FROM  ${Constants.ITEMS_VOCABULARY_TABLE} "
        if (filterType == "tolearn") query += " WHERE ${Constants.VOCABULARY_LEARNED_ITEM} = 0 "
        else if (filterType == "learned") query += " WHERE ${Constants.VOCABULARY_LEARNED_ITEM} = 1 "
        if (textSearch != "") query += " AND ${Constants.VOCABULARY_NAME_ITEM} LIKE '%$textSearch%' "
        val data = db!!.rawQuery(query,null)
        return data
    }

    //clase interna, herada de SQLite para acceder a la sintax de SQLite
    //creara la bd
    private class DictionariesDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "dbvocabularies.db", null, 1) {

        override fun onCreate(db: SQLiteDatabase) {
            //crear la tabla llamando los atributos de la clase object de Constants
            //para ejecutar consultas
            db.execSQL(
                "CREATE TABLE ${Constants.ITEMS_VOCABULARY_TABLE} (" +
                    "${Constants.VOCABULARY_ID_ITEM} TEXT PRIMARY KEY, " +
                    "${Constants.VOCABULARY_NAME_ITEM} TEXT NOT NULL, " +
                    "${Constants.VOCABULARY_LEARNED_ITEM} INTEGER )"
            )
            db.execSQL(
                "CREATE TABLE ${Constants.MEANINGS_TABLE} (" +
                    "${Constants.MEANING_ID} TEXT PRIMARY KEY, " +
                    "${Constants.MEANING_VOCABULARY_ID_ITEM} TEXT NOT NULL, " +
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