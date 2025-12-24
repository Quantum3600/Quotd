package com.trishit.quotd.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavouriteQuote::class, CachedQuote::class],
    version = 2,
    exportSchema = false
)
abstract class QuoteDatabase : RoomDatabase() {
    abstract fun favouriteDao(): FavouriteDao
    abstract fun cachedQuoteDao(): CachedQuoteDao
}