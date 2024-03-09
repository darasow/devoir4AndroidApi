package com.example.devoir4androidapi

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.devoir4androidapi.model.Departement
import com.example.devoir4androidapi.model.Etudiant
import org.json.JSONArray

class AjouterEtudiant : AppCompatActivity() {
    lateinit var departementList:ArrayList<Departement>
    private lateinit var nomEditText: EditText
    private lateinit var prenomEditText: EditText
    private lateinit var departementEditText: Spinner
    private lateinit var matriculeEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_etudiant)
        nomEditText = findViewById(R.id.editTextNom)
        prenomEditText = findViewById(R.id.editTextPrenom)
        departementEditText = findViewById(R.id.spinnerDep) // Utilisez le EditText pour le département
        matriculeEditText = findViewById(R.id.editTextMatricule)

        chargerDepartements()
        // Gérer l'ajout d'un étudiant lorsqu'un bouton est cliqué
        findViewById<Button>(R.id.buttonSave).setOnClickListener {
            val nom = nomEditText.text.toString()
            val prenom = prenomEditText.text.toString()
            val departement = departementEditText.selectedItem.toString()
            val matricule = matriculeEditText.text.toString()

            // Vérifier si tous les champs sont remplis
            if (nom.isNotEmpty() && prenom.isNotEmpty() && departement.isNotEmpty() && matricule.isNotEmpty()) {
                val departement = departementList[departementEditText.selectedItemPosition].id ?: ""
                val etudiant = Etudiant("", matricule, nom, prenom, departement)
                insertEtudiant(etudiant)
            } else {
                // Afficher un message d'erreur si tous les champs ne sont pas remplis
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }
    }// fin onCreate

    private fun insertEtudiant(etudiant: Etudiant) {
        // URL du point de terminaison pour ajouter un étudiant sur le serveur
        val ENDPOINT = "http://192.168.50.101:3000/etudiant/"

        // Créer une requête POST avec StringRequest
        val request = object : StringRequest(Method.POST, ENDPOINT,
            { response ->
                // Succès : l'étudiant a été ajouté avec succès sur le serveur
                Toast.makeText(this, "Étudiant ajouté avec succès", Toast.LENGTH_SHORT).show()
                // Fermer l'activité d'ajout d'étudiant
                finish()
            },
            { error ->
                // Erreur : impossible d'ajouter l'étudiant sur le serveur
                Toast.makeText(this, "Erreur lors de l'ajout de l'étudiant", Toast.LENGTH_SHORT).show()
                Log.e("INSERT_ERROR", "Erreur lors de l'ajout de l'étudiant: $error")
            }) {

            // Override de la méthode getParams() pour spécifier les paramètres de la requête
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                // Vérifier que les propriétés de l'étudiant ne sont pas nulles
                etudiant?.let {
                    // Ajouter les données de l'étudiant comme paramètres de la requête
                    it.matricule?.let { matricule ->
                        params["matricule"] = matricule
                    }
                    it.nom?.let { nom ->
                        params["firstname"] = nom
                    }
                    it.prenom?.let { prenom ->
                        params["lastname"] = prenom
                    }
                    it.departement?.let { departement ->
                        params["departement"] = departement
                    }
                }

                return params
            }

        }

        // Ajouter la requête à la file d'attente de Volley pour l'exécuter
        Volley.newRequestQueue(this).add(request)
    }

    private fun chargerDepartements() {
        val ENDPOINT = "http://192.168.50.101:3000/departement/"

        // Créer une requête GET avec StringRequest
        val request = StringRequest(
            Request.Method.GET, ENDPOINT,
            { response ->
                // Succès : Les départements ont été récupérés avec succès
                departementList = ArrayList()
                val responseData = JSONArray(response)
                for (i in 0 until responseData.length()) {
                    val departementJson = responseData.getJSONObject(i)
                    val id = departementJson.getString("_id")
                    val nom = departementJson.getString("name")
                    departementList.add(Departement(id, nom))
                }

                // Créer un adaptateur pour le spinner avec la liste de noms de départements
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departementList.map { it.nom })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                departementEditText.adapter = adapter
            },
            { error ->
                // Erreur : impossible de récupérer les départements
                Log.e("GET_DEPARTEMENTS_ERROR", "Erreur lors de la récupération des départements: $error")
                Toast.makeText(this, "Erreur lors de la récupération des départements", Toast.LENGTH_SHORT).show()
            })

        // Ajouter la requête à la file d'attente de Volley pour l'exécuter
        Volley.newRequestQueue(this).add(request)
    }

}
