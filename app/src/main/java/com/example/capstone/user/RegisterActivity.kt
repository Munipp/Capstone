package com.example.capstone.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.R
import com.example.capstone.databinding.ActivityRegisterBinding
import com.example.capstone.Constants
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var isPasswordVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

        binding.btnRegisterSubmit.setOnClickListener {
            val username = binding.etRegisterUsername.text.toString()
            val password = binding.etRegisterPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val editor = sharedPreferences.edit()
                editor.putString(Constants.PREF_KEY_USERNAME, username)
                editor.putString(Constants.PREF_KEY_PASSWORD_HASH, hashPassword(password))
                editor.apply()

                Toast.makeText(this, Constants.TOAST_REGISTRATION_SUCCESS, Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, Constants.TOAST_INVALID_INPUT, Toast.LENGTH_SHORT).show()
            }
        }

        binding.etRegisterPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                if (event.rawX >= (binding.etRegisterPassword.right - binding.etRegisterPassword.compoundDrawables[drawableEnd].bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    if (isPasswordVisible) {
                        binding.etRegisterPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        binding.etRegisterPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_eye, 0)
                    } else {
                        binding.etRegisterPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding.etRegisterPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_closed_eye, 0)
                    }
                    binding.etRegisterPassword.setSelection(binding.etRegisterPassword.text.length)
                    true
                } else false
            } else false
        }
    }
    private fun hashPassword(password: String): String {
        return password.hashCode().toString()
    }
}