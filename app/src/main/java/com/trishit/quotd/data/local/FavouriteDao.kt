package com.trishit.quotd.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {
    @Query("SELECT * FROM favourites ORDER BY id DESC")
    fun getAll(): Flow<List<FavouriteQuote>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: FavouriteQuote)

    @Delete
    suspend fun delete(item: FavouriteQuote)

    @Query("DELETE FROM favourites WHERE q = :q AND a = :a")
    suspend fun deleteByQuote(q: String, a: String)

    @Query("SELECT COUNT(*) FROM favourites WHERE q = :q AND a = :a")
    suspend fun countByQuote(q: String, a: String): Int
}