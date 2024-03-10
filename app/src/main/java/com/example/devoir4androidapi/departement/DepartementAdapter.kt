package com.example.devoir4androidapi.departement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.devoir4androidapi.R
import com.example.devoir4androidapi.model.Departement


class DepartementAdapter(val departements:ArrayList<Departement>): RecyclerView.Adapter<DepartementAdapter.ContactViewHolder>() {

    class ContactViewHolder(vue: View): RecyclerView.ViewHolder(vue){
        val nom = vue.findViewById<TextView>(R.id.tvNomDep)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val vue =  LayoutInflater.from(parent.context).inflate(R.layout.item_departement,parent,false)
        return ContactViewHolder(vue)
    }

    override fun getItemCount(): Int {
        return departements.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val departement = departements.get(position)
        holder.nom.setText(departement.nom)
    }

}