package com.trishit.quotd.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class FavouriteQuote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val q: String,
    val a: String,
)
