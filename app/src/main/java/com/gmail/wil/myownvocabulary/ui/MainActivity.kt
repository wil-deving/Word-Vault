package com.gmail.wil.myownvocabulary.ui

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.gmail.wil.myownvocabulary.R
import com.gmail.wil.myownvocabulary.db.DatabaseAdapter
import com.gmail.wil.myownvocabulary.listsAdapter.VocabularyListAdapter
import com.gmail.wil.myownvocabulary.model.ItemVocabulary
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fab_main_opt_layout.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener,
    SearchView.OnQueryTextListener {
    // Variables to list words
    private var adaptadorLista: VocabularyListAdapter? = null
    private val listItems = ArrayList<ItemVocabulary>()

    // Variables to connect DB
    private var db: DatabaseAdapter? = null

    // Variables to animate FAB
    lateinit var show_fab_new_word: Animation
    lateinit var hide_fab_new_word: Animation
    lateinit var show_fab_training: Animation
    lateinit var hide_fab_training: Animation
    var STATUS = false

    // Variable to filter type items Vocabulary
    private var FilterList= "tolearn"

    // Variable to set from SearchView
    private var TextSearched = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // building list words
        lvVocabularyList!!.onItemClickListener = this
        adaptadorLista = VocabularyListAdapter(this)
        lvVocabularyList!!.adapter = adaptadorLista
        registerForContextMenu(lvVocabularyList)

        // connect DB
        db = DatabaseAdapter(this)

        // methods to animate FAB and its fabs
        show_fab_new_word = AnimationUtils.loadAnimation(applicationContext,
            R.anim.fab_new_word_show)
        hide_fab_new_word = AnimationUtils.loadAnimation(applicationContext,
            R.anim.fab_new_word_hide)
        show_fab_training = AnimationUtils.loadAnimation(applicationContext,
            R.anim.fab_training_show)
        hide_fab_training = AnimationUtils.loadAnimation(applicationContext,
            R.anim.fab_training_hide)
        fabMainOptions.setOnClickListener {
            if (!STATUS){
                expandFAB()
                STATUS = true
            }else{
                hideFAB()
                STATUS = false
            }
        }
        // actions in sub buttons de FAB
        fab_new_word.setOnClickListener {
            val intent = Intent(this, AddMeaningActivity::class.java)
            startActivity(intent)
        }
        fab_training.setOnClickListener {
            val intent = Intent(this, TrainingActivity::class.java)
            startActivity(intent)
        }

        // Methods to Radios
        rbToLearn.setOnClickListener {
            FilterList = "tolearn"
            onClickRadioButton(FilterList, TextSearched)
        }
        rbLearned.setOnClickListener {
            FilterList = "learned"
            onClickRadioButton(FilterList, TextSearched)
        }

        // Variable to access to Search View and its methods
        val editSearch = findViewById(R.id.svFindItemVoc) as SearchView
        editSearch.setOnQueryTextListener(this)
    }

    override fun onStart() {
        super.onStart()
        db!!.abrir()
        // it will start loading list items to learn
        chargeAdapterList(getDataItems(FilterList, TextSearched))
    }

    override fun onStop() {
        super.onStop()
        db!!.cerrar()
    }

    // Function when press on item on radio
    fun onClickRadioButton(typeItemsVoc: String, textSearch: String = "") {
        chargeAdapterList(getDataItems(typeItemsVoc, textSearch))
    }

    // Methods to search items vocabulary for a text input
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        TextSearched = ""
        if (newText != null && newText !== "") TextSearched = newText
        chargeAdapterList(getDataItems(FilterList, TextSearched))
        return false
    }

    // Method receive two parameters
    // filter tolearn or learned and makes a query to SQLite
    // textsearch text input in Search View also makes a query to SQLite
    fun getDataItems(listFilter: String, textSearch: String = "") : ArrayList<ItemVocabulary> {
        var listItemsVocabulary = ArrayList<ItemVocabulary>()
        var cursor: Cursor? = null
        // if after want to get all data
        // if (listFilter == "all") cursor = db!!.getAllItemsVocabulary()
        cursor = db!!.getItemsVocabularyFiltered(listFilter, textSearch)
        if (cursor!!.moveToFirst()) {
            do {
                val itemVocabulary = ItemVocabulary(cursor.getString(0),
                    cursor.getString(1), cursor.getInt(2))
                listItemsVocabulary.add(itemVocabulary)
            } while (cursor.moveToNext())
        }
        return listItemsVocabulary
    }

    // This function builds list that recieve from getDataItems()
    fun chargeAdapterList(list: ArrayList<ItemVocabulary>) {
        var idResourceImage = R.drawable.ic_baseline_spellcheck_24
        listItems.clear()
        adaptadorLista!!.eliminarTodo()
        if (list.size > 0) {
            var text = "${list.size} Registros Encontrados"
            if (list.size == 1) text = "${list.size} Registro Encontrado"
            tvAreThereData.setText("$text")
            tvAreThereData.setGravity(Gravity.RIGHT)
            for (item in list) {
                if (item.learned_item == 1) idResourceImage = R.drawable.ic_baseline_spellcheck_24
                else idResourceImage = R.drawable.ic_baseline_priority_high_24
                adaptadorLista!!.adicionarItem(idResourceImage, item.name_item, "")
                listItems.add(ItemVocabulary(item.id_item, item.name_item, item.learned_item))
            }
        } else {
            tvAreThereData.setText("No se Encontaron Registros")
            tvAreThereData.setGravity(Gravity.CENTER)
        }
        adaptadorLista!!.notifyDataSetChanged()
    }

    // Function that make an intent to MeaningsListActivity when press an item from list
    override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        val intent = Intent(this, MeaningsListActivity::class.java)
        intent.putExtra("id_item_vocabulary", listItems[i].id_item)
        intent.putExtra("name_item_cobulary", listItems[i].name_item)
        startActivity(intent)
    }

    // Inflate menu to update, delete or sent to learn an item
    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        // If radio is checked to learn inflate menu without sent to learn
        if (FilterList == "tolearn") {
            menuInflater.inflate(R.menu.options_item_to_learn, menu)
        } else if (FilterList == "learned") {
            menuInflater.inflate(R.menu.options_item_learned, menu)
        }
    }

    // Method that happen when stay press on an item
    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val index = info.position
        when (item.itemId) {
            // Change an item from to learn to learned
            R.id.menu_send_item_voc_to_learn -> {
                createDialogSaveData("Cambiar Item a No Aprendidos",
                    "Está seguro de mover esta Palabra o Expresión a No Aprendidos?",
                    "sendItemToLearn", listItems[index].id_item).show()
            }
            // Update an item
            R.id.menu_editar_item_voc -> {
                val intent = Intent(this, ItemVocabularyFormActivity::class.java)
                intent.putExtra("idItemV", listItems[index].id_item)
                intent.putExtra("nameItemV", listItems[index].name_item)
                startActivity(intent)
            }
            // Delete an item
            R.id.menu_eliminar_item_voc -> {
                createDialogSaveData("Eliminar Item",
                    "Está seguro de Eliminar esta Palabra o Expresión?",
                    "deleteItem", listItems[index].id_item).show()
            }
        }
        return super.onContextItemSelected(item)
    }

    // Function create a Dialog
    fun createDialogSaveData(title: String, message: String, origin: String, idItemV: String)
            : AlertDialog {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        //prohibimos que al hacer click en cualquier lugar de la pantalla el dialogo desaparezca
        alertDialog.setCancelable(false)
        //con este botonnext del dialgo saldremos del dialogo, es positivo asi que se ubicara a la derecha
        alertDialog.setPositiveButton("Aceptar"){ dialogInterface, i ->
            // to change or delete
            if (origin == "sendItemToLearn") {
                db!!.updateTypeItemVocabulary(idItemV, 0)
            } else if (origin == "deleteItem") {
                // Delete item vocabulary, its meanings and its practices
                db!!.deleteItemVocabulary(idItemV)
                db!!.deleteMeaningsByItem(idItemV)
                db!!.deletePracticeByItem(idItemV)
            }
            // update list of items
            chargeAdapterList(getDataItems(FilterList, TextSearched))
        }
        //el botonnext neutral se ubica a la izquierda, esto por reglas de diseño
        alertDialog.setNeutralButton("Cancelar"){ dialogInterface, i ->
            // Toast.makeText(this, "Click en cancelar", Toast.LENGTH_SHORT).show()
        }
        return alertDialog.create()
    }


    // Functions to hide or show buttons of FAB
    private fun expandFAB() {
        val layoutParams = fab_new_word.layoutParams as FrameLayout.LayoutParams
        layoutParams.rightMargin += (fab_new_word.width * 0.25).toInt()
        layoutParams.bottomMargin += (fab_new_word.height * 1.7).toInt()
        fab_new_word.layoutParams = layoutParams
        fab_new_word.startAnimation(show_fab_new_word)
        fab_new_word.isClickable = true

        val layoutParams2 = fab_training.layoutParams as FrameLayout.LayoutParams
        layoutParams2.rightMargin += (fab_training.width * 1.3).toInt()
        layoutParams2.bottomMargin += (fab_training.height * 1.3).toInt()
        fab_training.layoutParams = layoutParams2
        fab_training.startAnimation(show_fab_training)
        fab_training.isClickable = true
    }

    private fun hideFAB() {
        val layoutParams = fab_new_word.layoutParams as FrameLayout.LayoutParams
        layoutParams.rightMargin -= (fab_new_word.width * 0.25).toInt()
        layoutParams.bottomMargin -= (fab_new_word.height * 1.7).toInt()
        fab_new_word.layoutParams = layoutParams
        fab_new_word.startAnimation(hide_fab_new_word)
        fab_new_word.isClickable = false

        val layoutParams2 = fab_training.layoutParams as FrameLayout.LayoutParams
        layoutParams2.rightMargin -= (fab_training.width * 1.3).toInt()
        layoutParams2.bottomMargin -= (fab_training.height * 1.3).toInt()
        fab_training.layoutParams = layoutParams2
        fab_training.startAnimation(hide_fab_training)
        fab_training.isClickable = false
    }
}