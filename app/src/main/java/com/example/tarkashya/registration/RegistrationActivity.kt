package com.example.tarkashya.registration

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tarkashya.R
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // 1. Initialize User Detail Views
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

        // 2. Initialize Relative Detail Views
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

        val btnNext = findViewById<Button>(R.id.btnNext)

        // 3. Set up Date Pickers
        etUserDob.setOnClickListener { showDatePicker(etUserDob, etUserAge) }
        etRelDob.setOnClickListener { showDatePicker(etRelDob, etRelAge) }

        // 4. Set up Dropdowns (Example Data)
        val states = arrayOf("Maharashtra", "Gujarat", "Karnataka", "Delhi")
        val bloodGroups = arrayOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
        val relations = arrayOf("Father", "Mother", "Spouse", "Sibling")

        autoState.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, states))
        autoRelState.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, states))
        autoRelBloodGroup.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, bloodGroups))
        autoRelRelationship.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, relations))

        // 5. Next Button Logic
        btnNext.setOnClickListener {
            // Intent to open Step 2
            val intent = Intent(this, RegistrationStep2Activity::class.java)
            startActivity(intent)
        }

    }

    private fun showDatePicker(dateField: TextInputEditText, ageField: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val dob = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            dateField.setText(dob)

            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val age = currentYear - selectedYear
            ageField.setText(age.toString())
        }, year, month, day)

        dpd.show()
    }
}