package com.gmail.wil.myownvocabulary.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.gmail.wil.myownvocabulary.R
import com.gmail.wil.myownvocabulary.db.DatabaseAdapter
import kotlinx.android.synthetic.main.activity_item_vocabulary_form.*

class ItemVocabularyFormActivity : AppCompatActivity() {

    // Variables to connect DB
    var db: DatabaseAdapter? = null
    // Variable that will receive a value from before activity in an extra
    var IdItemVocabulary = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_vocabulary_form)

        db = DatabaseAdapter(this)
        IdItemVocabulary = intent.getStringExtra("idItemV")
        // set etNewNameItemVocabulary with value received
        val oldNameItemVocabulary = intent.getStringExtra("nameItemV")
        etNewNameItemVocabulary!!.setText(oldNameItemVocabulary)

    }

    override fun onStart() {
        super.onStart()
        db!!.abrir()
    }

    override fun onStop() {
        super.onStop()
        db!!.cerrar()
    }

    fun saveNewItemVocabulary(view: View) {
        if (etNewNameItemVocabulary!!.text.toString() != null &&
            etNewNameItemVocabulary!!.text.toString() != "") {
            createDialogSaveData("Actualización de Item",
                "Está seguro de Actualizar esta Palabra o Expresión?").show()
        } else {
            Toast.makeText(this, "Debe llenar el Campo", Toast.LENGTH_LONG).show()
        }
    }

    fun cancel(view: View) {
        finish()
    }

    fun createDialogSaveData(title: String, message: String)
            : AlertDialog {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        //prohibimos que al hacer click en cualquier lugar de la pantalla el dialogo desaparezca
        alertDialog.setCancelable(false)
        //con este botonnext del dialgo saldremos del dialogo, es positivo asi que se ubicara a la derecha
        alertDialog.setPositiveButton("Aceptar"){ dialogInterface, i ->
            val newNameItemVocabulary = etNewNameItemVocabulary!!.text.toString()
            db!!.updateDataItemVocabulary(IdItemVocabulary, newNameItemVocabulary)
            finish()
        }
        //el botonnext neutral se ubica a la izquierda, esto por reglas de diseño
        alertDialog.setNeutralButton("Cancelar"){ dialogInterface, i ->
            // Toast.makeText(this, "Click en cancelar", Toast.LENGTH_SHORT).show()
        }
        return alertDialog.create()
    }

}