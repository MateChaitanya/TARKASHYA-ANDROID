package com.example.tarkashya

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvAddress: TextView
    private lateinit var drawerLayout: DrawerLayout
    private var isEmergencyMode = false

    private val CAMERA_PERMISSION_CODE = 102
    private val LOCATION_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isEmergencyMode = intent.getBooleanExtra("isEmergencyMode", false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        tvAddress = findViewById(R.id.tvLocationAddress)
        drawerLayout = findViewById(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)
        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        val btnBottomStatus = findViewById<Button>(R.id.bottomButton)

        // --- BOTTOM BUTTON CONFIGURATION ---
        btnBottomStatus.visibility = View.VISIBLE
        btnBottomStatus.text = "CHECK ALERT STATUS"

        btnBottomStatus.setOnClickListener {
            val intent = Intent(this, AlertStatusActivity::class.java)
            startActivity(intent)
        }

        if (isEmergencyMode) {
            setupEmergencyUI(menuIcon, btnBottomStatus)
        }

        // --- Sidebar Logic ---
        menuIcon.setOnClickListener {
            if (!isEmergencyMode) {
                drawerLayout.openDrawer(GravityCompat.START)
            } else {
                Toast.makeText(this, "Restricted: Use Medical Info/SOS", Toast.LENGTH_SHORT).show()
            }
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> navigateWithState(ProfileActivity::class.java)
                R.id.nav_logout -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // --- Grid Actions ---
        findViewById<LinearLayout>(R.id.btnMyQr).setOnClickListener {
            navigateWithState(MyQRCodeActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.btnScanQr).setOnClickListener {
            if (isEmergencyMode) showDisabledToast() else checkCameraPermission()
        }

        findViewById<LinearLayout>(R.id.btnCallContacts).setOnClickListener {
            showEmergencyDialog()
        }

        findViewById<LinearLayout>(R.id.btnSafetyTips).setOnClickListener {
            if (isEmergencyMode) showDisabledToast() else navigateWithState(SafetyTipsActivity::class.java)
        }

        val sosButton = findViewById<LinearLayout>(R.id.sosButton)
        sosButton.setOnLongClickListener {
            triggerEmergencyAlert()
            true
        }

        checkLocationPermission()
    }

    private fun setupEmergencyUI(menuIcon: ImageView, bottomBtn: Button) {
        menuIcon.visibility = View.GONE
        bottomBtn.visibility = View.VISIBLE

        findViewById<LinearLayout>(R.id.btnScanQr).alpha = 0.3f
        findViewById<LinearLayout>(R.id.btnSafetyTips).alpha = 0.3f

        Toast.makeText(this, "GUEST ACCESS: Medical Profile & SOS active", Toast.LENGTH_LONG).show()
    }

    private fun showDisabledToast() {
        Toast.makeText(this, "Login required for this feature", Toast.LENGTH_SHORT).show()
    }

    private fun navigateWithState(target: Class<*>) {
        val intent = Intent(this, target)
        intent.putExtra("isEmergencyMode", isEmergencyMode)
        startActivity(intent)
    }

    private fun triggerEmergencyAlert() {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000)
        } catch (e: Exception) { e.printStackTrace() }
        Toast.makeText(this, "🚨 SOS ACTIVE!", Toast.LENGTH_LONG).show()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        } else { getLastLocation() }
    }

    private fun getLastLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) tvAddress.text = addresses[0].getAddressLine(0)
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun showEmergencyDialog() {
        val options = arrayOf("Police (100)", "Ambulance (102)", "Fire (101)")
        AlertDialog.Builder(this)
            .setTitle("Emergency Call")
            .setItems(options) { _, which ->
                val number = when (which) { 0 -> "100"; 1 -> "102"; else -> "101" }
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
            }
            .show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else { startActivity(Intent(MediaStore.ACTION_IMAGE_CAPTURE)) }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (isEmergencyMode) {
            AlertDialog.Builder(this)
                .setTitle("Exit Emergency?")
                .setMessage("Return to login screen?")
                .setPositiveButton("Yes") { _, _ -> finish() }
                .setNegativeButton("No", null).show()
        } else { super.onBackPressed() }
    }
}