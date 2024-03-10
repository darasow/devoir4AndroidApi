package com.example.devoir4androidapi.departement

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
import com.example.devoir4androidapi.model.Departement
import org.json.JSONArray

class ListeDepartement : AppCompatActivity(),View.OnClickListener {
    lateinit var departements:ArrayList<Departement>
    lateinit var recyclerView: RecyclerView
    private var longClickedPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_departement)

        departements = ArrayList()

        recyclerView = findViewById<RecyclerView>(R.id.rvDepartement)
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


    } // onCreate
    override fun onResume() {
        departements = ArrayList()
        getDepartement()
        super.onResume()
    }
    fun parseData(response:String){
        val responseData = JSONArray(response)
        for(i in 0..responseData.length()-1){
            val departementJson = responseData.getJSONObject(i)
            val id = departementJson.getString("_id")
            val nom = departementJson.getString("name")
            val departement = Departement(id,nom)
            departements.add(departement)
        }

        val adapter = DepartementAdapter(departements)
        recyclerView.adapter = adapter

    }
    fun getDepartement(){
        val ENDPOINT = "http://192.168.50.101:3000/departement/"
        val request: StringRequest = StringRequest(
            Request.Method.GET,ENDPOINT,
            { response -> parseData(response)  },
            { error -> Log.e("GET_RESPONSE",error.toString()) })
        Volley.newRequestQueue(applicationContext).add(request)
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_depaction, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

            R.id.ajouterDep -> {
                Intent(this, AjoutDepartement::class.java).also {
                    startActivity(it)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.modalItemDelete -> {
                var confirmeFragment = ConfirmeDialogueLogout("", "Supprimer", applicationContext)
                confirmeFragment.listener =
                    object : ConfirmeDialogueLogout.ConfirmeDialogueListener {
                        override fun ondialoguePositiveClick() {
                            if (longClickedPosition != -1) {
                                val departement = departements[longClickedPosition]
                                departements.removeAt(longClickedPosition)
                                recyclerView.adapter?.notifyItemRemoved(longClickedPosition)
                                // Envoyer une requête DELETE au serveur pour supprimer l'étudiant
                                departement.id?.let { supprimerDepartementSurServeur(it) }
                                longClickedPosition = -1
                            }
                        }

                        override fun ondialogueNegativeClick() {
                            Toast.makeText(
                                applicationContext,
                                "Suppression ennulée ${departements[longClickedPosition].id}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                confirmeFragment.show(supportFragmentManager, "ConfirmationDialogue")
                return true
            }
            R.id.modalItemShow ->{
                val departement = departements[longClickedPosition]
                // Créez un Intent pour démarrer l'activité AddEtudiant et transmettez les détails de l'étudiant
                val intent = Intent(this, EditDep::class.java).apply {
                    putExtra("departement", departement as Parcelable)
                }
                startActivity(intent)
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    fun supprimerDepartementSurServeur(idDepartement: String) {
        val ENDPOINT = "http://192.168.50.101:3000/departement/$idDepartement"
        val request: StringRequest = StringRequest(
            Request.Method.DELETE, ENDPOINT,
            { response -> Log.d("DELETE_RESPONSE", "Étudiant supprimé avec succès sur le serveur") },
            { error -> Log.e("DELETE_RESPONSE", "Erreur lors de la suppression de l'étudiant sur le serveur: $error") })
        Volley.newRequestQueue(applicationContext).add(request)
    }

}