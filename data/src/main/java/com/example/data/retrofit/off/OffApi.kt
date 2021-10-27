package com.example.data.retrofit.off

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * API class that instantiates the API service.
 *
 */
class OffApi {
    companion object {
        /**
         * Base url of the API.
         */
        private const val baseUrl = "https://world.openfoodfacts.org/api/v0/"

        /**
         * Creates an instance of the [OffApiService].
         *
         * @return [OffApiService] The service class of the retrofit client.
         */
        fun createApi(): OffApiService {
            // Create an OkHttpClient to be able to make a log of the network traffic
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader(
                            "UserAgent",
                            "Vittles - Android - Alpha01"
                        )
                        .build()
                    chain.proceed(request)
                }
                .build()

            // Create the Retrofit instance
            val productsApi = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            // Return the Retrofit OffApiService
            return productsApi.create(OffApiService::class.java)
        }
    }
}