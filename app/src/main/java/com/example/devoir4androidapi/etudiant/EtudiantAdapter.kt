package com.example.devoir4androidapi.etudiant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.devoir4androidapi.R
import com.example.devoir4androidapi.model.Etudiant

class EtudiantAdapter(val etudiants:ArrayList<Etudiant>): RecyclerView.Adapter<EtudiantAdapter.ContactViewHolder>() {

    class ContactViewHolder(vue: View): RecyclerView.ViewHolder(vue){
        val firstname = vue.findViewById<TextView>(R.id.tvPrenom)
        val lastname = vue.findViewById<TextView>(R.id.tvNom)
        val departement = vue.findViewById<TextView>(R.id.tvDepartement)
        val matricule = vue.findViewById<TextView>(R.id.tvMatricule)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val vue =  LayoutInflater.from(parent.context).inflate(R.layout.item_etudiant,parent,false)
        return ContactViewHolder(vue)
    }

    override fun getItemCount(): Int {
        return etudiants.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val etudiant = etudiants.get(position)
        holder.firstname.setText(etudiant.prenom)
        holder.lastname.setText(etudiant.nom)
        holder.departement.setText(etudiant.departement)
        holder.matricule.setText(etudiant.matricule)
    }

}