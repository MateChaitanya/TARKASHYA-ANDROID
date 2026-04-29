
package com.example.tarkashya.registration

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tarkashya.R
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // User Views
        val etFullName = findViewById<TextInputEditText>(R.id.etFullName)
        val etMobile = findViewById<TextInputEditText>(R.id.etMobile)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etUserDob = findViewById<TextInputEditText>(R.id.etUserDob)
        val etUserAge = findViewById<TextInputEditText>(R.id.etUserAge)
        val etAadhar = findViewById<TextInputEditText>(R.id.etAadhar)
        val autoState = findViewById<AutoCompleteTextView>(R.id.autoState)
        val autoCity = findViewById<AutoCompleteTextView>(R.id.autoCity)
        val etPincode = findViewById<TextInputEditText>(R.id.etPincode)
        val etAddress = findViewById<TextInputEditText>(R.id.etAddress)
        val toggleUserGender = findViewById<MaterialButtonToggleGroup>(R.id.toggleUserGender)

        // Relative Views
        val etRelName = findViewById<TextInputEditText>(R.id.etRelName)
        val etRelMobile = findViewById<TextInputEditText>(R.id.etRelMobile)
        val etRelEmail = findViewById<TextInputEditText>(R.id.etRelEmail)
        val etRelDob = findViewById<TextInputEditText>(R.id.etRelDob)
        val etRelAge = findViewById<TextInputEditText>(R.id.etRelAge)
        val etRelAadhar = findViewById<TextInputEditText>(R.id.etRelAadhar)
        val autoRelState = findViewById<AutoCompleteTextView>(R.id.autoRelState)
        val autoRelCity = findViewById<AutoCompleteTextView>(R.id.autoRelCity)
        val etRelPincode = findViewById<TextInputEditText>(R.id.etRelPincode)
        val etRelAddress = findViewById<TextInputEditText>(R.id.etRelAddress)
        val autoRelBloodGroup = findViewById<AutoCompleteTextView>(R.id.autoRelBloodGroup)
        val autoRelRelationship = findViewById<AutoCompleteTextView>(R.id.autoRelRelationship)
        val toggleRelGender = findViewById<MaterialButtonToggleGroup>(R.id.toggleRelGender)

        // --- BLOCKING FILTERS ---
        etMobile.filters = arrayOf(InputFilter.LengthFilter(10))
        etRelMobile.filters = arrayOf(InputFilter.LengthFilter(10))
        etAadhar.filters = arrayOf(InputFilter.LengthFilter(12))
        etRelAadhar.filters = arrayOf(InputFilter.LengthFilter(12))
        etPincode.filters = arrayOf(InputFilter.LengthFilter(6))
        etRelPincode.filters = arrayOf(InputFilter.LengthFilter(6))

        // --- DATE PICKER LOGIC ---
        etUserDob.setOnClickListener { showDatePicker(etUserDob, etUserAge) }
        etRelDob.setOnClickListener { showDatePicker(etRelDob, etRelAge) }

        // --- DROPDOWN ADAPTERS ---
        val states = arrayOf("Maharashtra", "Delhi", "Karnataka", "Gujarat", "Uttar Pradesh", "Tamil Nadu")
        val cities = arrayOf("Pune", "Mumbai", "Bangalore", "Delhi", "Ahmedabad", "Lucknow")
        val bloodGroups = arrayOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
        val relationships = arrayOf("Father", "Mother", "Spouse", "Sibling", "Guardian")

        val stateAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, states)
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cities)
        val bloodAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, bloodGroups)
        val relationAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, relationships)

        autoState.setAdapter(stateAdapter)
        autoRelState.setAdapter(stateAdapter)
        autoCity.setAdapter(cityAdapter)
        autoRelCity.setAdapter(cityAdapter)
        autoRelBloodGroup.setAdapter(bloodAdapter)
        autoRelRelationship.setAdapter(relationAdapter)

        findViewById<Button>(R.id.btnNext).setOnClickListener {
            if (validateStrictly()) {
                val intentNext = Intent(this, RegistrationStep2Activity::class.java)

                val userGender = if (toggleUserGender.checkedButtonId == R.id.btnMale) "Male" else "Female"
                val relGender = if (toggleRelGender.checkedButtonId == R.id.btnRelMale) "Male" else "Female"

                // User Data
                intentNext.putExtra("fullName", etFullName.text.toString().trim())
                intentNext.putExtra("mobileNumber", etMobile.text.toString().trim())
                intentNext.putExtra("emailId", etEmail.text.toString().trim().lowercase())
                intentNext.putExtra("dob", etUserDob.text.toString())
                intentNext.putExtra("age", etUserAge.text.toString().toIntOrNull() ?: 0)
                intentNext.putExtra("gender", userGender)
                intentNext.putExtra("aadharUid", etAadhar.text.toString().trim())
                intentNext.putExtra("state", autoState.text.toString())
                intentNext.putExtra("city", autoCity.text.toString())
                intentNext.putExtra("pincode", etPincode.text.toString().trim())
                intentNext.putExtra("address", etAddress.text.toString().trim())

                // Relative Data
                intentNext.putExtra("relName", etRelName.text.toString().trim())
                intentNext.putExtra("relMobile", etRelMobile.text.toString().trim())
                intentNext.putExtra("relEmail", etRelEmail.text.toString().trim().lowercase())
                intentNext.putExtra("relDob", etRelDob.text.toString())
                intentNext.putExtra("relAge", etRelAge.text.toString().toIntOrNull() ?: 0)
                intentNext.putExtra("relGender", relGender)
                intentNext.putExtra("relAadharUid", etRelAadhar.text.toString().trim())
                intentNext.putExtra("relState", autoRelState.text.toString())
                intentNext.putExtra("relCity", autoRelCity.text.toString())
                intentNext.putExtra("relPincode", etRelPincode.text.toString().trim())
                intentNext.putExtra("relAddress", etRelAddress.text.toString().trim())
                intentNext.putExtra("relBloodGroup", autoRelBloodGroup.text.toString())
                intentNext.putExtra("relationship", autoRelRelationship.text.toString())

                startActivity(intentNext)
            }
        }
    }

    private fun showDatePicker(dateField: TextInputEditText, ageField: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
            dateField.setText(formattedDate)

            // Calculate Age
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val age = currentYear - selectedYear
            ageField.setText(age.toString())
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun validateStrictly(): Boolean {
        val email = findViewById<TextInputEditText>(R.id.etEmail).text.toString().trim()
        val mobile = findViewById<TextInputEditText>(R.id.etMobile).text.toString().trim()
        val aadhar = findViewById<TextInputEditText>(R.id.etAadhar).text.toString().trim()
        val pin = findViewById<TextInputEditText>(R.id.etPincode).text.toString().trim()

        if (mobile.length != 10) {
            findViewById<TextInputEditText>(R.id.etMobile).error = "Mobile must be 10 digits"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.contains("@")) {
            findViewById<TextInputEditText>(R.id.etEmail).error = "Enter a valid email"
            return false
        }

        if (aadhar.length != 12) {
            findViewById<TextInputEditText>(R.id.etAadhar).error = "Aadhar must be 12 digits"
            return false
        }
        if (pin.length != 6) {
            findViewById<TextInputEditText>(R.id.etPincode).error = "Pincode must be 6 digits"
            return false
        }
        return true
    }
}