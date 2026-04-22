package com.example.tarkashya

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvAddress: TextView
    private val CAMERA_PERMISSION_CODE = 102
    private val LOCATION_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        tvAddress = findViewById(R.id.tvLocationAddress)

        // 1. My QR Code
        findViewById<LinearLayout>(R.id.btnMyQr).setOnClickListener {
            startActivity(Intent(this, MyQRCodeActivity::class.java))
        }

        // 2. Scan QR Code (With Permission Check)
        findViewById<LinearLayout>(R.id.btnScanQr).setOnClickListener {
            checkCameraPermission()
        }

        // 3. Call Contacts
        findViewById<LinearLayout>(R.id.btnCallContacts).setOnClickListener {
            showEmergencyDialog()
        }

        // 4. Safety Tips
        findViewById<LinearLayout>(R.id.btnSafetyTips).setOnClickListener {
            startActivity(Intent(this, SafetyTipsActivity::class.java))
        }

        // Initialize Location
        checkLocationPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No Camera App Found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        } else {
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        tvAddress.text = addresses[0].getAddressLine(0)
                    }
                } else {
                    tvAddress.text = "Location not available (Turn on GPS)"
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun showEmergencyDialog() {
        val options = arrayOf("Police (100)", "Ambulance (102)", "Fire (101)")
        AlertDialog.Builder(this)
            .setTitle("Emergency Call")
            .setItems(options) { _, which ->
                val number = when (which) {
                    0 -> "100"
                    1 -> "102"
                    else -> "101"
                }
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                startActivity(intent)
            }
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else if (requestCode == LOCATION_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        }
    }
}