package com.example.capstone.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.MainActivity
import com.example.capstone.R
import com.example.capstone.databinding.ActivityLoginBinding
import com.example.capstone.Constants

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            if (validateCredentials(username, password)) {
                val editor = sharedPreferences.edit()
                editor.putString(Constants.PREF_KEY_LOGGED_IN_USER, username)
                editor.apply()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Close LoginActivity
            } else {
                Toast.makeText(this, Constants.TOAST_INVALID_CREDENTIALS, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.etPassword.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                if (event.rawX >= (binding.etPassword.right - binding.etPassword.compoundDrawables[drawableEnd].bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    if (isPasswordVisible) {
                        binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_eye, 0)
                    } else {
                        binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_closed_eye, 0)
                    }
                    binding.etPassword.setSelection(binding.etPassword.text.length)
                    true
                } else false
            } else false
        }
    }
    private fun validateCredentials(username: String, password: String): Boolean {
        val storedUsername = sharedPreferences.getString(Constants.PREF_KEY_USERNAME, "")
        val storedPasswordHash = sharedPreferences.getString(Constants.PREF_KEY_PASSWORD_HASH, "")
        return username == storedUsername && hashPassword(password) == storedPasswordHash
    }
    private fun hashPassword(password: String): String {
        return password.hashCode().toString()
    }
}