package com.example.uaspraktikummobile.room

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Movies")
data class MoviesRoom(
    @PrimaryKey(autoGenerate = true)

    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "imagePath")
    val imagePath: String,
    @ColumnInfo(name = "director")
    val director: String,
    @ColumnInfo(name = "rating")
    val rating: String,
    @ColumnInfo(name = "trending")
    val trending: Boolean
): Parcelable {
    // Implementasi Parcelable
    constructor(parcel: Parcel) : this(
        parcel.readString().toString().toIntOrNull() ?: 0,
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id.toString())
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(imagePath)
        parcel.writeString(director)
        parcel.writeString(rating)
        parcel.writeByte(if (trending) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MoviesRoom> {
        override fun createFromParcel(parcel: Parcel): MoviesRoom {
            return MoviesRoom(parcel)
        }

        override fun newArray(size: Int): Array<MoviesRoom?> {
            return arrayOfNulls(size)
        }
    }
}
