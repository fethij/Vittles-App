package com.example.data.retrofit.tsco

import com.example.data.BuildConfig
import net.danlew.android.joda.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * API class that instantiates the API service.
 *
 */
class TscoApi {
    companion object {
        /**
         * Base url of the API.
         */
        private const val baseUrl = "https://dev.tescolabs.com/"

        /**
         * Creates an instance of the [TscoApiService].
         *
         * @return [TscoApiService] The service class of the retrofit client.
         */
        fun createApi(): TscoApiService {
            // Create an OkHttpClient to be able to make a log of the network traffic
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Ocp-Apim-Subscription-Key", BuildConfig.TescoAPIKey)
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

            // Return the Retrofit TscoApiService
            return productsApi.create(TscoApiService::class.java)
        }
    }
}