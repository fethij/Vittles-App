package com.example.vittles.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/** @suppress */
@Module(includes = [DataModule::class])
class AppModule(private val application: Application) {

    @Singleton
    @Provides
    fun provideContext(): Context = application.applicationContext
}