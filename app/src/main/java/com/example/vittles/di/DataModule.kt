package com.example.vittles.di

import android.content.Context
import com.example.data.retrofit.off.OffApi
import com.example.data.retrofit.off.OffApiService
import com.example.data.retrofit.tsco.TscoApi
import com.example.data.retrofit.tsco.TscoApiService
import com.example.data.room.productdictionary.BarcodesRepositoryImpl
import com.example.data.room.productdictionary.BarcodeDao
import com.example.data.room.productdictionary.ProductDictionaryModelMapper
import com.example.data.room.createBarcodeDaoImpl
import com.example.data.room.product.ProductDao
import com.example.data.room.product.ProductModelMapper
import com.example.data.room.wastereport.WasteReportModelMapper
import com.example.data.room.createProductDaoImpl
import com.example.data.settings.SharedPrefsSettingsRepository
import com.example.domain.settings.SettingsRepository
import com.example.data.room.product.ProductsRepositoryImpl
import com.example.data.room.wastereport.WasteReportRepositoryImpl
import com.example.domain.barcode.BarcodesRepository
import com.example.domain.product.ProductsRepository
import com.example.domain.wasteReport.WasteReportRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/** @suppress */
@Module
class DataModule {

    @Singleton
    @Provides
    fun provideProductDaoImpl(context: Context) =
        createProductDaoImpl(context)

    @Singleton
    @Provides
    fun provideBarcodeDaoImpl(context: Context) =
        createBarcodeDaoImpl(context)

    @Singleton
    @Provides
    fun provideTscoProductsApiService() = TscoApi.createApi()

    @Singleton
    @Provides
    fun provideOffProductsApiService() = OffApi.createApi()

    @Singleton
    @Provides
    fun provideProductsRepository(
        productDao: ProductDao,
        mapper: ProductModelMapper
    ): ProductsRepository = ProductsRepositoryImpl(productDao, mapper)

    @Singleton
    @Provides
    fun provideSettingsRepository(
        context: Context
    ): SettingsRepository = SharedPrefsSettingsRepository(context)

    @Singleton
    @Provides
    fun provideBarcodesRepository(
        barcodeDao: BarcodeDao,
        productsApiTSCO: TscoApiService,
        productsApiOFF: OffApiService,
        mapper: ProductDictionaryModelMapper
    ): BarcodesRepository = BarcodesRepositoryImpl(
        barcodeDao,
        productsApiTSCO,
        productsApiOFF,
        mapper
    )

    @Singleton
    @Provides
    fun provideWasteReportRepository(productDao: ProductDao, mapper: WasteReportModelMapper): WasteReportRepository =
        WasteReportRepositoryImpl(productDao, mapper)
}