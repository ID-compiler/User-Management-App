package com.example.usermanagementapp_2023ebcs125

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuth

class NormalHomeActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val detailsList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_home)

        listView = findViewById(R.id.listViewUserDetails)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, detailsList)
        listView.adapter = adapter

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && isNetworkAvailable(this)) {
            database = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    detailsList.clear()
                    val email = snapshot.child("email").getValue(String::class.java) ?: ""
                    val username = snapshot.child("username").getValue(String::class.java) ?: ""
                    val role = snapshot.child("role").getValue(String::class.java) ?: ""
                    detailsList.add("Email: $email")
                    detailsList.add("Username: $username")
                    detailsList.add("Role: $role")
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@NormalHomeActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
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
