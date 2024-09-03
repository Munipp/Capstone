package com.example.capstone

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.bookmark.BookmarkActivity
import com.example.capstone.databinding.ActivityMainBinding
import com.example.capstone.detail.DescriptionAdapter
import com.example.capstone.history.HistoryActivity
import com.example.capstone.user.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var descriptionAdapter: DescriptionAdapter
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE)
        searchViewModel.initializeSharedPreferences(sharedPreferences)

        setupRecyclerView()
        setupBottomNavigation()

        searchViewModel.searchResults.observe(this, Observer { result ->
            result?.let { updateUI(it) }
        })

        searchViewModel.loadingState.observe(this, Observer { isLoading ->
            setLoadingState(isLoading)
        })

        searchViewModel.errorMessage.observe(this, Observer { message ->
            message?.let { showToast(it) }
        })

        searchViewModel.isBookmarked.observe(this, Observer { isBookmarked ->
            binding.icBookmark.setImageResource(searchViewModel.updateBookmarkIcon())
        })

        binding.searchButton.setOnClickListener {
            val query = binding.searchBar.text.toString()
            if (query.isNotEmpty()) {
                searchViewModel.fetchMeaning(query)
            } else {
                showToast(Constants.TOAST_INVALID_INPUT)
            }
        }

        binding.icBookmark.setOnClickListener {
            searchViewModel.toggleBookmark()
        }
    }

    private fun setupRecyclerView() {
        descriptionAdapter = DescriptionAdapter(emptyList())
        binding.rvDescription.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = descriptionAdapter
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_bookmark -> {
                    startActivity(Intent(this, BookmarkActivity::class.java))
                    true
                }
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
        binding.bottomNavigation.menu.findItem(R.id.nav_home).isChecked = true
    }

    private fun updateUI(wordResult: WordResult) {
        binding.tvWord.text = wordResult.word
        descriptionAdapter.updateData(wordResult.meanings)
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.searchButton.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.pBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
