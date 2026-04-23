package com.example.tarkashya.registration

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tarkashya.LoginActivity  // Import the LoginActivity
import com.example.tarkashya.R

class RegistrationStep3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure this matches your Step 3 XML file name
        setContentView(R.layout.view_reg_step_3)

        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            // 1. Logic to save data (PIN, emergency contacts, etc.) would go here

            // 2. Show a success message to the user
            Toast.makeText(this, "Registration Successful! Please login.", Toast.LENGTH_LONG).show()

            // 3. Navigate to LoginActivity instead of MainActivity
            val intent = Intent(this, LoginActivity::class.java)

            // 4. IMPORTANT: This clears all previous registration screens from memory
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            // 5. Close this activity
            finish()
        }
    }
}