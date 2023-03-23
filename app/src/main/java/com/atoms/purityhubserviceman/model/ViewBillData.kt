package com.atoms.purityhubserviceman.model

data class ViewBillData(
    val created_at: String,
    val id: Int,
    val price: Int,
    val product_name: String,
    val quantity: Int,
    val status: Int,
    val total_amount: Int
)