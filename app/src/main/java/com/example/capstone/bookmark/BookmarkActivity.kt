package com.example.capstone.bookmark

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.history.HistoryActivity
import com.example.capstone.MainActivity
import com.example.capstone.R
import com.example.capstone.user.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BookmarkActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookmarkAdapter: BookmarkAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val bookmarkViewModel: BookmarkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        bookmarkViewModel.initializeSharedPreferences(sharedPreferences)

        recyclerView = findViewById(R.id.recyclerViewBookmarks)
        bookmarkAdapter = BookmarkAdapter(emptyList(), ::removeBookmark)

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

        bookmarkViewModel.bookmarks.observe(this, Observer { bookmarks ->
            bookmarkAdapter.updateData(bookmarks)
        })
    }

    private fun removeBookmark(word: String) {
        bookmarkViewModel.removeBookmark(word)
    }
}
