package com.trishit.quotd.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trishit.quotd.data.QuoteRepository
import com.trishit.quotd.data.QuoteResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(private val repository: QuoteRepository) : ViewModel() {

    private val _state = MutableStateFlow(QuoteState())
    val state: StateFlow<QuoteState> = _state.asStateFlow()

    init {
        observeFavourites()
        fetchTodayQuote()
        onEvent(QuoteEvent.FetchNextQuote) // Initial quote fetch
    }

    fun onEvent(event: QuoteEvent) {
        when (event) {
            is QuoteEvent.FetchNextQuote -> fetchNextQuote()
            is QuoteEvent.FetchPreviousQuote -> fetchPreviousQuote()
            is QuoteEvent.ForceRefreshAndGetQuote -> forceRefreshAndGetQuote()
            is QuoteEvent.AddToFavourites -> addToFavourites(event.quote)
            is QuoteEvent.RemoveFromFavourites -> removeFromFavourites(event.quote)
            is QuoteEvent.ToggleFavourite -> toggleFavourite(event.quote)
        }
    }

    private fun observeFavourites() {
        viewModelScope.launch {
            repository.favouritesFlow().collectLatest { list ->
                _state.update {
                    it.copy(favourites = list.map { fav -> QuoteResponse(q = fav.q, a = fav.a) })
                }
            }
        }
    }

    private fun updateNavigationState() {
        _state.update {
            it.copy(canNavigateToPrevious = repository.canNavigateToPreviousQuote())
        }
    }

    private fun fetchNextQuote() {
        _state.update { it.copy(isLoading = true, errorMessage = null, navigationError = null) }
        viewModelScope.launch {
            try {
                val result = repository.getNextQuote()
                result.onSuccess { quoteResponse ->
                    _state.update {
                        it.copy(
                            quote = quoteResponse,
                            errorMessage = null
                        )
                    }
                    updateNavigationState()
                }.onFailure { error ->
                    Log.e("QuoteVM", "Error fetching next quote", error)
                    _state.update {
                        it.copy(errorMessage = error.message ?: "Failed to fetch quote")
                    }
                }
            } catch (e: Exception) {
                Log.e("QuoteVM", "Unexpected error in fetchNextQuote", e)
                _state.update { it.copy(errorMessage = e.message ?: "Network error") }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun fetchPreviousQuote() {
        if (!repository.canNavigateToPreviousQuote()) {
            _state.update {
                it.copy(navigationError = "Cannot navigate to previous quote in a new batch")
            }
            return
        }

        _state.update { it.copy(isLoading = true, navigationError = null, errorMessage = null) }

        viewModelScope.launch {
            try {
                val result = repository.getPreviousQuote()
                result.onSuccess { quoteResponse ->
                    _state.update {
                        it.copy(
                            quote = quoteResponse,
                            errorMessage = null
                        )
                    }
                }.onFailure { error ->
                    Log.e("QuoteVM", "Error fetching previous quote", error)
                    _state.update { it.copy(navigationError = error.message) }
                }
            } catch (e: Exception) {
                Log.e("QuoteVM", "Unexpected error in fetchPreviousQuote", e)
                _state.update { it.copy(navigationError = e.message ?: "Navigation error") }
            } finally {
                _state.update { it.copy(isLoading = false) }
                updateNavigationState()
            }
        }
    }

    private fun forceRefreshAndGetQuote() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val refreshSuccess = repository.forceRefreshCache()
                if (refreshSuccess) {
                    fetchNextQuote()
                } else {
                    _state.update {
                        it.copy(
                            errorMessage = "Failed to refresh quotes",
                            isLoading = false
                        )
                    }
                }
                updateNavigationState()
            } catch (e: Exception) {
                Log.e("QuoteVM", "Error refreshing quote cache", e)
                _state.update {
                    it.copy(
                        errorMessage = e.message ?: "Network error",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun addToFavourites(quote: QuoteResponse) {
        viewModelScope.launch {
            repository.addFavourite(quote.q, quote.a)
        }
    }

    private fun removeFromFavourites(quote: QuoteResponse) {
        viewModelScope.launch {
            repository.removeFavourite(quote.q, quote.a)
        }
    }

    private fun toggleFavourite(quote: QuoteResponse) {
        viewModelScope.launch {
            if (repository.isFavourite(quote.q, quote.a)) {
                repository.removeFavourite(quote.q, quote.a)
            } else {
                repository.addFavourite(quote.q, quote.a)
            }
        }
    }

    private fun fetchTodayQuote() {
        viewModelScope.launch {
            try {
                val result = repository.fetchTodayQuote()
                if (result.isSuccessful) {
                    val body = result.body()
                    if (!body.isNullOrEmpty()) {
                        _state.update { it.copy(todayQuote = body[0]) }
                    } else {
                        _state.update { it.copy(errorMessage = "Empty response for today's quote") }
                    }
                } else {
                    _state.update { it.copy(errorMessage = "Error ${result.code()} for today's quote") }
                }
            } catch (e: Exception) {
                Log.e("QuoteVM", "TodayQuote error", e)
                _state.update { it.copy(errorMessage = e.message ?: "Network error on today's quote") }
            }
        }
    }
}