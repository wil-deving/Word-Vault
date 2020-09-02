package com.gmail.wil.myownvocabulary.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.gmail.wil.myownvocabulary.R
import com.gmail.wil.myownvocabulary.db.DatabaseAdapter
import com.gmail.wil.myownvocabulary.listsAdapter.MeaningsListAdapter
import kotlinx.android.synthetic.main.activity_meanings_list.*

class MeaningsListActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var adaptadorLista: MeaningsListAdapter? = null
    private val ids = ArrayList<String>()

    // Variables to connect DB
    private var db: DatabaseAdapter? = null

    // variable to know what item was selected
    var idItemVocabulary: String = ""

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
        idItemVocabulary = intent.getStringExtra("id_item_vocabulary")


    }

    override fun onStart() {
        super.onStart()
        db!!.abrir()
        //chargeAdapterList(getDataItems("all"))

        cargarDatosLista(idItemVocabulary)
    }

    override fun onStop() {
        super.onStop()
        db!!.cerrar()
    }

    fun cargarDatosLista(id: String) {
        ids.clear()
        adaptadorLista!!.eliminarTodo()
        var numberItem = 0
        val cursor = db!!.getMeaningsByItem(id)
        if (cursor.moveToFirst()) {
            tvDatosMean.text = "Hay datos"
            do {
                numberItem++
                val descriptionMeaning = cursor.getString(0)
                adaptadorLista!!.adicionarItem(numberItem.toString(), descriptionMeaning)

            } while (cursor.moveToNext())
        } else {
            tvDatosMean.text = "No Hay datos"
        }
        adaptadorLista!!.notifyDataSetChanged()
    }

    override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        //val intent = Intent(this, DetalleActivity::class.java)
        //intent.putExtra("id", ids[i])
        //startActivity(intent)
    }
}