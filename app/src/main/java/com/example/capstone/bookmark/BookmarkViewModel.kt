package com.example.capstone.bookmark

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson

class BookmarkViewModel : ViewModel() {
    private val _bookmarks = MutableLiveData<List<String>>()
    val bookmarks: LiveData<List<String>> get() = _bookmarks

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    fun initializeSharedPreferences(sharedPreferences: SharedPreferences) {
        this.sharedPreferences = sharedPreferences
        loadBookmarks()
    }

    private fun loadBookmarks() {
        val bookmarks = mutableListOf<String>()
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            if (key.startsWith("isBookmarked_") && value == true) {
                bookmarks.add(key.removePrefix("isBookmarked_"))
            }
        }
        _bookmarks.value = bookmarks
    }

    fun removeBookmark(word: String) {
        val editor = sharedPreferences.edit()
        editor.remove("isBookmarked_$word")
        editor.apply()
        loadBookmarks()
    }
}
