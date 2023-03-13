package com.myapplication.model

data class HistoryRequest(
    val data: List<ServiceRequestData>,
    val message: String,
    val status: Int,
    val success: Boolean
)