package com.gmail.wil.myownvocabulary.ui

import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.gmail.wil.myownvocabulary.R
import com.gmail.wil.myownvocabulary.db.DatabaseAdapter
import com.gmail.wil.myownvocabulary.listsAdapter.TrainingOptionsListAdapter
import com.gmail.wil.myownvocabulary.model.ItemVocabulary
import com.gmail.wil.myownvocabulary.model.Meaning
import kotlinx.android.synthetic.main.activity_training.*
import kotlinx.android.synthetic.main.item_training_meaning.view.*

class TrainingActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    // Variables to list words
    private var adaptadorLista: TrainingOptionsListAdapter? = null
    private val ListMeanings = ArrayList<Meaning>()

    // Variables to connect DB
    private var db: DatabaseAdapter? = null

    private var IndiceItemVocabulary = 0
    private var ListItemsVocabulary = ArrayList<ItemVocabulary>()

    var meaningsSelected = ArrayList<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)

        // building list words
        lvOptionsMeanings!!.onItemClickListener = this
        adaptadorLista = TrainingOptionsListAdapter(this)
        lvOptionsMeanings!!.adapter = adaptadorLista
        registerForContextMenu(lvOptionsMeanings)

        // connect DB
        db = DatabaseAdapter(this)

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

    // Method receive two parameters
    // filter tolearn or learned and makes a query to SQLite
    // textsearch text input in Search View also makes a query to SQLite
    fun getDataItems(listFilter: String) : ArrayList<ItemVocabulary> {
        var listItemsVocabulary = ArrayList<ItemVocabulary>()
        var cursor: Cursor? = null
        // if after want to get all data
        // if (listFilter == "all") cursor = db!!.getAllItemsVocabulary()
        cursor = db!!.getItemsVocabularyFiltered(listFilter, "")
        if (cursor!!.moveToFirst()) {
            do {
                val itemVocabulary = ItemVocabulary(cursor.getString(0),
                    cursor.getString(1), cursor.getInt(2))
                listItemsVocabulary.add(itemVocabulary)
            } while (cursor.moveToNext())
        }
        return listItemsVocabulary
    }

    fun getMeaningsForItem () {
        val itemVoc = ListItemsVocabulary[IndiceItemVocabulary]
        val listMeanings = getDataMeanings(itemVoc.id_item)
        chargeAdapterList(listMeanings)
    }

    fun getDataMeanings(idItemVoc: String) : ArrayList<Meaning> {
        var listMeanings = ArrayList<Meaning>()
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

    // This function builds list that recieve from getDataItems()
    fun chargeAdapterList(list: ArrayList<Meaning>) {
        ListMeanings.clear()
        meaningsSelected.clear()
        adaptadorLista!!.eliminarTodo()
        var i = 0
        if (list.size > 0) {
            list.forEach {
                i++
                adaptadorLista!!.adicionarItem(i.toString(), it.original_description)
                meaningsSelected.add(false)
                ListMeanings.add(it)
            }

        } else {

            // no hay significados para esta palabra

        }
        adaptadorLista!!.notifyDataSetChanged()
    }

    fun nextItemVocabulary (view: View) {
        IndiceItemVocabulary++
        getMeaningsForItem()
    }

    override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

        // TODO Hacer commit!!!!!!!!


        if (!meaningsSelected[i]) {
            adapterView.getChildAt(i).llItem.
            setBackgroundColor(Color.argb(100, 93, 250, 73))
            meaningsSelected[i] = true
        } else {
            adapterView.getChildAt(i).llItem.
            setBackgroundColor(Color.WHITE)
            meaningsSelected[i] = false
        }
    }
}