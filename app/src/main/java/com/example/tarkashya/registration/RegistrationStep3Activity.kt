//package com.example.tarkashya.registration
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.tarkashya.LoginActivity  // Import the LoginActivity
//import com.example.tarkashya.R
//
//class RegistrationStep3Activity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // Ensure this matches your Step 3 XML file name
//        setContentView(R.layout.view_reg_step_3)
//
//        val btnRegister = findViewById<Button>(R.id.btnRegister)
//
//        btnRegister.setOnClickListener {
//            // 1. Logic to save data (PIN, emergency contacts, etc.) would go here
//
//            // 2. Show a success message to the user
//            Toast.makeText(this, "Registration Successful! Please login.", Toast.LENGTH_LONG).show()
//
//            // 3. Navigate to LoginActivity instead of MainActivity
//            val intent = Intent(this, LoginActivity::class.java)
//
//            // 4. IMPORTANT: This clears all previous registration screens from memory
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//
//            startActivity(intent)
//
//            // 5. Close this activity
//            finish()
//        }
//    }
//}

package com.example.tarkashya.registration

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tarkashya.LoginActivity
import com.example.tarkashya.R
import com.example.tarkashya.models.RegistrationRequest
import com.example.tarkashya.models.UserDetailsModel
import com.example.tarkashya.models.RelativeDetailsModel
import com.example.tarkashya.repository.RegistrationRepository
import com.google.android.material.checkbox.MaterialCheckBox

class RegistrationStep3Activity : AppCompatActivity() {

    private val repository = RegistrationRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_reg_step_3)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val checkBoxConsent = findViewById<MaterialCheckBox>(R.id.checkbox_consent)
        val pins = arrayOf(
            findViewById<EditText>(R.id.pin_1),
            findViewById<EditText>(R.id.pin_2),
            findViewById<EditText>(R.id.pin_3),
            findViewById<EditText>(R.id.pin_4)
        )

        setupPinAutoJump(pins)

        btnRegister.setOnClickListener {
            if (!checkBoxConsent.isChecked) {
                Toast.makeText(this, "Accept consent terms", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userPin = pins.joinToString("") { it.text.toString() }
            if (userPin.length < 4) {
                Toast.makeText(this, "Enter 4-digit PIN", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userDetails = UserDetailsModel(
                fullName = intent.getStringExtra("fullName") ?: "",
                mobileNumber = intent.getStringExtra("mobileNumber") ?: "",
                emailId = intent.getStringExtra("emailId") ?: "",
                dob = intent.getStringExtra("dob") ?: "",
                age = intent.getIntExtra("age", 0),
                gender = intent.getStringExtra("gender") ?: "Male",
                aadharUid = intent.getStringExtra("aadharUid") ?: "",
                state = intent.getStringExtra("state") ?: "",
                city = intent.getStringExtra("city") ?: "",
                pincode = intent.getStringExtra("pincode") ?: "",
                address = intent.getStringExtra("address") ?: ""
            )

            val relativeDetails = RelativeDetailsModel(
                relName = intent.getStringExtra("relName") ?: "",
                relMobile = intent.getStringExtra("relMobile") ?: "",
                relEmail = intent.getStringExtra("relEmail") ?: "",
                relDob = intent.getStringExtra("relDob") ?: "",
                relAge = intent.getIntExtra("relAge", 0),
                relGender = intent.getStringExtra("relGender") ?: "Female",
                relAadharUid = intent.getStringExtra("relAadharUid") ?: "",
                relState = intent.getStringExtra("relState") ?: "",
                relCity = intent.getStringExtra("relCity") ?: "",
                relPincode = intent.getStringExtra("relPincode") ?: "",
                relAddress = intent.getStringExtra("relAddress") ?: "",
                relBloodGroup = intent.getStringExtra("relBloodGroup") ?: "",
                relationship = intent.getStringExtra("relationship") ?: ""
            )

            repository.sendRegistrationData(RegistrationRequest(userDetails, relativeDetails, userPin)) { message ->
                runOnUiThread {
                    if (message?.contains("successfully", true) == true) {
                        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setupPinAutoJump(pins: Array<EditText>) {
        pins.forEachIndexed { i, pin ->
            pin.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < pins.size - 1) pins[i + 1].requestFocus()
                }
            })
        }
    }
}