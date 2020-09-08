package com.gmail.wil.myownvocabulary.ui

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.SearchView
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
    private val ids = ArrayList<String>()

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
        chargeAdapterList(getDataItems(FilterList))
    }

    override fun onStop() {
        super.onStop()
        db!!.cerrar()
    }

    override fun onPause() {
        super.onPause()
        // to hide buttons of FAB
        // hideFAB()
        // STATUS = false
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
        ids.clear()
        adaptadorLista!!.eliminarTodo()
        if (list.size > 0) {
            tvAreThereData.setVisibility(View.INVISIBLE)
            for (item in list) {
                if (item.learned_item == 1) idResourceImage = R.drawable.ic_baseline_spellcheck_24
                else idResourceImage = R.drawable.ic_baseline_priority_high_24
                adaptadorLista!!.adicionarItem(idResourceImage, item.name_item, "")
                ids.add(item.id_item)
            }
        } else {
            tvAreThereData.setVisibility(View.VISIBLE)
        }
        adaptadorLista!!.notifyDataSetChanged()
    }

    // Function that make an intent to MeaningsListActivity when press an item from list
    override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        val intent = Intent(this, MeaningsListActivity::class.java)
        intent.putExtra("id_item_vocabulary", ids[i])
        startActivity(intent)
    }

    /* todo methods update and delete here */

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