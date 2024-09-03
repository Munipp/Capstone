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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.bookmark.BookmarkActivity
import com.example.capstone.databinding.ActivityMainBinding
import com.example.capstone.detail.DescriptionAdapter
import com.example.capstone.history.HistoryActivity
import com.example.capstone.user.SettingsActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var descriptionAdapter: DescriptionAdapter
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private var isBookmarked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        restoreSearchResults()

        setupRecyclerView()
        setupBottomNavigation()

        searchViewModel.searchResults.observe(this, Observer { result ->
            result?.let { updateUI(it) }
        })

        binding.searchButton.setOnClickListener {
            val query = binding.searchBar.text.toString()
            if (query.isNotEmpty()) {
                fetchMeaning(query)
            } else {
                showToast(Constants.TOAST_INVALID_INPUT)
            }
        }

        binding.icBookmark.setOnClickListener {
            toggleBookmark()
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
    private fun fetchMeaning(query: String) {
        setLoadingState(true)
        lifecycleScope.launch {
            try {
                val response = NetworkService.dictionaryApi.getDescription(query)
                val result = response.body()?.firstOrNull()
                setLoadingState(false)
                result?.let {
                    searchViewModel.setSearchResults(it)
                    saveSearchResults(it)
                    binding.icBookmark.visibility = View.VISIBLE
                    isBookmarked = sharedPreferences.getBoolean("isBookmarked_${it.word}", false)
                    updateBookmarkIcon()
                } ?: showToast(Constants.TOAST_NO_RESULTS)
            } catch (e: Exception) {
                setLoadingState(false)
                showToast(Constants.TOAST_FETCH_ERROR)
            }
        }
    }
    private fun updateUI(wordResult: WordResult) {
        binding.tvWord.text = wordResult.word
        descriptionAdapter.updateData(wordResult.meanings)
        isBookmarked = sharedPreferences.getBoolean("isBookmarked_${wordResult.word}", false)
        updateBookmarkIcon()
    }
    private fun setLoadingState(isLoading: Boolean) {
        binding.searchButton.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.pBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun saveSearchResults(result: WordResult) {
        val searchResultsJson = sharedPreferences.getString("searchResults", "[]")
        val wordResults: MutableList<WordResult> = gson.fromJson(searchResultsJson, object : TypeToken<MutableList<WordResult>>() {}.type)

        wordResults.removeAll { it.word == result.word }
        wordResults.add(result)

        val editor = sharedPreferences.edit()
        editor.putString("searchResults", gson.toJson(wordResults))
        editor.putString("currentWord", result.word)
        editor.putString("currentMeanings", gson.toJson(result.meanings))
        editor.putBoolean("isBookmarked_${result.word}", isBookmarked)
        editor.apply()
    }
    private fun restoreSearchResults() {
        val searchResultsJson = sharedPreferences.getString("searchResults", "[]")
        val wordResults: List<WordResult> = gson.fromJson(searchResultsJson, object : TypeToken<List<WordResult>>() {}.type)

        if (wordResults.isNotEmpty()) {
            val currentWord = sharedPreferences.getString("currentWord", null)
            val currentMeaningsJson = sharedPreferences.getString("currentMeanings", null)

            if (currentWord != null && currentMeaningsJson != null) {
                val meanings = gson.fromJson(currentMeaningsJson, Array<Meaning>::class.java).toList()
                val result = WordResult(currentWord, meanings)
                searchViewModel.setSearchResults(result)
                isBookmarked = sharedPreferences.getBoolean("isBookmarked_${currentWord}", false)
                updateBookmarkIcon()
            }
        }
    }
    private fun toggleBookmark() {
        val currentWord = searchViewModel.searchResults.value?.word ?: return
        isBookmarked = !isBookmarked
        updateBookmarkIcon()
        val editor = sharedPreferences.edit()
        editor.putBoolean("isBookmarked_$currentWord", isBookmarked)
        editor.apply()
    }
    private fun updateBookmarkIcon() {
        val bookmarkIcon = if (isBookmarked) R.drawable.ic_bookmark_dark else R.drawable.ic_bookmark
        binding.icBookmark.setImageResource(bookmarkIcon)
    }
}
