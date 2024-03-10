package com.example.devoir4androidapi.etudiant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.devoir4androidapi.R
import com.example.devoir4androidapi.model.Departement
import com.example.devoir4androidapi.model.Etudiant
import org.json.JSONArray

class EditEtudiant : AppCompatActivity() {
    private lateinit var etudiantId: String
    private lateinit var matriculeEditText: EditText
    private lateinit var nomEditText: EditText
    private lateinit var prenomEditText: EditText
    private lateinit var spinnerDepartement: Spinner
    private lateinit var departementList: ArrayList<Departement>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_etudiant)

        val etudiant = intent.getParcelableExtra<Etudiant>("etudiant")
        etudiantId = etudiant?.id ?: ""
        val matricule = etudiant?.matricule ?: ""
        val nom = etudiant?.nom ?: ""
        val prenom = etudiant?.prenom ?: ""
        val departement = etudiant?.departement ?: ""

        matriculeEditText = findViewById(R.id.editTextMatricule)
        nomEditText = findViewById(R.id.editTextNom)
        prenomEditText = findViewById(R.id.editTextPrenom)
        spinnerDepartement = findViewById(R.id.spinnerDepartement)

        matriculeEditText.setText(matricule)
        nomEditText.setText(nom)
        prenomEditText.setText(prenom)

        // Charger les départements dans le Spinner
        chargerDepartements()
        departementList = ArrayList()
        // Sélectionner le département actuel de l'étudiant dans le Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ArrayList<String>())
        spinnerDepartement.adapter = adapter
        val index = departementList.indexOfFirst { it.nom == departement }
        if (index != -1) {
            spinnerDepartement.setSelection(index)
        }

        findViewById<Button>(R.id.buttonUpdate).setOnClickListener {
            val newMatricule = matriculeEditText.text.toString().trim()
            val newNom = nomEditText.text.toString().trim()
            val newPrenom = prenomEditText.text.toString().trim()
            val newDepartement = spinnerDepartement.selectedItem.toString()

            if (newMatricule.isNotEmpty() && newNom.isNotEmpty() && newPrenom.isNotEmpty()) {
                val departement = departementList[spinnerDepartement.selectedItemPosition].id ?: ""
                updateEtudiant(etudiantId, newMatricule, newNom, newPrenom, departement)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun chargerDepartements() {
        val ENDPOINT = "http://192.168.50.101:3000/departement/"

        val request = StringRequest(
            Request.Method.GET, ENDPOINT,
            { response ->
                departementList = ArrayList()
                val responseData = JSONArray(response)
                for (i in 0 until responseData.length()) {
                    val departementJson = responseData.getJSONObject(i)
                    val id = departementJson.getString("_id")
                    val nom = departementJson.getString("name")
                    departementList.add(Departement(id, nom))
                }

                val adapter = spinnerDepartement.adapter as ArrayAdapter<String>
                adapter.clear()
                adapter.addAll(departementList.map { it.nom })
                adapter.notifyDataSetChanged()
            },
            { error ->
                Log.e("GET_DEPARTEMENTS_ERROR", "Error loading departments: $error")
                Toast.makeText(this, "Error loading departments", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(this).add(request)
    }

    private fun updateEtudiant(id: String, matricule: String, nom: String, prenom: String, departement: String) {
        val ENDPOINT = "http://192.168.50.101:3000/etudiant/$id"
        val params = HashMap<String, String>()
        params["matricule"] = matricule
        params["firstname"] = nom
        params["lastname"] = prenom
        params["departement"] = departement

        val request = object : StringRequest(Request.Method.PUT, ENDPOINT,
            { response ->
                Toast.makeText(this, "Etudiant updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                Toast.makeText(this, "Error updating student: $error", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }
}
