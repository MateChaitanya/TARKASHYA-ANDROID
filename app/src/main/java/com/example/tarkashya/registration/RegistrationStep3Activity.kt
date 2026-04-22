package com.example.tarkashya.registration

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tarkashya.MainActivity
import com.example.tarkashya.R

class RegistrationStep3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure this matches your Step 3 XML file name
        setContentView(R.layout.view_reg_step_3)

        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            // Here you would typically save the PIN and consent status

            val intent = Intent(this, MainActivity::class.java)
            // Clear the backstack so the user cannot back-press into registration
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}