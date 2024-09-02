package com.example.capstone.history

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.MainActivity
import com.example.capstone.R
import com.example.capstone.user.SettingsActivity
import com.example.capstone.WordResult
import com.example.capstone.bookmark.BookmarkActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val recyclerView: RecyclerView = findViewById(R.id.rv_history)
        historyAdapter = HistoryAdapter(emptyList()) { item ->
            deleteWord(item)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = historyAdapter

        sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        restoreSearchResults()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_bookmark -> {
                    startActivity(Intent(this, BookmarkActivity::class.java))
                    true
                }
                R.id.nav_history -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.menu.findItem(R.id.nav_history).isChecked = true
    }

    private fun restoreSearchResults() {
        val searchResultsJson = sharedPreferences.getString("searchResults", "[]")
        val wordResults: List<WordResult> = gson.fromJson(searchResultsJson, object : TypeToken<List<WordResult>>() {}.type)
        historyAdapter.updateData(wordResults)
    }

    private fun deleteWord(wordResult: WordResult) {
        val searchResultsJson = sharedPreferences.getString("searchResults", "[]")
        val wordResults: MutableList<WordResult> = gson.fromJson(searchResultsJson, object : TypeToken<MutableList<WordResult>>() {}.type)

        wordResults.remove(wordResult)
        val editor = sharedPreferences.edit()
        editor.putString("searchResults", gson.toJson(wordResults))
        editor.apply()

        historyAdapter.updateData(wordResults)
    }
}