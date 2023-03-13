package com.atoms.purityhubserviceman.model


data class SignUp(
    val data: SignUpData,
    val message: String,
    val status: Int,
    val success: Boolean
)