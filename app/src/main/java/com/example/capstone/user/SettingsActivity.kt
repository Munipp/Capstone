package com.example.capstone.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    private lateinit var etPassword: EditText
    private lateinit var icEye: ImageView
    private var isPasswordVisible = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var btnChangePassword: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

        etPassword = findViewById(R.id.et_password)
        icEye = findViewById(R.id.ic_eye)
        btnChangePassword = findViewById(R.id.btn_change_password)
        btnLogout = findViewById(R.id.btn_logout)

        val username = sharedPreferences.getString(Constants.PREF_KEY_USERNAME, "Guest")
        findViewById<TextView>(R.id.tv_username).text = "${Constants.PREF_KEY_USERNAME_DISPLAY}: $username"

        icEye.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                icEye.setImageResource(R.drawable.ic_eye)
            } else {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                icEye.setImageResource(R.drawable.ic_closed_eye)
            }
            etPassword.setSelection(etPassword.text.length)
        }

        etPassword.addTextChangedListener {
            icEye.isEnabled = it?.isNotEmpty() == true
        }

        btnChangePassword.setOnClickListener {
            val newPassword = etPassword.text.toString()
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

        btnLogout.setOnClickListener {
            val editor = sharedPreferences.edit()
            val currentPassword = etPassword.text.toString()
            if (currentPassword.isNotEmpty()) {
                editor.putString(Constants.PREF_KEY_PASSWORD_HASH, hashPassword(currentPassword))
            }
            editor.remove(Constants.PREF_KEY_LOGGED_IN_USER)
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        setupBottomNavigationView()
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
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
                else -> false
            }
        }
        bottomNavigationView.menu.findItem(R.id.nav_settings).isChecked = true
    }

    private fun hashPassword(password: String): String {
        return password.hashCode().toString()
    }
}
