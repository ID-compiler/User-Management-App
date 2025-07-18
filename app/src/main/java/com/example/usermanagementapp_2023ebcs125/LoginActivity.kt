package com.example.usermanagementapp_2023ebcs125
//Student ID: 2023ebcs125
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        database = FirebaseDatabase.getInstance().getReference("users")

        val usernameEditText = findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        loginButton.setOnClickListener {
            val userInput = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            Toast.makeText(this, "Login button clicked", Toast.LENGTH_SHORT).show()

            if (userInput.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username/email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userInput.contains("@")) {
                // Treat as email
                auth.signInWithEmailAndPassword(userInput, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Email login success", Toast.LENGTH_SHORT).show()
                            sharedPref.edit().putString("username", userInput).apply()
                            val user = FirebaseAuth.getInstance().currentUser
                            if (user != null) {
                                val userRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
                                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val role = snapshot.child("role").getValue(String::class.java)
                                        val intent = when (role) {
                                            "admin" -> Intent(this@LoginActivity, AdminHomeActivity::class.java)
                                            "normal" -> Intent(this@LoginActivity, NormalHomeActivity::class.java)
                                            else -> Intent(this@LoginActivity, WelcomeActivity::class.java)
                                        }
                                        startActivity(intent)
                                        finish()
                                    }
                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(this@LoginActivity, "Failed to get user role", Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                        } else {
                            val errorMsg = task.exception?.message ?: ""
                            if (errorMsg.contains("no user record", ignoreCase = true)) {
                                Toast.makeText(this, "Email does not exist", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Email login failed: $errorMsg", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            } else {
                // Treat as username: look up email in database
                database.orderByChild("username").equalTo(userInput)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (userSnap in snapshot.children) {
                                    val email = userSnap.child("email").getValue(String::class.java)
                                    if (email != null) {
                                        auth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(this@LoginActivity, "Username login success", Toast.LENGTH_SHORT).show()
                                                    sharedPref.edit().putString("username", userInput).apply()
                                                    val user = FirebaseAuth.getInstance().currentUser
                                                    if (user != null) {
                                                        val userRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
                                                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                                val role = snapshot.child("role").getValue(String::class.java)
                                                                val intent = when (role) {
                                                                    "admin" -> Intent(this@LoginActivity, AdminHomeActivity::class.java)
                                                                    "normal" -> Intent(this@LoginActivity, NormalHomeActivity::class.java)
                                                                    else -> Intent(this@LoginActivity, WelcomeActivity::class.java)
                                                                }
                                                                startActivity(intent)
                                                                finish()
                                                            }
                                                            override fun onCancelled(error: DatabaseError) {
                                                                Toast.makeText(this@LoginActivity, "Failed to get user role", Toast.LENGTH_SHORT).show()
                                                            }
                                                        })
                                                    }
                                                } else {
                                                    Toast.makeText(this@LoginActivity, "Username login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        return
                                    }
                                }
                            } else {
                                Toast.makeText(this@LoginActivity, "Username does not exist", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@LoginActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }
}
