package com.example.focusstart.di

import com.example.focusstart.retrofit.RateService
import com.example.focusstart.retrofit.model.Rate
import com.example.focusstart.retrofit.model.RateDeserialazer
import com.example.focusstart.util.Constants
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RetrofitModule {

    @Binds
    abstract fun  bindJsonDeserializer(
        serializer: RateDeserialazer
    ): JsonDeserializer<@JvmSuppressWildcards Rate>

    companion object {
        @Provides
        fun provideUrl(): String {
            return Constants.BASE_URL
        }

        @Provides
        @Singleton
        fun provideJsonType():Type{
            return Rate::class.java
        }

        @Provides
        @Singleton
        fun provideGsonConverter(
            type: Type, typeAdapter: JsonDeserializer<@JvmSuppressWildcards Rate>
        ): GsonConverterFactory {
            val gson = GsonBuilder()
                .registerTypeAdapter(type, typeAdapter)
                .create()
            return GsonConverterFactory.create(gson)
        }

        @Provides
        @Singleton
        fun provideRetrofit(
            url: String,
            gsonConverter: GsonConverterFactory
        ): Retrofit.Builder {
            return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(gsonConverter)
        }

        @Provides
        @Singleton
        fun provideService(retrofit: Retrofit.Builder): RateService {
            return retrofit
                .build()
                .create(RateService::class.java)
        }
    }
}