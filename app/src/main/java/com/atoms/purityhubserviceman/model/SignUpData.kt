package com.atoms.purityhubserviceman.model

data class SignUpData(
    val email: String,
    val mobile: String,
    val name: String,
    val otp_id: Int,
    val token: String
)