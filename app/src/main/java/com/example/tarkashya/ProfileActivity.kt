package com.example.tarkashya

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Setup Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Profile"

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Initialize Views
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvUserId = findViewById<TextView>(R.id.tvUserId)

        // Data would typically be fetched from your database here
        // Using "Sample Name" as requested per project requirements
        tvUserName.text = "Sample Name"
        tvUserId.text = "User ID: TKS-8892"
    }
}