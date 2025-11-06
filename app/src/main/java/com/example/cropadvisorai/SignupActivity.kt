package com.example.cropadvisorai

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cropadvisorai.LoginActivity
import com.example.cropadvisorai.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignupActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var tvLogin: TextView
    private lateinit var progress: ProgressBar

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignup = findViewById(R.id.btnSignup)
        tvLogin = findViewById(R.id.tvGoLogin)
        progress = findViewById(R.id.progressSignup)

        auth = FirebaseAuth.getInstance()

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnSignup.setOnClickListener {
            doSignup()
        }
    }

    private fun doSignup() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (name.isEmpty()) {
            etName.error = "Name required"
            etName.requestFocus()
            return
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Valid email required"
            etEmail.requestFocus()
            return
        }
        if (password.length < 6) {
            etPassword.error = "Password must be >= 6 characters"
            etPassword.requestFocus()
            return
        }

        progress.visibility = View.VISIBLE
        btnSignup.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                progress.visibility = View.GONE
                btnSignup.isEnabled = true
                if (task.isSuccessful) {
                    // Update display name
                    val user = auth.currentUser
                    val profile = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    user?.updateProfile(profile)?.addOnCompleteListener {
                        // send email verification
                        user.sendEmailVerification()
                    }

                    Toast.makeText(this, "Account created. Taking you to Home Page", Toast.LENGTH_LONG).show()
                    // Go to login or main screen
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    val msg = task.exception?.localizedMessage ?: "Sign up failed"
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                }
            }
    }
}
