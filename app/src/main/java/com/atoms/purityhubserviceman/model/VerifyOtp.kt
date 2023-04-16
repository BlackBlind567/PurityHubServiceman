package com.atoms.purityhubserviceman.model

data class VerifyOtp(
    val data: VerifyOtpData,
    val message: String,
    val status: Int,
    val success: Boolean
)