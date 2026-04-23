package com.example.tarkashya

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class AlertStatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_status)

        // Back button to return to dashboard
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        // Call button
        findViewById<Button>(R.id.btnCallEmergency).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:100"))
            startActivity(intent)
        }

        // Cancel button
        findViewById<Button>(R.id.btnCancelAlert).setOnClickListener {
            finish() // Simply returns to the main screen
        }
    }
}