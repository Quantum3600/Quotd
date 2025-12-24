package com.trishit.quotd.data

import android.util.Log
import com.trishit.quotd.data.local.CachedQuote
import com.trishit.quotd.data.local.CachedQuoteDao
import com.trishit.quotd.data.local.FavouriteDao
import com.trishit.quotd.data.local.FavouriteQuote
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class QuoteRepository @Inject constructor(
    private val api: QuoteApi,
    private val favouriteDao: FavouriteDao,
    private val cachedQuoteDao: CachedQuoteDao
) {
    // Cache management constants
    companion object {
        private const val TAG = "QuoteRepository"
    }

    // Position trackers for quote navigation
    private var currentQuotePosition = 0
    private var currentBatchId = 0  // Track the current batch of quotes
    private var isNewBatch = true   // Flag to indicate if we're in a new batch

    // Original API methods
    suspend fun fetchRandomQuote(): Response<List<QuoteResponse>> = api.getRandomQuote()
    suspend fun fetchTodayQuote(): Response<List<QuoteResponse>> = api.getTodayQuote()

    // Favorite quotes management
    fun favouritesFlow(): Flow<List<FavouriteQuote>> = favouriteDao.getAll()
    suspend fun addFavourite(q: String, a: String) = favouriteDao.insert(FavouriteQuote(q = q, a = a))
    suspend fun removeFavourite(q: String, a: String) = favouriteDao.deleteByQuote(q, a)
    suspend fun isFavourite(q: String, a: String): Boolean = favouriteDao.countByQuote(q, a) > 0

    /**
     * Checks if we can navigate to a previous quote in the current batch
     * Returns true if there's a previous quote available
     */
    fun canNavigateToPreviousQuote(): Boolean {
        return currentQuotePosition > 0 && !isNewBatch
    }

    /**
     * Gets the previous quote from the cache
     * Returns null if there's no previous quote or an error occurs
     */
    suspend fun getPreviousQuote(): Result<QuoteResponse> {
        try {
            if (!canNavigateToPreviousQuote()) {
                return Result.failure(Exception("No previous quote available"))
            }

            // Decrement position counter
            currentQuotePosition--

            // Get quote at the current position
            val quote = cachedQuoteDao.getQuoteAtPosition(currentQuotePosition)

            return if (quote != null) {
                Result.success(QuoteResponse(quote.q, quote.a))
            } else {
                // If something went wrong, reset counter to prevent further issues
                currentQuotePosition++
                Result.failure(Exception("Failed to retrieve previous quote"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting previous quote", e)
            return Result.failure(e)
        }
    }

    /**
     * Gets the next quote from the cache or fetches new quotes if needed.
     * Returns null if there's an error fetching quotes.
     */
    suspend fun getNextQuote(): Result<QuoteResponse> {
        try {
            // Check if we need to fetch more quotes
            val quoteCount = cachedQuoteDao.getCachedQuoteCount()

            // If we're at the end or near the end of our cached quotes, fetch more
            if (currentQuotePosition >= quoteCount) {
                Log.d(TAG, "Cache exhausted or empty. Refreshing cache from API.")
                val result = refreshQuoteCache()
                if (!result) {
                    return Result.failure(Exception("Failed to refresh quote cache"))
                }
                // Reset position to start of the newly cached quotes
                currentQuotePosition = 0
                currentBatchId++ // Increment batch ID
                isNewBatch = true // Mark as new batch
            } else {
                // If we're navigating through quotes, mark as not a new batch
                isNewBatch = false
            }

            // Get quote at the current position
            val quote = cachedQuoteDao.getQuoteAtPosition(currentQuotePosition)
            // Increment for next time
            currentQuotePosition++

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
     * Returns true if successful, false otherwise.
     */
    private suspend fun refreshQuoteCache(): Boolean {
        return try {
            // Clear existing cache
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
                    Log.d(TAG, "Storing ${cachedQuotes.size} quotes in cache")
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
     * Forces a refresh of the quote cache
     */
    suspend fun forceRefreshCache(): Boolean {
        currentQuotePosition = 0
        currentBatchId++ // Increment batch ID when forcing refresh
        isNewBatch = true // Mark as new batch
        return refreshQuoteCache()
    }
}