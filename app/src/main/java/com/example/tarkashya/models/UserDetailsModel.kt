package com.example.tarkashya.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetailsModel(
    // Original Fields (Do not remove)
    val fullName: String,
    val mobileNumber: String,
    val emailId: String,
    val dob: String,
    val age: Int,
    val gender: String,
    val aadharUid: String,
    val state: String,
    val city: String,
    val pincode: String,
    val address: String,

    // New Fields needed for Emergency Scanner Mode
    val bloodGroup: String = "N/A",
    val allergies: String = "None",
    val emergencyContact: String = "N/A",
    val medicalConditions: String = "None",
    val qrIdentifier: String = ""
) : Parcelable