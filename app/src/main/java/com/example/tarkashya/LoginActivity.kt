package com.example.tarkashya

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.tarkashya.network.RetrofitClient
import com.example.tarkashya.registration.RegistrationActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val iconVisibility = findViewById<ImageView>(R.id.iconVisibility)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        val tvForgotPin = findViewById<TextView>(R.id.tvForgotPin)
        val btnBiometric = findViewById<LinearLayout>(R.id.btnBiometric)
        val btnEmergencyNoLogin = findViewById<LinearLayout>(R.id.btnEmergencyNoLogin)

        etPhone.filters = arrayOf(InputFilter.LengthFilter(10))
        etPassword.filters = arrayOf(InputFilter.LengthFilter(4))

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Note: For real biometric login, you'd usually verify a stored token or pin
                    navigateToMain(false, mutableMapOf("fullName" to "User"))
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Log in using your fingerprint")
            .setNegativeButtonText("Use PIN instead")
            .build()

        btnBiometric.setOnClickListener {
            val biometricManager = BiometricManager.from(this)
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                Toast.makeText(this, "Biometric unavailable", Toast.LENGTH_SHORT).show()
            }
        }

        btnEmergencyNoLogin.setOnClickListener {
            navigateToMain(true, mutableMapOf("fullName" to "Guest"))
        }

        btnLogin.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            val pin = etPassword.text.toString().trim()

            if (phone.length == 10 && pin.length == 4) {
                val loginRequest = mapOf("mobileNumber" to phone, "loginPin" to pin)
                btnLogin.isEnabled = false
                btnLogin.text = "Verifying..."

                RetrofitClient.instance.loginUser(loginRequest).enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                        btnLogin.isEnabled = true
                        btnLogin.text = "LOGIN"
                        if (response.isSuccessful) {
                            val userMap = response.body() ?: emptyMap()
                            navigateToMain(false, userMap)
                        } else {
                            val errorMsg = if (response.code() == 404) "User not found" else "Invalid PIN"
                            Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        btnLogin.isEnabled = true
                        btnLogin.text = "LOGIN"
                        Toast.makeText(this@LoginActivity, "Connection Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Enter 10-digit Phone & 4-digit PIN", Toast.LENGTH_SHORT).show()
            }
        }

        iconVisibility.setOnClickListener {
            if (isPasswordVisible) {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                iconVisibility.setImageResource(android.R.drawable.ic_menu_view) // Replace with your ic_eye_visible
            } else {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                iconVisibility.setImageResource(android.R.drawable.ic_menu_view) // Replace with your ic_eye_visible
            }
            isPasswordVisible = !isPasswordVisible
            etPassword.setSelection(etPassword.text.length)
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        tvForgotPin.setOnClickListener {
            Toast.makeText(this, "Feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMain(isEmergency: Boolean, userMap: Map<String, Any>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("isEmergencyMode", isEmergency)

        // Passing all Database fields to MainActivity to be used in Profile
        intent.putExtra("USER_NAME", userMap["fullName"]?.toString() ?: "User")
        intent.putExtra("USER_ID", userMap["id"]?.toString() ?: "8892")
        intent.putExtra("DOB", userMap["dob"]?.toString() ?: "Not Set")
        intent.putExtra("GENDER", userMap["gender"]?.toString() ?: "Not Set")
        intent.putExtra("BLOOD_GROUP", userMap["bloodGroup"]?.toString() ?: "Not Set")
        intent.putExtra("PHONE", userMap["mobileNumber"]?.toString() ?: "Not Set")

        // Medical Info
        intent.putExtra("ALLERGIES", userMap["allergies"]?.toString() ?: "None")
        intent.putExtra("CONDITIONS", userMap["medicalConditions"]?.toString() ?: "None")

        // Handle Nested Relative Info (Emergency Contact)
        val relative = userMap["relative"] as? Map<*, *>
        intent.putExtra("REL_NAME", relative?.get("relName")?.toString() ?: "Not Set")
        intent.putExtra("REL_PHONE", relative?.get("relMobile")?.toString() ?: "Not Set")

        startActivity(intent)
        finish()
    }
}