package com.gmail.wil.myownvocabulary.ui

import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import com.gmail.wil.myownvocabulary.R
import com.gmail.wil.myownvocabulary.db.DatabaseAdapter
import com.gmail.wil.myownvocabulary.managers.messArrayToList
import com.gmail.wil.myownvocabulary.model.ItemVocabulary
import com.gmail.wil.myownvocabulary.model.Meaning
import kotlinx.android.synthetic.main.activity_training.*
import kotlinx.android.synthetic.main.item_training_meaning.view.*

class TrainingActivity : AppCompatActivity() {

    // Variables to connect DB
    private var db: DatabaseAdapter? = null

    // Variable to arm cardView into it
    private var MainLayout : LinearLayout? = null

    private var IndiceItemVocabulary = 0
    private var ListItemsVocabulary = ArrayList<ItemVocabulary>()

    private val ListMeanings = ArrayList<Meaning>()
    private var meaningsSelected = ArrayList<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)

        // connect DB
        db = DatabaseAdapter(this)

        // get layout linear
        MainLayout = findViewById(R.id.llMainMeaningsTaining) as LinearLayout

    }

    override fun onStart() {
        super.onStart()
        db!!.abrir()
        // get data items vocabulary
        ListItemsVocabulary = getDataItems("tolearn")
        // execute method that charge data meanings by id item and charge list
        getMeaningsForItem()
    }

    override fun onStop() {
        super.onStop()
        db!!.cerrar()
    }

    fun getDataItems(listFilter: String) : ArrayList<ItemVocabulary> {
        val listItemsVocabulary = ArrayList<ItemVocabulary>()
        val cursor = db!!.getItemsVocabularyFiltered(listFilter, "")
        if (cursor!!.moveToFirst()) {
            do {
                val itemVocabulary = ItemVocabulary(cursor.getString(0),
                    cursor.getString(1), cursor.getInt(2))
                listItemsVocabulary.add(itemVocabulary)
            } while (cursor.moveToNext())
        }
        return listItemsVocabulary
    }

    fun getDataMeanings(idItemVoc: String) : ArrayList<Meaning> {
        val listMeanings = ArrayList<Meaning>()
        var cursor: Cursor? = null
        cursor = db!!.getMeaningsByItem(idItemVoc)
        if (cursor!!.moveToFirst()) {
            do {
                val meaning = Meaning(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
                )
                listMeanings.add(meaning)
            } while (cursor.moveToNext())
        }
        return listMeanings
    }

    fun getDataWrongMeanings(idItemVoc: String) : ArrayList<Meaning> {
        val listMeanings = ArrayList<Meaning>()
        //TODO traer los malos
        val cursor = db!!.getDistinctMeaningsByItem(idItemVoc)
        if (cursor!!.moveToFirst()) {
            do {
                val meaning = Meaning(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
                )
                listMeanings.add(meaning)
            } while (cursor.moveToNext())
        }
        return listMeanings
    }


    fun getMeaningsForItem () {
        // Seccion para limpiar data global corrects and wrongs


        if (ListItemsVocabulary.size > 0) {
            if (IndiceItemVocabulary < ListItemsVocabulary.size) {
                val itemVoc = ListItemsVocabulary[IndiceItemVocabulary]
                // to get data correct meanigs for this item
                val listMeanings = getDataMeanings(itemVoc.id_item)
                // to get data wrong meanigs for this item
                val listWrongMeanings = getDataWrongMeanings(itemVoc.id_item)
                // If is the last or not item to practice
                if (IndiceItemVocabulary < (ListItemsVocabulary.size - 1))
                    btnNextItemTraining!!.text = "Siguiente"
                else btnNextItemTraining!!.text = "Finalizar"
                if (listMeanings.size > 0) {
                    if (listWrongMeanings.size > 0) {
                        // Mix data and get data
                        val finalList = messArrayToList(listMeanings, listWrongMeanings)
                        tvItemHead!!.text = itemVoc.name_item
                        tvItemHead!!.setVisibility(View.VISIBLE)
                        tvAreThereDataMeaningsTrain!!.text =
                            "Seleccione ${listMeanings.size} Opciones"
                        tvAreThereDataMeaningsTrain!!.setVisibility(View.VISIBLE)
                        chargeAdapterList(finalList)
                    } else {
                        // there are not other meanings to start practicing
                        tvAreThereDataMeaningsTrain!!.text = "No hay Información para Practicar"
                        btnNextItemTraining!!.text = "Salir"
                    }
                } else {
                    // to follow to next item
                    IndiceItemVocabulary++
                    getMeaningsForItem()
                }
            } else {
                // to end activity because there is no more items to practice
                finish()
            }
        } else {
            // When there are not data in items vocabulary
            tvAreThereDataMeaningsTrain!!.text = "No Existen Palabras o Expresiones para Practicar"
            btnNextItemTraining!!.text = "Salir"
        }
    }

    fun chargeAdapterList(list: ArrayList<Meaning>) {
        ListMeanings.clear()
        meaningsSelected.clear()
        MainLayout!!.removeAllViews()
        var i = 0
        if (list.size > 0) {
            list.forEach {
                val itemView = LayoutInflater.from(this).
                    inflate(R.layout.item_training_meaning, MainLayout, false)
                itemView.id = i
                itemView!!.tvMeaningTraining.text = it.original_description
                itemView!!.cvItemMeaningTraining.setOnClickListener {
                    onClickItem(itemView.id, itemView)
                }
                MainLayout!!.addView(itemView)
                meaningsSelected.add(false)
                ListMeanings.add(it)
                i++
            }
        } else {

            // TODO tal vez no requiera validacion en esta parte
            // no hay significados para esta palabra
        }
    }

    fun onClickItem(position: Int, view: View) {
        // Toast.makeText(this, "Presiono $position", Toast.LENGTH_SHORT).show()
        if (!meaningsSelected[position]) {
            view.cvItemMeaningTraining.setCardBackgroundColor(Color.CYAN)
            meaningsSelected[position] = true

        } else {
            view.cvItemMeaningTraining.setCardBackgroundColor(Color.WHITE)
            meaningsSelected[position] = false
        }
    }

    fun nextItemVocabulary (view: View) {
        // Toast.makeText(this, "PRESS", Toast.LENGTH_SHORT).show()

        if (IndiceItemVocabulary < (ListItemsVocabulary.size - 1)) {
            // logica para practica


            IndiceItemVocabulary++
            getMeaningsForItem()

        } else {
            // logica para practica
            finish()
        }

    }


    // TODO COMMITEAR!!!!!!!!!!!!!!!!!
}