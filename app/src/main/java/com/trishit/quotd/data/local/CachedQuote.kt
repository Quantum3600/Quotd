package com.trishit.quotd.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_quotes")
data class CachedQuote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val q: String,
    val a: String,
    val fetchedAt: Long = System.currentTimeMillis(),
    val position: Int // To keep track of the order
)
