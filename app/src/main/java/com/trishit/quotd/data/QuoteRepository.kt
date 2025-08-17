package com.trishit.quotd.data

import javax.inject.Inject

class QuoteRepository @Inject constructor(private val api: QuoteApi) {
    suspend fun fetchRandomQuote() = api.getRandomQuote()
    suspend fun fetchTodayQuote() = api.getTodayQuote()
}