package com.example.devoir4androidapi

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ConfirmeDialogueLogout(val info : String? = "", val action : String? = "", val contex : Context) : DialogFragment()
{

    interface ConfirmeDialogueListener
    {
        fun ondialoguePositiveClick()
        fun ondialogueNegativeClick()
    }
    var listener : ConfirmeDialogueListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        builder.setTitle("Confirmation !")
        builder.setMessage("Etes-vous sur de ${this.action} ${this.info}?")
            .setPositiveButton("${this.action}", DialogInterface.OnClickListener{
                    dialog, id-> listener?.ondialoguePositiveClick()

            })
            .setNegativeButton("Ennuler", DialogInterface.OnClickListener{
                    dialog, id-> listener?.ondialogueNegativeClick()
            })

        return builder.create()
    }

}