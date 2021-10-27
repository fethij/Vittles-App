package com.example.data.retrofit.off

import com.google.gson.annotations.SerializedName

/**
 * API Result class for the Open Food Facts API.
 *
 *
 * @property product The retrieved product.
 */
data class OffResult (
    @SerializedName("status") var status: Int,
    @SerializedName("product") var product: OffProduct?
)
