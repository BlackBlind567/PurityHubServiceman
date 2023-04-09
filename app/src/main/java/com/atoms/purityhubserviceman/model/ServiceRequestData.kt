package com.atoms.purityhubserviceman.model

data class ServiceRequestData(
    val address: String,
    val branch_id: Int,
    val brand: String,
    val city: String,
    val close_datetime: String,
    val created_at: String,
    val geo_tag: String,
    val grand_total: Int,
    val id: Int,
    val image: String,
    val open_datetime: String,
    val otp: String,
    val priority: String,
    val problem_type: String,
    val remark: String,
    val service_category: String,
    val serviceman: String,
    val serviceman_mobile: String,
    val star: String,
    val state: String,
    val status: String,
    val user: String,
    val user_mobile: String,
    var expanded: Boolean = false
) {

}