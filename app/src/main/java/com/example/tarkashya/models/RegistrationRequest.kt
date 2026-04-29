package com.example.tarkashya.models

data class RegistrationRequest(
    val userDetails: UserDetailsModel,
    val relativeDetails: RelativeDetailsModel,
    val loginPin: String
)