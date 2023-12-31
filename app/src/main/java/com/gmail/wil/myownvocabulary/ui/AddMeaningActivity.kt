package com.gmail.wil.myownvocabulary.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.gmail.wil.myownvocabulary.db.DatabaseAdapter
import com.gmail.wil.myownvocabulary.R
import com.gmail.wil.myownvocabulary.managers.compareMeanings
import com.gmail.wil.myownvocabulary.managers.randomAlphanumericString
import com.gmail.wil.myownvocabulary.model.ItemVocabulary
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_meaning.*
import java.util.ArrayList

class AddMeaningActivity : AppCompatActivity() {
    // Variable to connect DB
    private var db: DatabaseAdapter? = null

    // Variable to create snackbar
    lateinit var linearLayout: LinearLayout

    // Variable that depend if is a new item and/or meaning
    private var FirstSaveItemVoc = true
    // Variable that change to true whether this activity is intent to update a meaning
    private var EditionMeaning = false
    // Variable that contains Id Item vocabulary general
    private var IdItemVoc = ""
    // Variable that contains meaning's value if EditionMeaning will be true
    private var IdMeaningToUpdate = ""

    private var meaningType = "Common"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meaning)

        // Assign value from layout
        linearLayout = findViewById(R.id.lnMainParent)

        // to connect to DB
        db = DatabaseAdapter(this)

        val spinner = findViewById<Spinner>(R.id.spnTermType)
        val lista = arrayOf("Common", "Adjective", "Noun", "Phrasal Verb", "Verb",
            "Adverb", "Preposition", "Conjunction", "Expression")
        meaningType = lista.first() // as default

        if (spinner != null) {
            val adapter = ArrayAdapter( this, android.R.layout.simple_spinner_item, lista)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    meaningType = lista[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }

        if (intent.extras != null) {
            // Is intent from List meanings or
            val addNewMeaningFromList = intent.getBooleanExtra("new_meaning",
                false)
            FirstSaveItemVoc = false
            IdItemVoc = intent.getStringExtra("id_item_vocabulary")
            val nameItemVocfromList = intent.getStringExtra("name_item_vocabulary")
            etNameItemVocabulary.setText(nameItemVocfromList)
            etNameItemVocabulary.isEnabled = false
            // If addNewMeaningFromList is false it wants to update the meaning
            if (!addNewMeaningFromList) {
                tvTitleView.setText("Actualizar Datos")
                EditionMeaning = true
                btnAnotherMeaning.isEnabled = false
                val idMeaning = intent.getStringExtra("id_meaning")
                val originDescMeaning = intent.getStringExtra("desc_original")
                val secundaryDescMeaning = intent.getStringExtra("desc_secundary")
                val meaningTypeToUpdate = intent.getStringExtra("meaning_type")
                val positionItemMeaningType = lista.indexOf(meaningTypeToUpdate)
                etDescOriginalMeaning.setText(originDescMeaning)
                etDescSecundaryMeaning.setText(secundaryDescMeaning)
                if (positionItemMeaningType != -1) {
                    spinner.setSelection(positionItemMeaningType)
                    meaningType = meaningTypeToUpdate
                }
                IdMeaningToUpdate = idMeaning
            }
            // If addNewMeaningFromList is true will keep normal flow
        } else {
            // It is to new Data
            EditionMeaning = false
        }
    }

    override fun onStart() {
        super.onStart()
        db!!.abrir()
    }

    override fun onStop() {
        super.onStop()
        db!!.cerrar()
    }

    // This function is executed when on press button finalizar
    fun saveAndFinish(view: View) {
        val descOriginalMeaning = etDescOriginalMeaning!!.text.toString()
        val descSecundaryMeaning = etDescSecundaryMeaning!!.text.toString()
        if (FirstSaveItemVoc) {
            if (validateFieldNameItemVoc() && validateFieldsMeaning()) {
                if (compareMeanings(descOriginalMeaning, descSecundaryMeaning)) {
                    val nameItemVoc = etNameItemVocabulary!!.text.toString()
                    if (!validateExistenceItemVocabulary(nameItemVoc)) {
                        createDialogSaveData("Guardar Nueva Palabra o Expresión",
                            "Está Seguro de Guardar esta Información?",
                            true).show()
                    } else {
                        showSnackBar("Esta palabra ya Existe!")
                    }
                } else {
                    showSnackBar("Los Significados deben ser Iguales o Parecerse")
                }
            } else {
                showSnackBar("Debe llenar los Campos")
            }
        } else {
            if (validateFieldNameItemVoc()) {
                if (validateFieldsMeaning()) {
                    if (compareMeanings(descOriginalMeaning, descSecundaryMeaning)) {
                        if (EditionMeaning) {
                            createDialogSaveData("Actualizar Significado",
                                "Está Seguro de Actualizar esta Información?",
                                true).show()
                        } else {
                            createDialogSaveData("Guardar Nuevo Significado",
                                "Está Seguro de Guardar esta Información?",
                                true).show()
                        }
                    } else {
                        showSnackBar("Los Significados deben ser Iguales o Parecerse")
                    }
                } else {
                    finish()
                }
            } else {
                showSnackBar("Debe llenar los Campos")
            }
        }
    }

    // This function is executed when on press button otro siginificado
    fun addAnotherMeaning (view: View) {
        val descOriginalMeaning = etDescOriginalMeaning!!.text.toString()
        val descSecundaryMeaning = etDescSecundaryMeaning!!.text.toString()
        Toast.makeText(this, meaningType, Toast.LENGTH_SHORT).show()
        if (FirstSaveItemVoc) {
            if (validateFieldNameItemVoc() && validateFieldsMeaning()) {
                if (compareMeanings(descOriginalMeaning, descSecundaryMeaning)) {
                    val nameItemVoc = etNameItemVocabulary!!.text.toString()
                    if (!validateExistenceItemVocabulary(nameItemVoc)) {
                        createDialogSaveData("Guardar Nueva Palabra o Expresión",
                            "Está Seguro de Guardar esta Información?",
                            false).show()
                    } else {
                        showSnackBar("Esta palabra ya Existe!")
                    }
                } else {
                    showSnackBar("Los Significados deben ser Iguales o Parecerse")
                }
            } else {
                showSnackBar("Debe llenar los Campos")
            }
        } else {
            if (validateFieldsMeaning()) {
                if (compareMeanings(descOriginalMeaning, descSecundaryMeaning)) {
                    createDialogSaveData("Guardar nuevo significado",
                        "Está Seguro de Guardar esta Información?",
                        false).show()
                } else {
                    showSnackBar("Los Significados deben ser Iguales o Parecerse")
                }
            } else {
                showSnackBar("Debe llenar los Campos")
            }
        }
    }

    // This function is executed when on press button cancelar
    fun cancel(view: View) {
        finish()
    }

    // This method saves An item vocabulary
    fun saveItemVocabulary(nameItemVoc: String) {
        // only first time
        IdItemVoc = randomAlphanumericString()
        // At first create a word
        val itemVocabulary = ItemVocabulary(IdItemVoc, nameItemVoc,0)
        // Create ITEMVOCABULARY
        db!!.addItemVocabulary(itemVocabulary.id_item, itemVocabulary.name_item,
            itemVocabulary.learned_item)
        // Create row to work after when we have to practice
        db!!.addDataPractice(randomAlphanumericString(), itemVocabulary.id_item)
    }

    // This method saves a meaning
    fun saveMeaning() {
        val idNewMeaning = randomAlphanumericString()
        val descOriginalMeaning = etDescOriginalMeaning!!.text.toString()
        val descSecundaryMeaning = etDescSecundaryMeaning!!.text.toString()
        db!!.addMeaning(idNewMeaning, IdItemVoc,
            descOriginalMeaning,
            descSecundaryMeaning,
            meaningType)
    }

    // This method updates a meaning
    fun updateMeaning() {
        val descOriginalMeaning = etDescOriginalMeaning!!.text.toString()
        val descSecundaryMeaning = etDescSecundaryMeaning!!.text.toString()
        db!!.updateMeaning(IdMeaningToUpdate, IdItemVoc,
            descOriginalMeaning,
            descSecundaryMeaning,
            meaningType)
    }

    // This method creates a dialog
    fun createDialogSaveData(title: String, message: String, isToFinish: Boolean = false)
            : AlertDialog {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        //prohibimos que al hacer click en cualquier lugar de la pantalla el dialogo desaparezca
        alertDialog.setCancelable(false)
        //con este botonnext del dialgo saldremos del dialogo, es positivo asi que se ubicara a la derecha
        alertDialog.setPositiveButton("Aceptar"){ dialogInterface, i ->
            // isToFinish contains origin of this method was called
            if (isToFinish) {
                // It's from btn finalizar
                if (FirstSaveItemVoc) {
                    // First save an item Vocabulary
                    val nameItemVoc = etNameItemVocabulary!!.text.toString()
                    saveItemVocabulary(nameItemVoc)
                    // Then save a meaning
                    saveMeaning()
                    finish()
                } else {
                    // If EditionMeaning is true is an update to a meaning
                    if (EditionMeaning) {
                        updateMeaning()
                    } else {
                        // It is a new Meaning
                        saveMeaning()
                    }
                    finish()
                }
            } else {
                // is from btn add another meaning
                if (FirstSaveItemVoc) {
                    // First save an item Vocabulary
                    val nameItemVoc = etNameItemVocabulary!!.text.toString()
                    saveItemVocabulary(nameItemVoc)
                    // Then save a meaning
                    saveMeaning()
                } else {
                    // Just save a meaning
                    saveMeaning()
                }
                // Change global variables and clear fields
                etNameItemVocabulary.isEnabled = false
                FirstSaveItemVoc = false
                clearFields("addAnotherMeaning")
            }
        }
        //el botonnext neutral se ubica a la izquierda, esto por reglas de diseño
        alertDialog.setNeutralButton("Cancelar"){ dialogInterface, i ->
            // Toast.makeText(this, "Click en cancelar", Toast.LENGTH_SHORT).show()
        }
        return alertDialog.create()
    }

    // This method creates and shows a Snackbar
    fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(linearLayout, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    // This method validates that field Name item not exists in DB
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
                    // If exists return true and break loop
                    flag = true
                    break
                }
            }
        }
        return flag
    }

    // This function validates that a field's value is not empty
    fun validateFieldNameItemVoc() :Boolean {
        if (etNameItemVocabulary.text.toString() == null ||
            etNameItemVocabulary.text.toString() == "") {
            return false
        }
        return true
    }

    // This function validates that field's values is not empty
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

    // This method clear fields when press on add another meaning
    fun clearFields(origin: String = "") {
        if (origin == "addAnotherMeaning") {
            etDescOriginalMeaning.setText("")
            etDescSecundaryMeaning.setText("")
            spnTermType.setSelection(0)
            meaningType = "Common"
        }
    }

}