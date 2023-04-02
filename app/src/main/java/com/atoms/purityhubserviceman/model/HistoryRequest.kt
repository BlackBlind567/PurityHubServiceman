package com.myapplication.model

import com.atoms.purityhubserviceman.model.ServiceRequestData

data class HistoryRequest(
    val data: List<ServiceRequestData>,
    val message: String,
    val status: Int,
    val success: Boolean
)