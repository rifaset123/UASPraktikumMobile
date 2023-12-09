package com.example.uaspraktikummobile.database

import android.os.Parcel
import android.os.Parcelable

data class Movies(
    var id: String = "",
    var imagePath: String = "",
    var title: String = "",
    var director: String = "",
    var description: String = "",
    var rating: String = ""
): Parcelable {
    // agar bisa di edit
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(imagePath)
        parcel.writeString(title)
        parcel.writeString(director)
        parcel.writeString(description)
        parcel.writeString(rating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Movies> {
        override fun createFromParcel(parcel: Parcel): Movies {
            return Movies(parcel)
        }

        override fun newArray(size: Int): Array<Movies?> {
            return arrayOfNulls(size)
        }
    }

    private constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        imagePath = parcel.readString() ?: "",
        title = parcel.readString() ?: "",
        director = parcel.readString() ?: "",
        description = parcel.readString() ?: "",
        rating = parcel.readString() ?: ""
    )
}
