package com.gmail.wil.myownvocabulary.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.gmail.wil.myownvocabulary.db.DatabaseAdapter
import com.gmail.wil.myownvocabulary.R
import com.gmail.wil.myownvocabulary.managers.randomAlphanumericString
import com.gmail.wil.myownvocabulary.model.ItemVocabulary
import kotlinx.android.synthetic.main.activity_add_meaning.*

class AddMeaningActivity : AppCompatActivity() {
    private var db: DatabaseAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meaning)

        db = DatabaseAdapter(this)
        db!!.abrir()
    }

    fun finalizar(view: View) {
        val idNewItemV = randomAlphanumericString()
        val itemVocabulary = ItemVocabulary(idNewItemV,
            etNameWord!!.text.toString(),
            0)
        db!!.addWord(itemVocabulary.id_item, itemVocabulary.name_item, itemVocabulary.learned_item)

        db!!.addMeaning(idNewItemV,
            etMeaningOne!!.text.toString(),
            etMeaningTwo!!.text.toString())

        finish()
    }

    fun saveMeaning () {
//        val desc1 = etMeaningOne!!.text.toString()
//        val desc2 = etMeaningTwo!!.text.toString()

        //Toast.makeText(this, "PREss", Toast.LENGTH_SHORT).show()
//
        //db!!.addMeaning("IdItem1", "Hola", "Chau")
        //val cursor = db!!.getMeaningsByItem(0)
//        if (cursor.moveToFirst()) {
//            do {
//                val meaning = cursor.getString(0)
//                tvRepeatMeaning.text = meaning
//            } while (cursor.moveToNext())
//        } else {
//            tvRepeatMeaning.text = "No hay nada"
//        }

        //finish()

    }

    override fun onStop() {
        super.onStop()
        db!!.cerrar()
    }
}