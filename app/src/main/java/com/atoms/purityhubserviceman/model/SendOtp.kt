package com.atoms.purityhubserviceman.model

data class SendOtp(
    val `data`: String,
    val message: String,
    val status: Int,
    val success: Boolean
)