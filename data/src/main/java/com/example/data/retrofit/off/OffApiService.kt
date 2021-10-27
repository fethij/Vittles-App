package com.example.data.retrofit.off

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service that connects to the endpoints of the API.
 *
 */
interface OffApiService {

    /**
     * Connects to the endpoint that retrieves product data based on a GTIN code.
     *
     * @param barcode The GTIN code of the product.
     * @return A [OffProduct] object with the retrieved data.
     */
    @Headers("Accept: application/json")
    @GET("product/{gtin}.json")
    fun getProductName(@Path(value = "gtin", encoded = true) barcode: String): Observable<OffResult>
}