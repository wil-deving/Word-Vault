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

    private var firstSave = true
    private var idItemVoc = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meaning)

        db = DatabaseAdapter(this)
        db!!.abrir()
    }

    fun finalizar(view: View) {

        // validacion para campos
//        if (etNameWord.text.toString() != null && etNameWord.text.toString() != "") {
//            Toast.makeText(this, "ESTA BIEN", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "ESTA MAL", Toast.LENGTH_SHORT).show()
//        }


        // todo trabaja tranquilo pero modularizar
        if (firstSave) {
            // validar campo palabra
            val idNewItemV = randomAlphanumericString()
            val itemVocabulary = ItemVocabulary(idNewItemV,
                etNameWord!!.text.toString(),
                0)
            db!!.addWord(itemVocabulary.id_item, itemVocabulary.name_item, itemVocabulary.learned_item)

            val idNewMeaning = randomAlphanumericString()
            db!!.addMeaning(idNewMeaning, idNewItemV,
                etMeaningOne!!.text.toString(),
                etMeaningTwo!!.text.toString())
            finish()
        } else {
            finish()
        }


        //finish()
    }

    fun saveMeaning (view: View) {

        if (firstSave) {
            // solo la primera vez creara un codigo alfanumerico
            idItemVoc = randomAlphanumericString()

            // At first create a word
            val itemVocabulary = ItemVocabulary(idItemVoc,
                etNameWord!!.text.toString(),
                0)
            db!!.addWord(itemVocabulary.id_item, itemVocabulary.name_item,
                itemVocabulary.learned_item)

            firstSave = false
            // deshabilitar et de la palabra
            etNameWord.isEnabled = false
        }
        val idNewMeaning = randomAlphanumericString()
        db!!.addMeaning(idNewMeaning, idItemVoc,
            etMeaningOne!!.text.toString(),
            etMeaningTwo!!.text.toString())
        etMeaningOne.setText("")
        etMeaningTwo.setText("")

    }

    override fun onStop() {
        super.onStop()
        db!!.cerrar()
    }
}