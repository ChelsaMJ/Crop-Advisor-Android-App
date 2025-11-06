package com.example.cropadvisorai.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.cropadvisorai.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.jvm.java

class ProfileFragment : Fragment() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private lateinit var ivAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnLogin: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ivAvatar = view.findViewById(R.id.ivAvatar)
        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.tvEmail)
        btnLogout = view.findViewById(R.id.btnLogout)
        btnLogin = view.findViewById(R.id.btnLogin)

        btnLogout.setOnClickListener { confirmSignOut() }
        btnLogin.setOnClickListener { openLoginScreen() }

        refreshUI(auth.currentUser)
    }

    private fun refreshUI(user: FirebaseUser?) {
        if (user == null) {
            tvName.text = getString(R.string.not_signed_in)
            tvEmail.text = ""
            btnLogout.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
            ivAvatar.setImageResource(R.drawable.ic_profile)
        } else {
            tvName.text = user.displayName ?: getString(R.string.no_name)
            tvEmail.text = user.email ?: ""
            btnLogout.visibility = View.VISIBLE
            btnLogin.visibility = View.GONE
            ivAvatar.setImageResource(R.drawable.ic_profile)
        }
    }

    private fun confirmSignOut() {
        AlertDialog.Builder(requireContext())
            .setTitle("Sign out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Sign out") { _, _ -> performSignOut() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performSignOut() {
        auth.signOut()
        refreshUI(null)
        openLoginScreen()
    }

    private fun openLoginScreen() {
        // Replace with your actual login activity (LoginActivity or SplashActivity)
        val intent = Intent(requireContext(), com.example.cropadvisorai.LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        refreshUI(auth.currentUser)
    }
}
