package com.trishit.quotd.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trishit.quotd.data.QuoteRepository
import com.trishit.quotd.data.QuoteResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(private val repository: QuoteRepository) : ViewModel(){
    private val _quote = MutableLiveData<QuoteResponse?>(null)
    val quote: LiveData<QuoteResponse?> get() = _quote

    private val _todayQuote = MutableLiveData<QuoteResponse?>(null)
    val todayQuote: LiveData<QuoteResponse?> get() = _todayQuote

    private val _favourites = MutableLiveData<List<QuoteResponse>>(emptyList())
    val favourites: LiveData<List<QuoteResponse>> get() = _favourites

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        fetchRandomQuote()
        fetchTodayQuote()
    }

    fun fetchRandomQuote() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = repository.fetchRandomQuote()
                Log.d("QuoteVM", "RandomQuote response: code=${result.code()} body=${result.body()}")
                if (result.isSuccessful) {
                    val body = result.body()
                    if (!body.isNullOrEmpty()) {
                        _quote.value = body[0]
                    } else {
                        _errorMessage.value = "Empty response"
                    }
                } else {
                    _errorMessage.value = "Error ${result.code()}"
                }
            } catch (e: Exception) {
                Log.e("QuoteVM", "RandomQuote error", e)
                _errorMessage.value = e.message ?: "Network error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToFavourites(quote: QuoteResponse) {
        val currentList = _favourites.value ?: emptyList()
        if (currentList.none { it.q == quote.q && it.a == quote.a }) {
            _favourites.value = currentList + quote
        }
    }

    fun removeFromFavourites(quote: QuoteResponse) {
        val currentList = _favourites.value ?: emptyList()
        _favourites.value = currentList.filterNot { it.q == quote.q && it.a == quote.a }
    }

    fun fetchTodayQuote() {
        viewModelScope.launch {
            try {
                val result = repository.fetchTodayQuote()
                Log.d("QuoteVM", "TodayQuote response: code=${result.code()} body=${result.body()}")
                if (result.isSuccessful) {
                    val body = result.body()
                    if (!body.isNullOrEmpty()) {
                        _todayQuote.value = body[0]
                    } else {
                        _errorMessage.value = "Empty response"
                    }
                } else {
                    _errorMessage.value = "Error ${result.code()}"
                }
            } catch (e: Exception) {
                Log.e("QuoteVM", "TodayQuote error", e)
                _errorMessage.value = e.message ?: "Network error"
            }
        }
    }
}