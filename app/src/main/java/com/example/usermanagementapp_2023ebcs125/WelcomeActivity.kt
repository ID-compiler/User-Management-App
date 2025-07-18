package com.example.usermanagementapp_2023ebcs125

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "User")

        val welcomeText = findViewById<TextView>(R.id.textViewWelcome)
        val logoutButton = findViewById<Button>(R.id.buttonLogout)

        welcomeText.text = "Welcome, $username!"

        logoutButton.setOnClickListener {
            // Clear SharedPreferences
            sharedPref.edit().clear().apply()
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut()
            // Go back to RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
