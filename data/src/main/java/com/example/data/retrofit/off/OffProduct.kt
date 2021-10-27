package com.example.data.retrofit.off

import com.google.gson.annotations.SerializedName

/**
 * API Result class for the Open Food Facts API.
 *
 *
 * @property barcode The barcode of the product.
 * @property productName The product name of the product in it's origin language.
 */
data class OffProduct (
    @SerializedName("code") var barcode: String?,
    @SerializedName("product_name") var productName: String?
)