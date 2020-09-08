package com.gmail.wil.myownvocabulary.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.gmail.wil.myownvocabulary.db.DatabaseAdapter
import com.gmail.wil.myownvocabulary.R
import com.gmail.wil.myownvocabulary.managers.compareMeanings
import com.gmail.wil.myownvocabulary.managers.randomAlphanumericString
import com.gmail.wil.myownvocabulary.model.ItemVocabulary
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_meaning.*
import java.util.ArrayList

class AddMeaningActivity : AppCompatActivity() {
    private var db: DatabaseAdapter? = null

    //lateinit var coord: CoordinatorLayout
    lateinit var linearLayout: LinearLayout

    private var FirstSaveItemVoc = true
    private var EditionMeaning = false
    private var IdItemVoc = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meaning)

        linearLayout = findViewById(R.id.lnMainParent)

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
        val descOriginalMeaning = etDescOriginalMeaning!!.text.toString()
        val descSecundaryMeaning = etDescSecundaryMeaning!!.text.toString()
        if (FirstSaveItemVoc) {
            if (validateFieldNameItemVoc() && validateFieldsMeaning()) {
                if (compareMeanings(descOriginalMeaning, descSecundaryMeaning)) {
                    val nameItemVoc = etNameItemVocabulary!!.text.toString()
                    if (!validateExistenceItemVocabulary(nameItemVoc)) {
                        createDialogSaveData("Guardar nueva Palabra o Expresión",
                            "Está seguro de guardar esta información?",
                            true).show()
                    } else {
                        showSnackBar("Esta palabra ya existe!")
                    }
                } else {
                    showSnackBar("Los Significados deben ser iguales o parecerse")
                }
            } else {
                showSnackBar("Debe llenar los campos")
            }
        } else {
            if (validateFieldNameItemVoc()) {
                if (validateFieldsMeaning()) {
                    if (compareMeanings(descOriginalMeaning, descSecundaryMeaning)) {
                        createDialogSaveData("Guardar nuevo significado",
                            "Está seguro de guardar esta información?",
                            true).show()
                    } else {
                        showSnackBar("Los Significados deben ser iguales o parecerse")
                    }
                } else {
                    finish()
                }
            } else {
                showSnackBar("Debe llenar los campos")
            }
        }
    }

    fun addAnotherMeaning (view: View) {
        val descOriginalMeaning = etDescOriginalMeaning!!.text.toString()
        val descSecundaryMeaning = etDescSecundaryMeaning!!.text.toString()
        if (FirstSaveItemVoc) {
            if (validateFieldNameItemVoc() && validateFieldsMeaning()) {
                if (compareMeanings(descOriginalMeaning, descSecundaryMeaning)) {
                    val nameItemVoc = etNameItemVocabulary!!.text.toString()
                    if (!validateExistenceItemVocabulary(nameItemVoc)) {
                        createDialogSaveData("Guardar nueva Palabra o Expresión",
                            "Está seguro de guardar esta información?",
                            false).show()
                    } else {
                        showSnackBar("Esta palabra ya existe!")
                    }
                } else {
                    showSnackBar("Los Significados deben ser iguales o parecerse")
                }
            } else {
                showSnackBar("Debe llenar los campos")
            }
        } else {
            if (validateFieldsMeaning()) {
                if (compareMeanings(descOriginalMeaning, descSecundaryMeaning)) {
                    createDialogSaveData("Guardar nuevo significado",
                        "Está seguro de guardar esta información?",
                        false).show()
                } else {
                    showSnackBar("Los Significados deben ser iguales o parecerse")
                }
            } else {
                showSnackBar("Debe llenar los campos")
            }
        }
    }

    fun cancel(view: View) {
        finish()
    }

    fun saveItemVocabulary(nameItemVoc: String) {
        // solo la primera vez creara un codigo alfanumerico
        IdItemVoc = randomAlphanumericString()
        // At first create a word
        val itemVocabulary = ItemVocabulary(IdItemVoc, nameItemVoc,0)
        db!!.addItemVocabulary(itemVocabulary.id_item, itemVocabulary.name_item,
            itemVocabulary.learned_item)
    }

    fun saveMeaning() {
        val idNewMeaning = randomAlphanumericString()
        val descOriginalMeaning = etDescOriginalMeaning!!.text.toString()
        val descSecundaryMeaning = etDescSecundaryMeaning!!.text.toString()
        db!!.addMeaning(idNewMeaning, IdItemVoc,
            descOriginalMeaning,
            descSecundaryMeaning)
    }

    fun createDialogSaveData(title: String, message: String, isToFinish: Boolean = false)
            : AlertDialog {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        //prohibimos que al hacer click en cualquier lugar de la pantalla el dialogo desaparezca
        alertDialog.setCancelable(false)
        //con este boton del dialgo saldremos del dialogo, es positivo asi que se ubicara a la derecha
        alertDialog.setPositiveButton("Aceptar"){ dialogInterface, i ->
            if (isToFinish) {
                if (FirstSaveItemVoc) {
                    val nameItemVoc = etNameItemVocabulary!!.text.toString()
                    saveItemVocabulary(nameItemVoc)
                    saveMeaning()
                    finish()
                } else {
                    saveMeaning()
                    finish()
                }
            } else {
                if (FirstSaveItemVoc) {
                    val nameItemVoc = etNameItemVocabulary!!.text.toString()
                    saveItemVocabulary(nameItemVoc)
                    saveMeaning()
                } else {
                    saveMeaning()
                }
                etNameItemVocabulary.isEnabled = false
                FirstSaveItemVoc = false
                clearFields("addAnotherMeaning")
            }
        }
        //el boton neutral se ubica a la izquierda, esto por reglas de diseño
        alertDialog.setNeutralButton("Cancelar"){ dialogInterface, i ->
            // Toast.makeText(this, "Click en cancelar", Toast.LENGTH_SHORT).show()
        }
        return alertDialog.create()
    }

    fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(linearLayout, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    fun validateExistenceItemVocabulary(nameItemVoc: String) : Boolean {
        // si esta palabra hay en DB false sino true
        var flag = false
        val arrayDB = ArrayList<String>()
        val cursor = db!!.getItemsLookLike(nameItemVoc.trim())
        if (cursor!!.moveToFirst()) {
            do {
                arrayDB.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        if (arrayDB.size > 0) {
            for (itemDB in arrayDB) {
                if (itemDB.toUpperCase() == nameItemVoc.trim().toUpperCase()) {
                    flag = true
                    break
                }
            }
        }
        return flag
    }

    fun validateFieldNameItemVoc() :Boolean {
        if (etNameItemVocabulary.text.toString() == null ||
            etNameItemVocabulary.text.toString() == "") {
            return false
        }
        return true
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

    fun clearFields(origin: String = "") {
        if (origin == "addAnotherMeaning") {
            etDescOriginalMeaning.setText("")
            etDescSecundaryMeaning.setText("")
        }
    }

}