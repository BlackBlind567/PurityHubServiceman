package com.atoms.purityhubserviceman.model


data class Product(
    val data:  List<ProductData>,
    val message: String,
    val status: Int,
    val success: Boolean
)