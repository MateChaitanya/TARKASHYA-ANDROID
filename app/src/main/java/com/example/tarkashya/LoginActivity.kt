//package com.example.tarkashya
//
//import android.content.Intent
//import android.os.Bundle
//import android.text.InputFilter
//import android.text.method.HideReturnsTransformationMethod
//import android.text.method.PasswordTransformationMethod
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.biometric.BiometricManager
//import androidx.biometric.BiometricPrompt
//import androidx.core.content.ContextCompat
//import com.example.tarkashya.registration.RegistrationActivity
//import java.util.concurrent.Executor
//
//class LoginActivity : AppCompatActivity() {
//
//    private var isPasswordVisible = false
//    private lateinit var executor: Executor
//    private lateinit var biometricPrompt: BiometricPrompt
//    private lateinit var promptInfo: BiometricPrompt.PromptInfo
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        // 1. Initialize Views
//        val etPhone = findViewById<EditText>(R.id.etPhone)
//        val etPassword = findViewById<EditText>(R.id.etPassword)
//        val iconVisibility = findViewById<ImageView>(R.id.iconVisibility)
//        val btnLogin = findViewById<Button>(R.id.btnLogin)
//        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
//        val btnBiometric = findViewById<LinearLayout>(R.id.btnBiometric)
//
//        etPhone.filters = arrayOf(InputFilter.LengthFilter(10))
//        etPassword.filters = arrayOf(InputFilter.LengthFilter(4))
//
//        // 2. Setup Biometric Logic
//        executor = ContextCompat.getMainExecutor(this)
//        biometricPrompt = BiometricPrompt(this, executor,
//            object : BiometricPrompt.AuthenticationCallback() {
//                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//                    super.onAuthenticationError(errorCode, errString)
//                    Toast.makeText(applicationContext, "Auth error: $errString", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//                    super.onAuthenticationSucceeded(result)
//                    Toast.makeText(applicationContext, "Login Successful!", Toast.LENGTH_SHORT).show()
//                    // Navigate to Dashboard
//                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//                    finish()
//                }
//
//                override fun onAuthenticationFailed() {
//                    super.onAuthenticationFailed()
//                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
//                }
//            })
//
//        promptInfo = BiometricPrompt.PromptInfo.Builder()
//            .setTitle("Biometric Login")
//            .setSubtitle("Log in using your fingerprint")
//            .setNegativeButtonText("Use PIN instead")
//            .build()
//
//        // 3. Biometric Button Click
//        btnBiometric.setOnClickListener {
//            val biometricManager = BiometricManager.from(this)
//            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
//                BiometricManager.BIOMETRIC_SUCCESS ->
//                    biometricPrompt.authenticate(promptInfo)
//                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
//                    Toast.makeText(this, "No biometric hardware detected", Toast.LENGTH_SHORT).show()
//                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
//                    Toast.makeText(this, "Biometric hardware is currently unavailable", Toast.LENGTH_SHORT).show()
//                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
//                    Toast.makeText(this, "No fingerprints registered on this device", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // 4. Standard Login & Visibility (Keep your existing code)
//        iconVisibility.setOnClickListener {
//            if (isPasswordVisible) {
//                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
//            } else {
//                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
//            }
//            isPasswordVisible = !isPasswordVisible
//            etPassword.setSelection(etPassword.text.length)
//        }
//
//        btnLogin.setOnClickListener {
//            // Your existing validation logic here...
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }
//
//        tvSignUp.setOnClickListener {
//            startActivity(Intent(this, RegistrationActivity::class.java))
//        }
//    }
//}

package com.example.tarkashya

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.tarkashya.registration.RegistrationActivity
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 1. Initialize Views
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val iconVisibility = findViewById<ImageView>(R.id.iconVisibility)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        val tvForgotPin = findViewById<TextView>(R.id.tvForgotPin)
        val btnBiometric = findViewById<LinearLayout>(R.id.btnBiometric)
        val btnEmergencyNoLogin = findViewById<LinearLayout>(R.id.btnEmergencyNoLogin)

        // Input Constraints
        etPhone.filters = arrayOf(InputFilter.LengthFilter(10))
        etPassword.filters = arrayOf(InputFilter.LengthFilter(4))

        // 2. Setup Biometric Logic
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Auth error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    navigateToMain(false) // Regular login
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Log in using your fingerprint")
            .setNegativeButtonText("Use PIN instead")
            .build()

        // 3. Click Listeners

        // Biometric Login
        btnBiometric.setOnClickListener {
            val biometricManager = BiometricManager.from(this)
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> biometricPrompt.authenticate(promptInfo)
                else -> Toast.makeText(this, "Biometric unavailable or not set up", Toast.LENGTH_SHORT).show()
            }
        }

        // Emergency Access (The feature you requested)
        btnEmergencyNoLogin.setOnClickListener {
            // We pass 'true' to signal that the app is in Emergency Mode
            navigateToMain(true)
        }

        // Standard Login
        btnLogin.setOnClickListener {
            val phone = etPhone.text.toString()
            val pin = etPassword.text.toString()

            if (phone.length == 10 && pin.length == 4) {
                navigateToMain(false)
            } else {
                Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        // Password Visibility Toggle
        iconVisibility.setOnClickListener {
            if (isPasswordVisible) {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                iconVisibility.setImageResource(R.drawable.ic_eye_visible)
            } else {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                iconVisibility.setImageResource(R.drawable.ic_eye_visible) // Ensure you have correct drawable
            }
            isPasswordVisible = !isPasswordVisible
            etPassword.setSelection(etPassword.text.length)
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        tvForgotPin.setOnClickListener {
            Toast.makeText(this, "PIN recovery feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Navigates to MainActivity.
     * @param isEmergency If true, the dashboard will hide private info.
     */
    private fun navigateToMain(isEmergency: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("isEmergencyMode", isEmergency)
        startActivity(intent)
        finish()
    }
}