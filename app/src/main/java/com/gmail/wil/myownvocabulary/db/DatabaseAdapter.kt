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

    fun addDataPractice(idPractice: String, idItemVocabulary: String) : Long {
        val contentValues = ContentValues()
        contentValues.put(Constants.PRACTICE_ID, idPractice)
        contentValues.put(Constants.PRACTICE_VOCABULARY_ID_ITEM, idItemVocabulary)
        contentValues.put(Constants.SHOTS, 0)
        return db!!.insert(Constants.PRACTICE_TABLE, null, contentValues)
    }

    fun addMeaning(idMeaning: String, idItemVocabulary: String,
                   descOne: String, descTwo: String, type: String) : Long {
        val contentValues = ContentValues()
        contentValues.put(Constants.MEANING_ID, idMeaning)
        contentValues.put(Constants.MEANING_VOCABULARY_ID_ITEM, idItemVocabulary)
        contentValues.put(Constants.MEANING_DESC_ONE, descOne)
        contentValues.put(Constants.MEANING_DESC_TWO, descTwo)
        contentValues.put(Constants.MEANING_TYPE, type)
        return db!!.insert(Constants.MEANINGS_TABLE, null, contentValues)
    }

    fun updateMeaning(idMeaning: String, idItemVoc: String, descOrigin: String,
                      descSecundary: String, type: String) : Int {
        val contentValues = ContentValues()
        contentValues.put(Constants.MEANING_DESC_ONE, descOrigin)
        contentValues.put(Constants.MEANING_DESC_TWO, descSecundary)
        contentValues.put(Constants.MEANING_TYPE, type)
        //necesario rescatar el id para que vaya en el where del update
        return db!!.update(Constants.MEANINGS_TABLE, contentValues, "${Constants.MEANING_ID}=?", arrayOf(idMeaning))
    }

    fun deleteMeaning(id: String) : Boolean {
        //las condiciones del argumento del where se pueden concatenar para hacer mas flitro del query
        return db!!.delete(Constants.MEANINGS_TABLE, "${Constants.MEANING_ID}='$id'", null) > 0
    }

    fun getItemsLookLike(nameInput: String = "") : Cursor {
        val args = arrayOf("%$nameInput%")
        val query =
            " SELECT ${Constants.VOCABULARY_NAME_ITEM} " +
            " FROM ${Constants.ITEMS_VOCABULARY_TABLE} " +
            " WHERE ${Constants.VOCABULARY_NAME_ITEM} LIKE ? "
        val data = db!!.rawQuery(query, args)
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

    fun updateDataItemPractice(idItemVoc: String, shots: Int) : Int {
        val contentValues = ContentValues()
        contentValues.put(Constants.SHOTS, shots)
        //necesario rescatar el id para que vaya en el where del update
        return db!!.update(Constants.PRACTICE_TABLE, contentValues, "${Constants.PRACTICE_VOCABULARY_ID_ITEM}=?", arrayOf(idItemVoc))
    }

    fun deleteItemVocabulary(id: String) : Boolean {
        //las condiciones del argumento del where se pueden concatenar para hacer mas flitro del query
        return db!!.delete(Constants.ITEMS_VOCABULARY_TABLE, "${Constants.VOCABULARY_ID_ITEM}='$id'", null) > 0
    }

    fun deleteMeaningsByItem(idItemVocabulary: String) : Boolean {
        return db!!.delete(Constants.MEANINGS_TABLE,
            "${Constants.MEANING_VOCABULARY_ID_ITEM}='$idItemVocabulary'", null) > 0
    }

    fun deletePracticeByItem(idItemVocabulary: String) : Boolean{
        return db!!.delete(Constants.PRACTICE_TABLE,
            "${Constants.PRACTICE_VOCABULARY_ID_ITEM}='$idItemVocabulary'", null) > 0
    }

    fun getMeaningsByItem(idItemVocabulary: String = "") : Cursor {
        val query =
            " SELECT m.${Constants.MEANING_ID}, i.${Constants.VOCABULARY_ID_ITEM}, " +
            " m.${Constants.MEANING_DESC_ONE}, m.${Constants.MEANING_DESC_TWO}, " +
            " m.${Constants.MEANING_TYPE} " +
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

    fun getPracticeByItem(idItemVocabulary: String = "") : Cursor {
        val query =
            " SELECT p.${Constants.PRACTICE_ID}, i.${Constants.VOCABULARY_ID_ITEM}, " +
                    " p.${Constants.SHOTS} " +
                    " FROM ${Constants.PRACTICE_TABLE} p INNER JOIN ${Constants.ITEMS_VOCABULARY_TABLE} i " +
                    " ON p.${Constants.PRACTICE_VOCABULARY_ID_ITEM} = i.${Constants.VOCABULARY_ID_ITEM} " +
                    " WHERE p.${Constants.PRACTICE_VOCABULARY_ID_ITEM} = '$idItemVocabulary' "
        val data = db!!.rawQuery(query,null)
        return data
    }

    fun getItemsVocabularyFiltered(filterType: String, textSearch: String): Cursor {
        var args: Array<String>? = null
        var query =
            " SELECT i.${Constants.VOCABULARY_ID_ITEM}, i.${Constants.VOCABULARY_NAME_ITEM}, " +
            " i.${Constants.VOCABULARY_LEARNED_ITEM} " +
            " FROM ${Constants.ITEMS_VOCABULARY_TABLE} i INNER JOIN ${Constants.MEANINGS_TABLE} m " +
            " ON i.${Constants.VOCABULARY_ID_ITEM} = m.${Constants.MEANING_VOCABULARY_ID_ITEM} "
        if (filterType == "tolearn") query += " WHERE ${Constants.VOCABULARY_LEARNED_ITEM} = 0 "
        else if (filterType == "learned") query += " WHERE ${Constants.VOCABULARY_LEARNED_ITEM} = 1 "
        // if (textSearch != "") query += " AND ${Constants.VOCABULARY_NAME_ITEM} LIKE '%$textSearch%' "
        if (textSearch != "") {
            query += " AND( i.${Constants.VOCABULARY_NAME_ITEM} LIKE ? "
            query += " OR m.${Constants.MEANING_DESC_ONE} LIKE ? )"
            args = arrayOf("%$textSearch%", "%$textSearch%")
        }
        query += " GROUP BY i.${Constants.VOCABULARY_ID_ITEM} "
        // firstParameter.- Is the Query
        // secondParameter.- Is an array of strings with [args] to match with each ? in Query
        // the number types must be as string as well!!!!
        // this array must be ordered according the ?, ?...
        // (firstParameter, secondParameter)
        val data = db!!.rawQuery(query, args)
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
                    "${Constants.MEANING_DESC_TWO} TEXT NOT NULL, " +
                    "${Constants.MEANING_TYPE} TEXT NOT NULL )"
            )
            db.execSQL(
                "CREATE TABLE ${Constants.PRACTICE_TABLE} (" +
                    "${Constants.PRACTICE_ID} TEXT PRIMARY KEY, " +
                    "${Constants.PRACTICE_VOCABULARY_ID_ITEM} TEXT NOT NULL, " +
                    "${Constants.SHOTS} INTEGER )"
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS ${Constants.ITEMS_VOCABULARY_TABLE}")
            db.execSQL("DROP TABLE IF EXISTS ${Constants.MEANINGS_TABLE}")
            db.execSQL("DROP TABLE IF EXISTS ${Constants.PRACTICE_TABLE}")
            onCreate(db)
        }
    }
}