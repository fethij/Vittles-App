package com.example.data.retrofit.tsco

import com.google.gson.annotations.SerializedName

/**
 * API Result class for the Tesco API
 *
 * @property barcode The barcode of the product.
 * @property value The product name of the product.
 */
data class TescoProduct(
    @SerializedName("gtin") var barcode: String?,
    @SerializedName("description") var value: String?
)