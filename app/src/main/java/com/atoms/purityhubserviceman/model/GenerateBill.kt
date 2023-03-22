package com.atoms.purityhubserviceman.model

data class GenerateBill(
    val productId: String,
    val productName: String,
    val productMrp: String,
    val productQuantity: String,
    val productTotalItemPrice: String

) {
    override fun toString(): String {
        return "GenerateBill(productId='$productId', productName='$productName', productMrp='$productMrp', productQuantity='$productQuantity', productTotalItemPrice='$productTotalItemPrice')"
    }
}