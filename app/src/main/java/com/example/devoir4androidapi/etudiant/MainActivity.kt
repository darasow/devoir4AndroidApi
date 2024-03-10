package com.example.devoir4androidapi.etudiant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.devoir4androidapi.ConfirmeDialogueLogout
import com.example.devoir4androidapi.R
import com.example.devoir4androidapi.RecyclerItemClickListener
import com.example.devoir4androidapi.departement.ListeDepartement
import com.example.devoir4androidapi.model.Etudiant
import org.json.JSONArray

class MainActivity : AppCompatActivity(), View.OnClickListener  {
    lateinit var etudiants:ArrayList<Etudiant>
    lateinit var recyclerView:RecyclerView
    private var longClickedPosition: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById<RecyclerView>(R.id.rvEtudiant)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(this, recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    // Gérez le clic sur l'élément ici si nécessaire
                    longClickedPosition = position
                }

                override fun onItemLongClick(view: View?, position: Int) {
                    longClickedPosition = position
                    openContextMenu(view)
                }
            })
        )
        registerForContextMenu(recyclerView)

    }// fin onCreate

    override fun onResume() {
        etudiants = ArrayList()
        getEtudiants()
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_departement, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menuDepartement -> {
                Intent(this, ListeDepartement::class.java).also {
                    startActivity(it)
                }
                return true
            }
            R.id.ajouterDepartement -> {
                Intent(this, AjouterEtudiant::class.java).also {
                    startActivity(it)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun getEtudiants(){
        val ENDPOINT = "http://192.168.50.101:3000/etudiant/"
        val request: StringRequest = StringRequest(
            Request.Method.GET,ENDPOINT,
            { response -> parseData(response)  },
            { error -> Log.e("GET_RESPONSE",error.toString())  })
        Volley.newRequestQueue(applicationContext).add(request)
    }

    fun parseData(response:String){
        val responseData = JSONArray(response)
        for(i in 0..responseData.length()-1){
            val etudiantJson = responseData.getJSONObject(i)
            val id = etudiantJson.getString("_id")
            val prenom = etudiantJson.getString("firstname")
            val nom = etudiantJson.getString("lastname")
            val matricule = etudiantJson.getString("matricule")
            val departement = etudiantJson.getJSONObject("departement").getString("name")
            val etudiant = Etudiant(id,matricule,nom,prenom, departement)
            etudiants.add(etudiant)
        }

        val adapter = EtudiantAdapter(etudiants)
        recyclerView.adapter = adapter

    }

    override fun onClick(view: View) {
        if(view.tag != null)
        {
            val index = view.tag as Int
        }
        return
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.menu_action, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.modalItemDelete -> {
                var confirmeFragment = ConfirmeDialogueLogout("", "Supprimer", applicationContext)
                confirmeFragment.listener = object :
                    ConfirmeDialogueLogout.ConfirmeDialogueListener {
                    override fun ondialoguePositiveClick() {
                        if (longClickedPosition != -1) {
                            val etudiant = etudiants[longClickedPosition]
                            etudiants.removeAt(longClickedPosition)
                            recyclerView.adapter?.notifyItemRemoved(longClickedPosition)
                            // Envoyer une requête DELETE au serveur pour supprimer l'étudiant
                            etudiant.id?.let { supprimerEtudiantSurServeur(it) }
                            longClickedPosition = -1
                        }
                    }
                    override fun ondialogueNegativeClick() {
                        Toast.makeText(applicationContext, "Suppression ennulée ${etudiants[longClickedPosition].id}", Toast.LENGTH_SHORT).show()
                    }
                }
                confirmeFragment.show(supportFragmentManager, "ConfirmationDialogue")
                return true
            }
            R.id.modalItemShow ->{
                val etudiant = etudiants[longClickedPosition]
                // Créez un Intent pour démarrer l'activité AddEtudiant et transmettez les détails de l'étudiant
                val intent = Intent(this, EditEtudiant::class.java).apply {
                    putExtra("etudiant", etudiant as Parcelable)
                }
                startActivity(intent)
                return true
            }
        }
        return super.onContextItemSelected(item)
    }
    fun supprimerEtudiantSurServeur(idEtudiant: String) {
        val ENDPOINT = "http://192.168.50.101:3000/etudiant/$idEtudiant"
        val request: StringRequest = StringRequest(
            Request.Method.DELETE, ENDPOINT,
            { response -> Log.d("DELETE_RESPONSE", "Étudiant supprimé avec succès sur le serveur") },
            { error -> Log.e("DELETE_RESPONSE", "Erreur lors de la suppression de l'étudiant sur le serveur: $error") })
        Volley.newRequestQueue(applicationContext).add(request)
    }


}