package com.trishit.quotd.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedQuoteDao {
    @Query("SELECT * FROM cached_quotes ORDER BY position ASC")
    fun getAllCachedQuotes(): Flow<List<CachedQuote>>

    @Query("SELECT * FROM cached_quotes ORDER BY position ASC")
    suspend fun getCachedQuotesAsList(): List<CachedQuote>

    @Query("SELECT COUNT(*) FROM cached_quotes")
    suspend fun getCachedQuoteCount(): Int

    @Query("SELECT * FROM cached_quotes WHERE position = :position LIMIT 1")
    suspend fun getQuoteAtPosition(position: Int): CachedQuote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(quotes: List<CachedQuote>)

    @Query("DELETE FROM cached_quotes")
    suspend fun clearCache()

    @Query("SELECT MAX(position) FROM cached_quotes")
    suspend fun getMaxPosition(): Int?
}
