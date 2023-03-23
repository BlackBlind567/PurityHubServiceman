package com.atoms.purityhubserviceman.model

data class ViewBill(
    val data: List<ViewBillData>,
    val summery: ViewBillSummery,
    val message: String,
    val status: Int,
    val success: Boolean
)