package com.example.capstone.bookmark

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.history.HistoryActivity
import com.example.capstone.MainActivity
import com.example.capstone.R
import com.example.capstone.user.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class BookmarkActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookmarkAdapter: BookmarkAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val bookmarks = loadBookmarks()

        recyclerView = findViewById(R.id.recyclerViewBookmarks)
        bookmarkAdapter = BookmarkAdapter(bookmarks, ::removeBookmark)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@BookmarkActivity)
            adapter = bookmarkAdapter
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_bookmark -> true
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.menu.findItem(R.id.nav_bookmark).isChecked = true
    }
    private fun loadBookmarks(): List<String> {
        val bookmarks = mutableListOf<String>()
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            if (key.startsWith("isBookmarked_") && value == true) {
                bookmarks.add(key.removePrefix("isBookmarked_"))
            }
        }
        return bookmarks
    }
    private fun removeBookmark(word: String) {
        val editor = sharedPreferences.edit()
        editor.remove("isBookmarked_$word")
        editor.apply()
        bookmarkAdapter.updateData(loadBookmarks())
    }
}