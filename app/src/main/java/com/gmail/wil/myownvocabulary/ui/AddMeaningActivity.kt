package com.gmail.wil.myownvocabulary.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gmail.wil.myownvocabulary.db.DatabaseAdapter
import com.gmail.wil.myownvocabulary.R
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
        val itemVocabulary = ItemVocabulary(etNameWord!!.text.toString(), 0)
        db!!.addWord(itemVocabulary.name_word, itemVocabulary.learned)
        finish()
    }

    override fun onStop() {
        super.onStop()
        db!!.cerrar()
    }
}