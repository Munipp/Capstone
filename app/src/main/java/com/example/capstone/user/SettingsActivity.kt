package com.example.capstone.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.capstone.MainActivity
import com.example.capstone.R
import com.example.capstone.bookmark.BookmarkActivity
import com.example.capstone.history.HistoryActivity
import com.example.capstone.Constants
import com.example.capstone.databinding.ActivitySettingsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

        // Set username
        val username = sharedPreferences.getString(Constants.PREF_KEY_USERNAME, "Guest")
        binding.tvUsername.text = "${Constants.PREF_KEY_USERNAME_DISPLAY}: $username"

        binding.icEye.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.icEye.setImageResource(R.drawable.ic_eye)
            } else {
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.icEye.setImageResource(R.drawable.ic_closed_eye)
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.etPassword.addTextChangedListener {
            binding.icEye.isEnabled = it?.isNotEmpty() == true
        }

        binding.btnChangePassword.setOnClickListener {
            val newPassword = binding.etPassword.text.toString()
            if (newPassword.isNotEmpty()) {
                val editor = sharedPreferences.edit()
                editor.putString(Constants.PREF_KEY_PASSWORD_HASH, hashPassword(newPassword))
                editor.apply()

                Toast.makeText(this, Constants.TOAST_PASSWORD_CHANGED, Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, Constants.TOAST_EMPTY_PASSWORD, Toast.LENGTH_SHORT).show()
            }
        }

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_bookmark -> {
                    startActivity(Intent(this, BookmarkActivity::class.java))
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
        bottomNavigationView.menu.findItem(R.id.nav_settings).isChecked = true

        binding.btnLogout.setOnClickListener {
            val editor = sharedPreferences.edit()

            val currentPassword = binding.etPassword.text.toString()
            if (currentPassword.isNotEmpty()) {
                editor.putString(Constants.PREF_KEY_PASSWORD_HASH, hashPassword(currentPassword))
            }

            editor.remove(Constants.PREF_KEY_LOGGED_IN_USER)

            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun hashPassword(password: String): String {
        return password.hashCode().toString()
    }
}