package com.gmail.wil.myownvocabulary.listsAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.gmail.wil.myownvocabulary.R

class MeaningsListAdapter(contexto: Context) : BaseAdapter() {
    val inflater: LayoutInflater = LayoutInflater.from(contexto)
    val numbersItem: ArrayList<String> = ArrayList()
    val meaningItem: ArrayList<String> = ArrayList()

    override fun getCount(): Int {
        return numbersItem.size
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
            view = inflater.inflate(R.layout.item_meaning_list, null)
            holder = ViewHolder()
            holder.tvNumber = view!!.findViewById(R.id.tvNumMeaning)
            holder.tvMeaning = view!!.findViewById(R.id.tvMeaning)
            view!!.setTag(holder)
        } else {
            holder = view!!.tag as ViewHolder
        }
        holder.tvNumber!!.text = numbersItem[i]
        holder.tvMeaning!!.text = meaningItem[i]
        return view
    }

    internal class ViewHolder {
        var tvNumber: TextView? = null
        var tvMeaning: TextView? = null
    }

    fun adicionarItem(number: String, meaning: String = "") {
        numbersItem.add(number + ".")
        meaningItem.add(meaning)
        //con este metodo adicionamos uno nuevo, y notificamos cambios en el adapter
        notifyDataSetChanged()
    }

    fun eliminarTodo() {
        numbersItem.clear()
        meaningItem.clear()
        notifyDataSetChanged()
    }
}