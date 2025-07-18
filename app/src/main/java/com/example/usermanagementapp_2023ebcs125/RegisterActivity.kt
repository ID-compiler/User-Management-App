package com.example.usermanagementapp_2023ebcs125

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.appcompat.app.AlertDialog

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val usernameEditText = findViewById<EditText>(R.id.editTextUsername)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val roleSpinner = findViewById<Spinner>(R.id.spinnerRole)
        val registerButton = findViewById<Button>(R.id.buttonRegister)
        val loginLink = findViewById<TextView>(R.id.textViewLoginLink)

        // Setup spinner
        val roles = arrayOf("admin", "normal")
        roleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val role = roleSpinner.selectedItem.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isNetworkAvailable()) {
                Toast.makeText(this, "No network. Please enable internet.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Save extra details in Firebase Database
                        val userId = auth.currentUser?.uid
                        val userMap = mapOf(
                            "username" to username,
                            "role" to role,
                            "email" to email
                        )
                        if (userId != null) {
                            FirebaseDatabase.getInstance().getReference("users")
                                .child(userId)
                                .setValue(userMap)
                                .addOnSuccessListener {
                                    AlertDialog.Builder(this)
                                        .setTitle("Registration Successful")
                                        .setMessage("User $username is registered.")
                                        .setPositiveButton("OK") { dialog, _ ->
                                            dialog.dismiss()
                                            // Optionally, navigate to LoginActivity after registration
                                            startActivity(Intent(this, LoginActivity::class.java))
                                            finish()
                                        }
                                        .show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
