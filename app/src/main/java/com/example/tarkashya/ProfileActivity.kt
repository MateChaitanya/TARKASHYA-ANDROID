package com.example.tarkashya

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Profile"

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // --- 1. Top Header Views ---
        val tvHeaderName = findViewById<TextView>(R.id.tvUserName)
        val tvHeaderId = findViewById<TextView>(R.id.tvUserId)

        // --- 2. Personal Info Views (In included row_profile_info.xml) ---
        val tvFullName = findViewById<TextView>(R.id.tvProfileFullName)
        val tvDob = findViewById<TextView>(R.id.tvProfileDob)
        val tvGender = findViewById<TextView>(R.id.tvProfileGender)
        val tvBloodGroup = findViewById<TextView>(R.id.tvProfileBlood)
        val tvPhone = findViewById<TextView>(R.id.tvProfilePhone)

        // --- 3. Medical Info Views ---
        val tvAllergies = findViewById<TextView>(R.id.tvProfileAllergies)
        val tvConditions = findViewById<TextView>(R.id.tvProfileConditions)
        val tvMeds = findViewById<TextView>(R.id.tvProfileMeds)
        val tvNote = findViewById<TextView>(R.id.tvProfileNote)

        // --- 4. Bind Data ---
        val userName = intent.getStringExtra("USER_NAME") ?: "N/A"

        tvHeaderName.text = userName
        tvHeaderId.text = "User ID: TKS-${intent.getStringExtra("USER_ID") ?: "8892"}"

        // Card 1
        tvFullName?.text = userName
        tvDob?.text = intent.getStringExtra("DOB") ?: "Not Set"
        tvGender?.text = intent.getStringExtra("GENDER") ?: "Not Set"
        tvBloodGroup?.text = intent.getStringExtra("BLOOD_GROUP") ?: "Not Set"
        tvPhone?.text = intent.getStringExtra("PHONE") ?: "Not Set"

        // Card 2
        tvAllergies.text = intent.getStringExtra("ALLERGIES") ?: "None"
        tvConditions.text = intent.getStringExtra("CONDITIONS") ?: "None"
        tvMeds.text = intent.getStringExtra("MEDS") ?: "N/A"
        tvNote.text = intent.getStringExtra("NOTE") ?: "None"
    }
}