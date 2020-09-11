package com.gmail.wil.myownvocabulary.listsAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.gmail.wil.myownvocabulary.R

class TrainingOptionsListAdapter(contexto: Context) : BaseAdapter() {
    val inflater: LayoutInflater = LayoutInflater.from(contexto)
    // val numbersItem: ArrayList<String> = ArrayList()
    val meaningItem: ArrayList<String> = ArrayList()

    override fun getCount(): Int {
        return meaningItem.size
    }

    override fun getItem(i: Int): Any {
        return i
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var view = view
        val holder: ViewHolder
        if (view == null) {
            view = inflater.inflate(R.layout.item_training_meaning, null)
            holder = ViewHolder()
            // holder.tvNumberOption = view!!.findViewById(R.id.tvOptionNumberTraining)
            holder.tvMeaning = view!!.findViewById(R.id.tvMeaningTraining)
            view!!.setTag(holder)
        } else {
            holder = view!!.tag as ViewHolder
        }
        // holder.tvNumberOption!!.text = numbersItem[i]
        holder.tvMeaning!!.text = meaningItem[i]
        return view
    }

    internal class ViewHolder {
        var tvNumberOption: TextView? = null
        var tvMeaning: TextView? = null
    }

    fun adicionarItem(number: String, meaning: String = "") {
        // numbersItem.add("Opción " + number)
        meaningItem.add(meaning)
        //con este metodo adicionamos uno nuevo, y notificamos cambios en el adapter
        notifyDataSetChanged()
    }

    fun eliminarTodo() {
        // numbersItem.clear()
        meaningItem.clear()
        notifyDataSetChanged()
    }
}