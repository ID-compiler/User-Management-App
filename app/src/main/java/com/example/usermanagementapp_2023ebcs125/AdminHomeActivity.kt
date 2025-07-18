package com.example.usermanagementapp_2023ebcs125

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.content.Intent
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminHomeActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val userList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        val username = intent.getStringExtra("username") ?: ""
        val welcomeText = findViewById<TextView>(R.id.textViewAdminWelcome)
        welcomeText.text = "Hello admin $username"

        listView = findViewById(R.id.listViewAllUsers)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
        listView.adapter = adapter

        if (isNetworkAvailable(this)) {
            database = FirebaseDatabase.getInstance().getReference("users")
            // You can use a Service here, but for simplicity, we'll fetch directly
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (userSnap in snapshot.children) {
                        val details = userSnap.value as? Map<*, *>
                        val display = details?.entries?.joinToString { "${it.key}: ${it.value}" }
                        userList.add(display ?: "No details")
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AdminHomeActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No network. Please enable internet.", Toast.LENGTH_LONG).show()
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
