package com.atoms.purityhubserviceman.model

data class VerifyOtpData(
    val brands: ArrayList<OtpBrand>,
    val service_category: ArrayList<OtpServiceCategory>,
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