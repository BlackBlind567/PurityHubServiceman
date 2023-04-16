package com.atoms.purityhubserviceman.model

data class VerifyOtpData(
    val brands: List<OtpBrand>,
    val service_category: List<OtpServiceCategory>,
    val address: String,
    val city_id: String,
    val city_name: String,
    val email: String,
    val mobile: String,
    val name: String,
    val online: String,
    val otp_id: Int,
    val role_id: Int,
    val state_id: String,
    val state_name: String,
    val token: String,
    val type: String
)