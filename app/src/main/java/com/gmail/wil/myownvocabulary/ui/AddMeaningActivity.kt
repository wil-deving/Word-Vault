package com.gmail.wil.myownvocabulary.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.gmail.wil.myownvocabulary.db.DatabaseAdapter
import com.gmail.wil.myownvocabulary.R
import com.gmail.wil.myownvocabulary.managers.randomAlphanumericString
import com.gmail.wil.myownvocabulary.model.ItemVocabulary
import kotlinx.android.synthetic.main.activity_add_meaning.*

class AddMeaningActivity : AppCompatActivity() {
    private var db: DatabaseAdapter? = null

    private var FirstSaveItemVoc = true
    private var EditionMeaning = false
    private var IdItemVoc = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meaning)

        db = DatabaseAdapter(this)

    }

    override fun onStart() {
        super.onStart()
        db!!.abrir()
    }

    override fun onStop() {
        super.onStop()
        db!!.cerrar()
    }

    fun saveAndFinish(view: View) {
        if (FirstSaveItemVoc) {
            val isCorrectFieldNameItemVoc = validateFieldNameItemVoc()
            val isCorrectFieldsMeaning = validateFieldsMeaning()
            if (isCorrectFieldNameItemVoc && isCorrectFieldsMeaning) {
                createDialogSaveData("Guardar nuevo significado",
                    "Seguro de guardar significado?", true).show()
            } else {
                Toast.makeText(this, "Debe llenar los campos", Toast.LENGTH_SHORT).show()
            }
        } else {
           finish()
        }
    }

    fun addAnotherMeaning (view: View) {
        val isCorrectFieldsMeaning = validateFieldsMeaning()
        val isCorrectFieldNameItemVoc = validateFieldNameItemVoc()
        if (isCorrectFieldsMeaning && isCorrectFieldNameItemVoc) {
            createDialogSaveData("Guardar nuevo significado",
                "Seguro de guardar significado?").show()
        } else {
            Toast.makeText(this, "Debe llenar los campos", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveData(isToEnd: Boolean) {
        val nameItemVoc =etNameItemVocabulary!!.text.toString()
        if (FirstSaveItemVoc) {
            if (isToEnd) {
                if (validateFieldsMeaning()) {
                    val existItemVoc = validateExistenceItemVocabulary(nameItemVoc)
                    if (!existItemVoc) {
                        saveItemVocabulary(nameItemVoc)
                        saveMeaning()
                        finish()
                    } else {
                        Toast.makeText(this, "Esta palabra o expresión ya existe",
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Debe llenar los campos", Toast.LENGTH_SHORT).show()
                }
            } else {
                val existItemVoc = validateExistenceItemVocabulary(nameItemVoc)
                if (!existItemVoc) {
                    saveItemVocabulary(nameItemVoc)
                    saveMeaning()
                    FirstSaveItemVoc = false
                    // deshabilitar et de la palabra
                    etNameItemVocabulary.isEnabled = false
                    clearFields(false)
                }
            }
        } else {
            if (isToEnd) {
                if (validateFieldsMeaning()) {
                    saveMeaning()
                    finish()
                }
            } else {
                saveMeaning()
                clearFields(false)
            }
        }
    }

    fun saveItemVocabulary(nameItemVoc: String) {
        // solo la primera vez creara un codigo alfanumerico
        IdItemVoc = randomAlphanumericString()
        // At first create a word
        val itemVocabulary = ItemVocabulary(IdItemVoc, nameItemVoc,1)
        db!!.addWord(itemVocabulary.id_item, itemVocabulary.name_item,
            itemVocabulary.learned_item)
    }

    fun saveMeaning() {
        val idNewMeaning = randomAlphanumericString()
        val descOriginalMeaning =etDescOriginalMeaning!!.text.toString()
        val descSecundaryMeaning =etDescSecundaryMeaning!!.text.toString()
        db!!.addMeaning(idNewMeaning, IdItemVoc,
            descOriginalMeaning,
            descSecundaryMeaning)
    }

    fun createDialogSaveData(title: String, message: String, isToEnd: Boolean = false)
            : AlertDialog {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        //prohibimos que al hacer click en cualquier lugar de la pantalla el dialogo desaparezca
        alertDialog.setCancelable(false)
        //con este boton del dialgo saldremos del dialogo, es positivo asi que se ubicara a la derecha
        alertDialog.setPositiveButton("Aceptar"){ dialogInterface, i ->
            saveData(isToEnd)
        }
        //el boton neutral se ubica a la izquierda, esto por reglas de diseño
        alertDialog.setNeutralButton("Cancelar"){ dialogInterface, i ->
            // Toast.makeText(this, "Click en cancelar", Toast.LENGTH_SHORT).show()
        }
        return alertDialog.create()
    }

    fun cancel(view: View) {
        clearFields(true)
    }

    fun validateExistenceItemVocabulary(nameItemVoc: String) : Boolean {
        // si esta palabra hay en DB false sino true

        return false
    }

    fun validateFieldsMeaning() : Boolean {
        if (etDescOriginalMeaning.text.toString() == null ||
            etDescOriginalMeaning.text.toString() == "") {
            return false
        }
        if (etDescSecundaryMeaning.text.toString() == null ||
            etDescSecundaryMeaning.text.toString() == "") {
            return false
        }
        return true
    }

    fun validateFieldNameItemVoc() :Boolean {
        if (etNameItemVocabulary.text.toString() == null ||
            etNameItemVocabulary.text.toString() == "") {
            return false
        }
        return true
    }

    fun clearFields(fromCancel: Boolean = false) {
        if (fromCancel) {
            etNameItemVocabulary.setText("")
            etDescOriginalMeaning.setText("")
            etDescSecundaryMeaning.setText("")
        } else {
            etDescOriginalMeaning.setText("")
            etDescSecundaryMeaning.setText("")
        }
    }

}