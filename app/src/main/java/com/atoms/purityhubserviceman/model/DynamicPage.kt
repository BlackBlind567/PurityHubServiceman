package com.atoms.purityhubserviceman.model

data class DynamicPage(
    val data: DynamicPageData,
    val message: String,
    val status: Int,
    val success: Boolean
)