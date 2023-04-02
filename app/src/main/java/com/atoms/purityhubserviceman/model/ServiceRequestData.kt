package com.atoms.purityhubserviceman.model

data class ServiceRequestData(
    val address: String,
    val brand: String,
    val city: String,
    val created_at: String,
    val geo_tag: String,
    val id: Int,
    val image: Any,
    val priority: String,
    val problem_type: String,
    val remark: String,
    val service_category: String,
    val serviceman: String,
    val state: String,
    val user: String,
    var expanded: Boolean = false
) {

}