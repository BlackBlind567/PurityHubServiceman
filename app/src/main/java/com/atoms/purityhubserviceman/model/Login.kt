package com.atoms.purityhubserviceman.model

data class Login(
    val data: LoginData,
    val message: String,
    val status: Int,
    val success: Boolean,

    )