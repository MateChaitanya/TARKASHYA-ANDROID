package com.example.tarkashya

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvAddress: TextView
    private lateinit var drawerLayout: DrawerLayout
    private var isEmergencyMode = false

    // NEW: Prevents multiple profile screens opening at once
    private var isScanning = false

    private lateinit var cameraExecutor: ExecutorService

    private val CAMERA_PERMISSION_CODE = 102
    private val LOCATION_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isEmergencyMode = intent.getBooleanExtra("isEmergencyMode", false)
        val userName = intent.getStringExtra("USER_NAME") ?: "User"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        tvAddress = findViewById(R.id.tvLocationAddress)
        drawerLayout = findViewById(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)
        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
        val btnBottomStatus = findViewById<Button>(R.id.bottomButton)

        cameraExecutor = Executors.newSingleThreadExecutor()

        val headerView = navView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.tvHeaderName).text = userName

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else if (isEmergencyMode) {
                    showExitEmergencyDialog()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        if (isEmergencyMode) setupEmergencyUI(menuIcon, btnBottomStatus)

        menuIcon.setOnClickListener {
            if (!isEmergencyMode) drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_profile -> navigateWithState(ProfileActivity::class.java)
                R.id.nav_logout -> logout()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // --- NEW: Bottom Button Logic ---
        btnBottomStatus.setOnClickListener {
            // Replace with the name of your Alert Status Activity
            val intent = Intent(this, AlertStatusActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnScanQr).setOnClickListener {
            if (isEmergencyMode) showDisabledToast() else checkCameraPermission()
        }

        findViewById<LinearLayout>(R.id.btnMyQr).setOnClickListener {
            navigateWithState(MyQRCodeActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.btnCallContacts).setOnClickListener {
            showEmergencyDialog()
        }

        findViewById<LinearLayout>(R.id.btnSafetyTips).setOnClickListener {
            if (isEmergencyMode) showDisabledToast()
            else navigateWithState(SafetyTipsActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.sosButton).setOnLongClickListener {
            triggerEmergencyAlert()
            true
        }

        checkLocationPermission()
    }

    // ✅ FIXED: Checks permission and then opens the dedicated ScannerActivity
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            openScanner()
        }
    }

    // ✅ FIXED: Starts the ScannerActivity once permission is granted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openScanner()
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ✅ NEW: Helper function to navigate to your Scanner screen
    private fun openScanner() {
        val intent = Intent(this, ScannerActivity::class.java)
        intent.putExtra("isEmergencyMode", isEmergencyMode)
        startActivity(intent)
    }

    // ✅ These stay here for secondary processing if needed,
    // but primary scanning now happens in ScannerActivity
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val imageAnalyzer = ImageAnalysis.Builder().build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        if (isScanning) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image ?: return
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let {
                        isScanning = true
                        handleQrResult(it)
                        return@addOnSuccessListener
                    }
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }

    private fun handleQrResult(data: String) {
        runOnUiThread {
            Toast.makeText(this, "Scanned: $data", Toast.LENGTH_SHORT).show()
        }
        val intentToProfile = Intent(this, ProfileActivity::class.java)
        intentToProfile.putExtra("isEmergencyMode", true)
        intentToProfile.putExtra("SCANNED_USER_ID", data)
        startActivity(intentToProfile)
    }

    override fun onResume() {
        super.onResume()
        isScanning = false
    }

    // --- LOCATION ---
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE)
        } else getLastLocation()
    }

    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val geocoder = Geocoder(this, Locale.getDefault())
                val address = geocoder.getFromLocation(
                    it.latitude, it.longitude, 1
                )
                if (!address.isNullOrEmpty()) {
                    tvAddress.text = address[0].getAddressLine(0)
                }
            }
        }
    }

    // --- UTIL ---
    private fun triggerEmergencyAlert() {
        val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000)
    }

    private fun showEmergencyDialog() {
        val options = arrayOf("Police (100)", "Ambulance (102)", "Fire (101)")
        AlertDialog.Builder(this)
            .setTitle("Emergency Call")
            .setItems(options) { _, which ->
                val num = when (which) {
                    0 -> "100"
                    1 -> "102"
                    else -> "101"
                }
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num")))
            }.show()
    }

    private fun setupEmergencyUI(menuIcon: ImageView, bottomBtn: Button) {
        menuIcon.visibility = View.GONE
        bottomBtn.visibility = View.VISIBLE
    }

    private fun showDisabledToast() =
        Toast.makeText(this, "Action disabled", Toast.LENGTH_SHORT).show()

    private fun navigateWithState(target: Class<*>) {
        val nextIntent = Intent(this, target)
        nextIntent.putExtra("isEmergencyMode", isEmergencyMode)
        intent.extras?.let {
            nextIntent.putExtras(it)
        }
        startActivity(nextIntent)
    }

    private fun showExitEmergencyDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit Emergency?")
            .setPositiveButton("Yes") { _, _ -> finish() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun logout() {
        val logoutIntent = Intent(this, LoginActivity::class.java)
        logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(logoutIntent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}