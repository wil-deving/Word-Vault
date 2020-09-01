package com.gmail.wil.myownvocabulary.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.FrameLayout
import com.gmail.wil.myownvocabulary.R
import com.gmail.wil.myownvocabulary.db.DatabaseAdapter
import com.gmail.wil.myownvocabulary.listsAdapter.WordsListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fab_main_opt_layout.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    // Variables to list words
    private var adaptadorLista: WordsListAdapter? = null
    private val ids = ArrayList<Long>()

    // Variables to connect DB
    private var db: DatabaseAdapter? = null

    // Variables to animate FAB
    lateinit var show_fab_new_word: Animation
    lateinit var hide_fab_new_word: Animation
    lateinit var show_fab_training: Animation
    lateinit var hide_fab_training: Animation
    var STATUS = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // building list words
        lvVocabularyList!!.onItemClickListener = this
        adaptadorLista = WordsListAdapter(this)
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

        fab_new_word.setOnClickListener {
            val intent = Intent(this, AddMeaningActivity::class.java)
            startActivity(intent)
        }
        fab_training.setOnClickListener {
            val intent = Intent(this, TrainingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        db!!.abrir()
        cargarDatosLista()
    }

    fun cargarDatosLista() {
        /*tenemos un arraylist de id`s en ids, obtenemos toas las personas
        * devolvemos un cursos de todas las personas, ocultamos ll si encontro un registro
        * minimo, */
        ids.clear()
        adaptadorLista!!.eliminarTodo()

        val cursor = db!!.getAllWords()
        if (cursor.moveToFirst()) {
            tvDatos.setText("SI hay datos")
        } else {
            tvDatos.setText("NO hay datos")
        }

        adaptadorLista!!.adicionarItem("WWWWWW111", "WWWW")
        adaptadorLista!!.adicionarItem("WWWWWW111", "WWWW")
        adaptadorLista!!.adicionarItem("WWWWWW111", "WWWW")
        adaptadorLista!!.adicionarItem("WWWWWW111", "WWWW")
        adaptadorLista!!.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        db!!.cerrar()
    }

    override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        //val intent = Intent(this, DetalleActivity::class.java)
        //intent.putExtra("id", ids[i])
        //startActivity(intent)
    }

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