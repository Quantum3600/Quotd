package com.trishit.quotd.data

import com.trishit.quotd.data.local.FavouriteDao
import com.trishit.quotd.data.local.FavouriteQuote
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuoteRepository @Inject constructor(
    private val api: QuoteApi,
    private val favouriteDao: FavouriteDao
) {
    suspend fun fetchRandomQuote() = api.getRandomQuote()
    suspend fun fetchTodayQuote() = api.getTodayQuote()
    fun favouritesFlow(): Flow<List<FavouriteQuote>> = favouriteDao.getAll()
    suspend fun addFavourite(q: String, a: String) = favouriteDao.insert(FavouriteQuote(q = q, a = a))
    suspend fun removeFavourite(q: String, a: String) = favouriteDao.deleteByQuote(q, a)
    suspend fun isFavourite(q: String, a: String): Boolean = favouriteDao.countByQuote(q, a) > 0
}