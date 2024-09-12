package com.example.capstone

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _searchResults = MutableLiveData<WordResult>()
    val searchResults: LiveData<WordResult> get() = _searchResults

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isBookmarked = MutableLiveData<Boolean>()
    val isBookmarked: LiveData<Boolean> get() = _isBookmarked

    private val gson = Gson()
    private lateinit var sharedPreferences: SharedPreferences

    fun initializeSharedPreferences(sharedPreferences: SharedPreferences) {
        this.sharedPreferences = sharedPreferences
        restoreSearchResults()
    }

    fun setSearchResults(result: WordResult) {
        _searchResults.value = result
    }

    fun fetchMeaning(query: String) {
        _loadingState.value = true
        viewModelScope.launch {
            try {
                val response = NetworkService.dictionaryApi.getDescription(query)
                val result = response.body()?.firstOrNull()
                _loadingState.value = false
                result?.let {
                    setSearchResults(it)
                    saveSearchResults(it)
                } ?: _errorMessage.postValue(Constants.TOAST_NO_RESULTS)
            } catch (e: Exception) {
                _loadingState.value = false
                _errorMessage.postValue(Constants.TOAST_FETCH_ERROR)
            }
        }
    }

    private fun saveSearchResults(result: WordResult) {
        val searchResultsJson = sharedPreferences.getString(Constants.SEARCHRESULT, "[]")
        val wordResults: MutableList<WordResult> = gson.fromJson(searchResultsJson, object : TypeToken<MutableList<WordResult>>() {}.type)

        wordResults.removeAll { it.word == result.word }
        wordResults.add(result)

        val editor = sharedPreferences.edit()
        editor.putString(Constants.SEARCHRESULT, gson.toJson(wordResults))
        editor.putString(Constants.CURRENTWORD, result.word)
        editor.putString(Constants.CURRENTMEANING, gson.toJson(result.meanings))
        editor.putBoolean("isBookmarked_${result.word}", _isBookmarked.value ?: false)
        editor.apply()
    }

    private fun restoreSearchResults() {
        val searchResultsJson = sharedPreferences.getString(Constants.SEARCHRESULT, "[]")
        val wordResults: List<WordResult> = gson.fromJson(searchResultsJson, object : TypeToken<List<WordResult>>() {}.type)

        if (wordResults.isNotEmpty()) {
            val currentWord = sharedPreferences.getString(Constants.CURRENTWORD, null)
            val currentMeaningsJson = sharedPreferences.getString(Constants.CURRENTMEANING, null)

            if (currentWord != null && currentMeaningsJson != null) {
                val meanings = gson.fromJson(currentMeaningsJson, Array<Meaning>::class.java).toList()
                val result = WordResult(currentWord, meanings)
                setSearchResults(result)
                _isBookmarked.value = sharedPreferences.getBoolean("isBookmarked_${currentWord}", false)
            }
        }
    }

    fun toggleBookmark() {
        val currentWord = _searchResults.value?.word ?: return
        val newState = !(_isBookmarked.value ?: false)
        _isBookmarked.value = newState
        updateBookmarkInPreferences(currentWord, newState)
    }

    private fun updateBookmarkInPreferences(word: String, isBookmarked: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isBookmarked_$word", isBookmarked)
        editor.apply()
    }

    fun updateBookmarkIcon(): Int {
        return if (_isBookmarked.value == true) R.drawable.ic_bookmark_dark else R.drawable.ic_bookmark
    }
}
