package com.example.data.retrofit.tsco

import com.google.gson.annotations.SerializedName

/**
 * API Result class for the Tesco API
 *
 * @property products Products found with the given parameters.
 */
@Suppress("ArrayInDataClass")
data class TescoResult(
    @SerializedName("products") var products: Array<TescoProduct>?
)