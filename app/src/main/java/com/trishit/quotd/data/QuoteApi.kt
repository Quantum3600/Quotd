package com.trishit.quotd.data

import retrofit2.Response
import retrofit2.http.GET

interface QuoteApi {
    @GET("quotes")
    suspend fun getRandomQuote(): Response<List<QuoteResponse>>
    @GET("today")
    suspend fun getTodayQuote(): Response<List<QuoteResponse>>
}