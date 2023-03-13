package com.atoms.purityhubserviceman.model



data class StateCity(
    val data: List<StateCityData>,
    val message: String,
    val status: Int,
    val success: Boolean
)