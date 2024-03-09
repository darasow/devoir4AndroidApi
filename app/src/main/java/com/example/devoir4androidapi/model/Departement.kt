package com.example.devoir4androidapi.model

import android.os.Parcel
import android.os.Parcelable

class Departement(val id:String?,val nom:String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nom)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Departement> {
        override fun createFromParcel(parcel: Parcel): Departement {
            return Departement(parcel)
        }

        override fun newArray(size: Int): Array<Departement?> {
            return arrayOfNulls(size)
        }
    }
}