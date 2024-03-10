package com.example.devoir4androidapi.departement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.devoir4androidapi.R
import com.example.devoir4androidapi.model.Departement

class EditDep : AppCompatActivity() {
    private lateinit var departmentId: String
    private lateinit var departmentNameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_dep)

        val department = intent.getParcelableExtra<Departement>("departement")
        departmentId = department?.id ?: ""
        val departmentName = department?.nom ?: ""

        departmentNameEditText = findViewById(R.id.editTextName)
        departmentNameEditText.setText(departmentName)

        findViewById<Button>(R.id.buttonUpdate).setOnClickListener {
            val newName = departmentNameEditText.text.toString().trim()

            if (newName.isNotEmpty()) {
                updateDepartment(departmentId, newName)
            } else {
                Toast.makeText(this, "Donner un departement", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateDepartment(id: String, newName: String) {
        val ENDPOINT = "http://192.168.50.101:3000/departement/$id"

        val departement = Departement("", newName)

        val request = object : StringRequest(
            Request.Method.PUT, ENDPOINT,
            { response ->
                Toast.makeText(this, "Department modifier avec succes", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                Toast.makeText(this, "Une erreur est survenue", Toast.LENGTH_SHORT).show()
            }) {

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

        Volley.newRequestQueue(this).add(request)
    }
}