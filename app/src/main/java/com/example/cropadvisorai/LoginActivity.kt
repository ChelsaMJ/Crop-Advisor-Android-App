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
import com.example.cropadvisorai.R
import com.example.cropadvisorai.SignupActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignup: TextView
    private lateinit var progress: ProgressBar

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignup = findViewById(R.id.tvGoSignup)
        progress = findViewById(R.id.progressLogin)

        auth = FirebaseAuth.getInstance()

        // If already signed in, go to Splash/Main directly
        auth.currentUser?.let {
            // Optionally check email verification here
            navigateToMain()
            finish()
        }

        btnLogin.setOnClickListener {
            doLogin()
        }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun doLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            etEmail.error = "Email required"
            etEmail.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Enter a valid email"
            etEmail.requestFocus()
            return
        }
        if (password.length < 6) {
            etPassword.error = "Password should be at least 6 characters"
            etPassword.requestFocus()
            return
        }

        progress.visibility = View.VISIBLE
        btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                progress.visibility = View.GONE
                btnLogin.isEnabled = true
                if (task.isSuccessful) {
                    // Optionally check email verification:
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        navigateToMain()
                        finish()
                    } else {
                        navigateToMain()
                        finish()
                    }
                } else {
                    val msg = task.exception?.localizedMessage ?: "Login failed"
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun navigateToMain() {
        // On successful login, route to your main screen. Using SplashActivity from your project:
        val intent = Intent(this, com.example.cropadvisorai.SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
