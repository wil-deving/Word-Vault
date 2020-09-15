package com.gmail.wil.myownvocabulary.ui

import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.gmail.wil.myownvocabulary.R
import com.gmail.wil.myownvocabulary.db.DatabaseAdapter
import com.gmail.wil.myownvocabulary.managers.messArrayToList
import com.gmail.wil.myownvocabulary.model.ItemPractice
import com.gmail.wil.myownvocabulary.model.ItemVocabulary
import com.gmail.wil.myownvocabulary.model.Meaning
import kotlinx.android.synthetic.main.activity_training.*
import kotlinx.android.synthetic.main.item_training_meaning.view.*

class TrainingActivity : AppCompatActivity() {

    // Variables to connect DB
    private var db: DatabaseAdapter? = null

    // Variable to arm cardView into it
    private var MainLayout : LinearLayout? = null

    // Variable to get list of items to learn and going mapping
    private var ListItemsVocabulary = ArrayList<ItemVocabulary>()
    private var IndiceItemVocabulary = 0

    // Variable to Know which meaning was selected or deselected
    private var meaningsSelected = ArrayList<Boolean>()
    // Variable that counts meanings selected
    private var countMeaningsSelected = 0

    // Variable to set with the correct meanings of an item
    private var ListCorrectMeanings = ArrayList<Meaning>()

    // Variable that contains all of meanings corrects and wrongs
    private var AllMeaningsList = ArrayList<Meaning>()


    private var MEANINGSTOEVALUATE = ArrayList<Meaning>()

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

    fun getDataPracticeItem(idItemVoc: String) : ItemPractice {
        var itemPractice = ItemPractice("", "", 0)
        val cursor = db!!.getPracticeByItem(idItemVoc)
        if (cursor!!.moveToFirst()) {
            do {
                itemPractice = ItemPractice(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getInt(2)
                )
            } while (cursor.moveToNext())
        }
        return itemPractice
    }


    fun getMeaningsForItem () {
        // Clear global list of correct meanings
        ListCorrectMeanings.clear()
        MEANINGSTOEVALUATE.clear()
        countMeaningsSelected = 0

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
                        // Set the global list of correct meanings
                        ListCorrectMeanings = listMeanings
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
                        btnNextItemTraining!!.setEnabled(false)
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
            btnNextItemTraining!!.setEnabled(false)
        }
    }

    fun chargeAdapterList(list: ArrayList<Meaning>) {
        meaningsSelected.clear()
        AllMeaningsList.clear()
        MainLayout!!.removeAllViews()
        ivCorrectAnswers!!.setVisibility(View.INVISIBLE)
        var i = 0
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
            AllMeaningsList.add(it)
            i++
        }
    }

    fun onClickItem(position: Int, view: View) {
        if (!meaningsSelected[position]) {
            if (countMeaningsSelected < ListCorrectMeanings.size) {
                countMeaningsSelected++
                view.cvItemMeaningTraining.setCardBackgroundColor(Color.CYAN)
                meaningsSelected[position] = true
                MEANINGSTOEVALUATE.add(AllMeaningsList[position])
            } else {
                Toast.makeText(this,
                "Solo puede seleccionar ${ListCorrectMeanings.size} Opciones",
                Toast.LENGTH_SHORT).show()
            }
        } else {
            // Delete item when is diselected
            var indiceMeaning = -1
            for ((index, item) in MEANINGSTOEVALUATE.withIndex()) {
                if (AllMeaningsList[position].id_meaning == item.id_meaning) {
                    indiceMeaning = index
                }
            }
            if (indiceMeaning >= 0) MEANINGSTOEVALUATE.removeAt(indiceMeaning)
            // Visual Changes
            view.cvItemMeaningTraining.setCardBackgroundColor(Color.WHITE)
            countMeaningsSelected--
            meaningsSelected[position] = false
        }
    }

    fun nextItemVocabulary (view: View) {
        val timeToNext: Long = 1500
        if (countMeaningsSelected == ListCorrectMeanings.size) {
            if (IndiceItemVocabulary < (ListItemsVocabulary.size - 1)) {
                btnNextItemTraining!!.setEnabled(false)
                changesInDataPractice()
                Handler().postDelayed({
                    IndiceItemVocabulary++
                    getMeaningsForItem()
                    btnNextItemTraining!!.setEnabled(true)
                }, timeToNext)
            } else {
                btnNextItemTraining!!.setEnabled(false)
                changesInDataPractice()
                Handler().postDelayed({
                    finish()
                }, timeToNext)
            }
        } else {
            Toast.makeText(this,
                "Falta Seleccionar Opciones",
                Toast.LENGTH_SHORT).show()
        }
    }

    // This function makes logic that makes changes in DB when we practice
    fun changesInDataPractice() {
        // this varible get a boolean if we choice options correct or not
        val isCorrect =
            validateCorrectMeaningsSelected(ListCorrectMeanings, MEANINGSTOEVALUATE)
        // if we choice correct options
        if (isCorrect) {
            // show correct image view
            llContentAnswer!!.setVisibility(View.VISIBLE)
            ivCorrectAnswers!!.setImageResource(R.drawable.ic_baseline_check_circle_24)
            ivCorrectAnswers!!.setVisibility(View.VISIBLE)
            tvAnswerIs!!.text = "Correcto"
            // get data practice for this item vocabulary
            val practiceItem =
                getDataPracticeItem(ListItemsVocabulary[IndiceItemVocabulary].id_item)

            Toast.makeText(this,
                "shots: ${practiceItem.shots + 1}", Toast.LENGTH_SHORT).show()

            // if is the 5th time that we make correct options
            if ((practiceItem.shots + 1) >= 5) {
                // changes in DB shots in practice to 0 and this item will be marked as learned
                db!!.updateDataItemPractice(ListItemsVocabulary[IndiceItemVocabulary].id_item, 0)
                db!!.updateTypeItemVocabulary(ListItemsVocabulary[IndiceItemVocabulary].id_item, 1)
            } else {
                // if not only add 1 more time as correct practice
                db!!.updateDataItemPractice(ListItemsVocabulary[IndiceItemVocabulary].id_item,
                    (practiceItem.shots + 1))
            }
        } else {
            // if not choice correct options
            llContentAnswer!!.setVisibility(View.VISIBLE)
            ivCorrectAnswers!!.setImageResource(R.drawable.ic_baseline_cancel_24)
            ivCorrectAnswers!!.setVisibility(View.VISIBLE)
            tvAnswerIs!!.text = "Incorrecto"
            // Change shots to 0
            db!!.updateDataItemPractice(ListItemsVocabulary[IndiceItemVocabulary].id_item, 0)
        }
    }

    // This function compare two lists and validate if we selected correct options or not
    fun validateCorrectMeaningsSelected (correctMeanings: ArrayList<Meaning>,
                                         selectedMeanings: ArrayList<Meaning>) : Boolean {
        if (correctMeanings.size != selectedMeanings.size) return false
        else {
            for (meaningSelected in selectedMeanings) {
                if (!(meaningSelected in correctMeanings)) {
                    return false
                }
            }
        }
        return true
    }
}