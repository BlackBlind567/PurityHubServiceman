package com.atoms.purityhubserviceman.model

data class GenerateBill(
    val productId: String,
    val productName: String,
    val productMrp: String,
    val productQuantity: String

)