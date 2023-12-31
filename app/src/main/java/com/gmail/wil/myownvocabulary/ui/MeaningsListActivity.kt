package com.gmail.wil.myownvocabulary.ui

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gmail.wil.myownvocabulary.R
import com.gmail.wil.myownvocabulary.db.DatabaseAdapter
import com.gmail.wil.myownvocabulary.listsAdapter.MeaningsListAdapter
import com.gmail.wil.myownvocabulary.model.Meaning
import kotlinx.android.synthetic.main.activity_meanings_list.fabAddNewMeaning
import kotlinx.android.synthetic.main.activity_meanings_list.lvMeaningsList
import kotlinx.android.synthetic.main.activity_meanings_list.tvAreThereDataMeanings
import kotlinx.android.synthetic.main.activity_meanings_list.tvNameItemInListMeanings

class MeaningsListActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    // Varible to build list adapter
    private var adaptadorLista: MeaningsListAdapter? = null

    // List to save data from DB
    private val meaningsList = ArrayList<Meaning>()

    // Variables to connect DB
    private var db: DatabaseAdapter? = null

    // variable to know what item was selected
    var IdItemVocabulary: String = ""
    var NameItemVocabulary: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meanings_list)
        // building list words
        lvMeaningsList!!.onItemClickListener = this
        adaptadorLista = MeaningsListAdapter(this)
        lvMeaningsList!!.adapter = adaptadorLista
        registerForContextMenu(lvMeaningsList)

        // connect DB
        db = DatabaseAdapter(this)

        // get id item vocabulary selected
        IdItemVocabulary = intent.getStringExtra("id_item_vocabulary")
        NameItemVocabulary = intent.getStringExtra("name_item_cobulary")

        tvNameItemInListMeanings!!.text = NameItemVocabulary.toUpperCase()

        // Function that executas intent when press on FAB new add meaning
        fabAddNewMeaning.setOnClickListener {
            val intent = Intent(this, AddMeaningActivity::class.java)
            // Sent 3 extras to work in that activity
            intent.putExtra("id_item_vocabulary", IdItemVocabulary)
            intent.putExtra("name_item_vocabulary", NameItemVocabulary)
            intent.putExtra("new_meaning", true)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        db!!.abrir()
        cargarDatosLista(IdItemVocabulary)
    }

    override fun onStop() {
        super.onStop()
        db!!.cerrar()
    }

    // This function makes a query to DB and change list of meanings
    fun cargarDatosLista(idItemVoc: String) {
        meaningsList.clear()
        adaptadorLista!!.eliminarTodo()
        var numberMeaning = 0
        val cursor = db!!.getMeaningsByItem(idItemVoc)
        if (cursor.moveToFirst()) {
            do {
                numberMeaning++
                val meaning = Meaning(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
                )
                val shortMeaningType = getShortType(cursor.getString(4))
                adaptadorLista!!.adicionarItem(numberMeaning.toString(),
                    "${shortMeaningType}. ${meaning.original_description}")
                meaningsList.add(meaning)
            } while (cursor.moveToNext())
            var text = "${numberMeaning} Registros Encontrados"
            if (numberMeaning == 1) text = "$numberMeaning Registro Encontrado"
            tvAreThereDataMeanings.setText("$text")
            tvAreThereDataMeanings.setGravity(Gravity.RIGHT)
        } else {
            tvAreThereDataMeanings.setText("No se Encontaron Registros")
            tvAreThereDataMeanings.setGravity(Gravity.CENTER)
        }
        adaptadorLista!!.notifyDataSetChanged()
    }

    override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        val intent = Intent(this, AddMeaningActivity::class.java)
        intent.putExtra("id_meaning", meaningsList[i].id_meaning)
        intent.putExtra("desc_original", meaningsList[i].original_description)
        intent.putExtra("desc_secundary", meaningsList[i].secundary_description)
        intent.putExtra("meaning_type", meaningsList[i].meaningType)
        intent.putExtra("id_item_vocabulary", IdItemVocabulary)
        intent.putExtra("name_item_vocabulary", NameItemVocabulary)
        intent.putExtra("new_meaning", false)
        startActivity(intent)
    }

    // Inflate menu to delete an item
    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.options_meaning, menu)
    }

    // Method that happen when stay press on an item
    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val index = info.position
        when (item.itemId) {
            // Delete an item
            R.id.menu_eliminar_meaning -> {
                createDialogData("Eliminar Significado",
                    "Está seguro de Eliminar este Significado?",
                    meaningsList[index].id_meaning).show()
            }
        }
        return super.onContextItemSelected(item)
    }

    // Function create a Dialog
    fun createDialogData(title: String, message: String, idMeaning: String)
            : AlertDialog {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        //prohibimos que al hacer click en cualquier lugar de la pantalla el dialogo desaparezca
        alertDialog.setCancelable(false)
        //con este botonnext del dialgo saldremos del dialogo, es positivo asi que se ubicara a la derecha
        alertDialog.setPositiveButton("Aceptar"){ dialogInterface, i ->
            db!!.deleteMeaning(idMeaning)
            // update list of items
            cargarDatosLista(IdItemVocabulary)
        }
        //el botonnext neutral se ubica a la izquierda, esto por reglas de diseño
        alertDialog.setNeutralButton("Cancelar"){ dialogInterface, i ->
            // Toast.makeText(this, "Click en cancelar", Toast.LENGTH_SHORT).show()
        }
        return alertDialog.create()
    }

    private fun getShortType(type: String): String {
        when (type) {
            "Common" -> return "com"
            "Adjective" -> return "adj"
            "Noun" -> return "n"
            "Phrasal Verb" -> return "PHR V"
            "Verb" -> return "v"
            "Adverb" -> return "adv"
            "Preposition" -> return "prep"
            "Conjunction" -> return "con"
            "Expression" -> return "exp"
        }
        return "S/T"
    }
}