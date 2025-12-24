package com.trishit.quotd.ui

import com.trishit.quotd.data.QuoteResponse

data class QuoteState(
    val quote: QuoteResponse? = null,
    val todayQuote: QuoteResponse? = null,
    val favourites: List<QuoteResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigationError: String? = null,
    val canNavigateToPrevious: Boolean = false
)

sealed interface QuoteEvent {
    object FetchNextQuote : QuoteEvent
    object FetchPreviousQuote : QuoteEvent
    object ForceRefreshAndGetQuote : QuoteEvent
    data class AddToFavourites(val quote: QuoteResponse) : QuoteEvent
    data class RemoveFromFavourites(val quote: QuoteResponse) : QuoteEvent
    data class ToggleFavourite(val quote: QuoteResponse) : QuoteEvent
}
