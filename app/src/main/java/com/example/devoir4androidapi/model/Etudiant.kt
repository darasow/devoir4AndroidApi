package com.example.devoir4androidapi.model

import android.os.Parcel
import android.os.Parcelable

data class Etudiant(val id:String?,val matricule:String?, val nom:String?, val  prenom:String?, val departement:String?) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(matricule)
        parcel.writeString(nom)
        parcel.writeString(prenom)
        parcel.writeString(departement)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Etudiant> {
        override fun createFromParcel(parcel: Parcel): Etudiant {
            return Etudiant(parcel)
        }

        override fun newArray(size: Int): Array<Etudiant?> {
            return arrayOfNulls(size)
        }
    }
}