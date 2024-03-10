package com.example.devoir4androidapi.departement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.devoir4androidapi.R
import com.example.devoir4androidapi.model.Departement

class AjoutDepartement : AppCompatActivity() {
    private lateinit var nomEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajout_departement)

        nomEditText = findViewById(R.id.editTextDepartmentName)

        findViewById<Button>(R.id.buttonSubmit).setOnClickListener {
            val nom = nomEditText.text.toString()

            if (nom.isNotEmpty()) {
                // Créer un objet JSON avec le nom du département
                val departement = Departement("", nom.toString())
                insertDepartment(departement)
            } else {
                Toast.makeText(this, "Veuillez saisir un nom de département", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun insertDepartment(departement: Departement) {
        val ENDPOINT = "http://192.168.50.101:3000/departement/"

        // Créer une requête POST avec StringRequest
        val request = object : StringRequest(
            Request.Method.POST, ENDPOINT,
            { response ->
                // Succès : le département a été ajouté avec succès sur le serveur
                Toast.makeText(this, "Département ajouté avec succès", Toast.LENGTH_SHORT).show()
                // Fermer l'activité d'ajout de département
                finish()
            },
            { error ->
                // Erreur : impossible d'ajouter le département sur le serveur
                Toast.makeText(this, "Erreur lors de l'ajout du département", Toast.LENGTH_SHORT).show()
                Log.e("INSERT_DEPARTMENT_ERROR", "Erreur lors de l'ajout du département: $error")
            }) {

            // Override de la méthode getParams() pour spécifier les paramètres de la requête
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                // Vérifier que les propriétés de l'étudiant ne sont pas nulles
                departement?.let {
                    // Ajouter les données de l'étudiant comme paramètres de la requête
                    it.nom?.let { nom ->
                        params["name"] = nom
                    }


                }

                return params
            }
        }

        // Ajouter la requête à la file d'attente de Volley pour l'exécuter
        Volley.newRequestQueue(this).add(request)
    }
}