package com.atoms.purityhubserviceman.model


data class BrandCategory(
    val data: List<BrandCategoryData>,
    val message: String,
    val status: Int,
    val success: Boolean
)