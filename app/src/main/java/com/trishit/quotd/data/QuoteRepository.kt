package com.trishit.quotd.data

import android.util.Log
import com.trishit.quotd.data.local.CachedQuote
import com.trishit.quotd.data.local.CachedQuoteDao
import com.trishit.quotd.data.local.FavouriteDao
import com.trishit.quotd.data.local.FavouriteQuote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class QuoteRepository @Inject constructor(
    private val api: QuoteApi,
    private val favouriteDao: FavouriteDao,
    private val cachedQuoteDao: CachedQuoteDao,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    // Cache management constants
    companion object {
        private const val TAG = "QuoteRepository"
        private val CACHE_EXPIRATION = TimeUnit.HOURS.toMillis(24)
    }

    // Position trackers for quote navigation
    private var currentQuotePosition = -1 // Represents the index of the currently visible quote
    private var currentBatchId = 0      // Track the current batch of quotes
    private var isNewBatch = true       // Flag to indicate if we're in a new batch

    // Original API methods
    suspend fun fetchRandomQuote(): Response<List<QuoteResponse>> = api.getRandomQuote()
    suspend fun fetchTodayQuote(): Response<List<QuoteResponse>> = api.getTodayQuote()

    // Favorite quotes management
    fun favouritesFlow(): Flow<List<FavouriteQuote>> = favouriteDao.getAll()
    suspend fun addFavourite(q: String, a: String) = favouriteDao.insert(FavouriteQuote(q = q, a = a))
    suspend fun removeFavourite(q: String, a: String) = favouriteDao.deleteByQuote(q, a)
    suspend fun isFavourite(q: String, a: String): Boolean = favouriteDao.countByQuote(q, a) > 0

    // New caching functionality
    fun cachedQuotesFlow(): Flow<List<QuoteResponse>> =
        cachedQuoteDao.getAllCachedQuotes().map { cachedList ->
            cachedList.map { QuoteResponse(it.q, it.a) }
        }

    /**
     * Checks if we can navigate to a previous quote in the current batch.
     */
    fun canNavigateToPreviousQuote(): Boolean {
        // Can navigate back if we are not on the first quote and not in a new batch
        return currentQuotePosition > 0 && !isNewBatch
    }

    /**
     * Gets the previous quote from the cache.
     */
    suspend fun getPreviousQuote(): Result<QuoteResponse> {
        try {
            if (!canNavigateToPreviousQuote()) {
                return Result.failure(Exception("No previous quote available"))
            }

            // Decrement position to get the previous quote's index
            currentQuotePosition--
            userPreferencesRepository.saveLastQuotePosition(currentQuotePosition)

            val quote = cachedQuoteDao.getQuoteAtPosition(currentQuotePosition)

            return if (quote != null) {
                Result.success(QuoteResponse(quote.q, quote.a))
            } else {
                // If quote is not found, revert the position change
                currentQuotePosition++
                userPreferencesRepository.saveLastQuotePosition(currentQuotePosition)
                Result.failure(Exception("Failed to retrieve previous quote"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting previous quote", e)
            return Result.failure(e)
        }
    }

    /**
     * Gets the next quote from the cache or fetches new quotes if needed.
     */
    suspend fun getNextQuote(): Result<QuoteResponse> {
        try {
            val oldestTimestamp = cachedQuoteDao.getOldestCacheTimestamp() ?: 0
            val isCacheExpired = System.currentTimeMillis() - oldestTimestamp > CACHE_EXPIRATION
            val quoteCount = cachedQuoteDao.getCachedQuoteCount()

            // Refresh cache if it's expired, empty, or we're at the end
            if (isCacheExpired || quoteCount == 0) {
                Log.d(TAG, "Cache is expired or empty. Refreshing.")
                val result = refreshQuoteCache()
                if (!result) {
                    return Result.failure(Exception("Failed to refresh quote cache"))
                }
                // Reset position to the start of the new batch
                currentQuotePosition = -1
                currentBatchId++
                isNewBatch = true
            } else if (currentQuotePosition == -1) {
                currentQuotePosition = userPreferencesRepository.lastQuotePosition.first()
                isNewBatch = false
            } else if (currentQuotePosition >= quoteCount - 1) {
                Log.d(TAG, "Cache exhausted. Refreshing.")
                val result = refreshQuoteCache()
                if (!result) {
                    return Result.failure(Exception("Failed to refresh quote cache"))
                }
                // Reset position to the start of the new batch
                currentQuotePosition = -1
                currentBatchId++
                isNewBatch = true
            } else {
                isNewBatch = false
            }

            // Increment position for the next quote
            currentQuotePosition++
            userPreferencesRepository.saveLastQuotePosition(currentQuotePosition)

            val quote = cachedQuoteDao.getQuoteAtPosition(currentQuotePosition)

            return if (quote != null) {
                Result.success(QuoteResponse(quote.q, quote.a))
            } else {
                Result.failure(Exception("No quote found at position $currentQuotePosition"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting next quote", e)
            return Result.failure(e)
        }
    }

    /**
     * Fetches a batch of quotes from the API and stores them in the cache.
     */
    private suspend fun refreshQuoteCache(): Boolean {
        return try {
            cachedQuoteDao.clearCache()
            val response = api.getRandomQuote()

            if (response.isSuccessful) {
                val quoteResponse = response.body()
                if (!quoteResponse.isNullOrEmpty()) {
                    val cachedQuotes = quoteResponse.mapIndexed { index, quote ->
                        CachedQuote(
                            q = quote.q,
                            a = quote.a,
                            position = index,
                            fetchedAt = System.currentTimeMillis()
                        )
                    }
                    cachedQuoteDao.insertAll(cachedQuotes)
                    true
                } else {
                    Log.e(TAG, "API error when fetching quote: ${response.code()}")
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing quote cache", e)
            false
        }
    }

    /**
     * Forces a refresh of the quote cache.
     */
    suspend fun forceRefreshCache(): Boolean {
        val refreshed = refreshQuoteCache()
        if (refreshed) {
            currentQuotePosition = -1
            currentBatchId++
            isNewBatch = true
            userPreferencesRepository.saveLastQuotePosition(currentQuotePosition)
        }
        return refreshed
    }
}
