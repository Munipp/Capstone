package com.example.capstone.history

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.capstone.WordResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _historyResults = MutableLiveData<List<WordResult>>()
    val historyResults: LiveData<List<WordResult>> get() = _historyResults

    init {
        loadSearchResults()
    }

    private fun loadSearchResults() {
        val searchResultsJson = sharedPreferences.getString("searchResults", "[]")
        val wordResults: List<WordResult> = gson.fromJson(searchResultsJson, object : TypeToken<List<WordResult>>() {}.type)
        _historyResults.value = wordResults
    }

    fun deleteWord(wordResult: WordResult) {
        viewModelScope.launch(Dispatchers.IO) {
            val searchResultsJson = sharedPreferences.getString("searchResults", "[]")
            val wordResults: MutableList<WordResult> = gson.fromJson(searchResultsJson, object : TypeToken<MutableList<WordResult>>() {}.type)

            wordResults.remove(wordResult)
            val editor = sharedPreferences.edit()
            editor.putString("searchResults", gson.toJson(wordResults))
            editor.apply()

            _historyResults.postValue(wordResults)
        }
    }
}
