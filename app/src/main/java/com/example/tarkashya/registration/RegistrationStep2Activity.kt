//package com.example.tarkashya.registration
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import androidx.appcompat.app.AppCompatActivity
//import com.example.tarkashya.R
//
//class RegistrationStep2Activity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.layout_reg_step2)
//
//        val btnNext = findViewById<Button>(R.id.btnNext)
//
//        btnNext.setOnClickListener {
//            val intent = Intent(this, RegistrationStep3Activity::class.java)
//            startActivity(intent)
//        }
//    }
//}

package com.example.tarkashya.registration

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tarkashya.R
import com.google.android.material.button.MaterialButtonToggleGroup

class RegistrationStep2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_reg_step2)

        val btnNext = findViewById<Button>(R.id.btnNext)
        val genderToggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.genderToggleGroup)

        // These will now work because we added the IDs to the XML
        val autoBloodGroup = findViewById<AutoCompleteTextView>(R.id.autoBloodGroup)
        val autoMedical = findViewById<AutoCompleteTextView>(R.id.autoMedical)
        val autoAllergies = findViewById<AutoCompleteTextView>(R.id.autoAllergies)

        // Sample Data for Dropdowns
        val bloodGroups = arrayOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
        val medicalConditions = arrayOf("None", "Diabetes", "Hypertension", "Asthma", "Thyroid", "Heart Disease")
        val allergiesList = arrayOf("None", "Peanuts", "Dust", "Latex", "Penicillin", "Pollen")

        // Set Adapters
        autoBloodGroup.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, bloodGroups))
        autoMedical.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, medicalConditions))
        autoAllergies.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, allergiesList))

        btnNext.setOnClickListener {
            val intentNext = Intent(this, RegistrationStep3Activity::class.java)

            // Carry forward Step 1 data (Full Name, Mobile, etc.)
            intent.extras?.let { intentNext.putExtras(it) }

            // Capture Gender
            val selectedGender = when (genderToggleGroup.checkedButtonId) {
                R.id.btnMale -> "Male"
                R.id.btnFemale -> "Female"
                R.id.btnOther -> "Other"
                else -> "Male"
            }

            // Put Step 2 data into intent
            intentNext.putExtra("gender", selectedGender)
            intentNext.putExtra("bloodGroup", autoBloodGroup.text.toString())
            intentNext.putExtra("medicalConditions", autoMedical.text.toString())
            intentNext.putExtra("allergies", autoAllergies.text.toString())

            startActivity(intentNext)
        }
    }
}