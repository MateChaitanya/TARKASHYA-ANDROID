package com.example.tarkashya

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import com.example.tarkashya.models.UserDetailsModel     // ✅ Fixes Model reference
import com.example.tarkashya.network.RetrofitClient      // ✅ Fixes Client reference
import com.example.tarkashya.network.ApiService

class ScannerActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private var isScanning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        previewView = findViewById(R.id.previewView)
        val btnManual = findViewById<Button>(R.id.btnManualDetails)

        btnManual.setOnClickListener {
            // Optional: Navigate to a manual form
            Toast.makeText(this, "Manual entry coming soon", Toast.LENGTH_SHORT).show()
        }

        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer
                )
            } catch (exc: Exception) {
                Toast.makeText(this, "Use Case Binding Failed", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null && !isScanning) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { data ->
                            isScanning = true
                            fetchUserDetails(data)
                        }
                    }
                }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }

    private fun fetchUserDetails(userId: String) {
        // userId should be the ID found in the QR (e.g., "USER_123")
        // Logic to call Spring Boot via Retrofit:
        RetrofitClient.instance.getUserDetails(userId).enqueue(object : retrofit2.Callback<UserDetailsModel> {
            override fun onResponse(call: retrofit2.Call<UserDetailsModel>, response: retrofit2.Response<UserDetailsModel>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@ScannerActivity, ProfileActivity::class.java)
                    intent.putExtra("isEmergencyMode", true)
                    intent.putExtra("USER_DATA", response.body()) // Ensure UserDetailsModel is Parcelable
                    startActivity(intent)
                    finish()
                } else {
                    isScanning = false
                    Toast.makeText(this@ScannerActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<UserDetailsModel>, t: Throwable) {
                isScanning = false
                Toast.makeText(this@ScannerActivity, "Server error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}