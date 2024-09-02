package com.example.capstone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    private val _searchResults = MutableLiveData<WordResult>()
    val searchResults: LiveData<WordResult> get() = _searchResults
    fun setSearchResults(result: WordResult) {
        _searchResults.value = result
    }
}

