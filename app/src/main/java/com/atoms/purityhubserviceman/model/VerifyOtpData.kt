package com.atoms.purityhubserviceman.model

data class VerifyOtpData(
    val otp_id: Int,
    val type: String,
    val token: String,
    val name: String,
    val email: String,
    val mobile: String
)