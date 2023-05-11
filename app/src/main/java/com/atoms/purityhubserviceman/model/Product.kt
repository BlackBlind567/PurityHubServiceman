package com.atoms.purityhubserviceman.model

data class Product(
    val data: ProductData,
    val message: String,
    val status: Int,
    val success: Boolean
)