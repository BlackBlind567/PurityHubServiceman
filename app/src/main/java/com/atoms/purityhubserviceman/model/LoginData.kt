package com.atoms.purityhubserviceman.model

data class LoginData(
    val created_at: String,
    val email: String,
    val name: String,
    val token: String,
    val vendor_id: Any
)