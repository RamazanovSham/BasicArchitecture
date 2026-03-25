package ru.otus.basicarchitecture.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.otus.basicarchitecture.BuildConfig
import ru.otus.basicarchitecture.network.dadata.DadataApi
import ru.otus.basicarchitecture.network.dadata.DadataAuthInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val DADATA_BASE_URL = "https://suggestions.dadata.ru/"

    @Provides
    @Singleton
    fun provideDadataAuthInterceptor(): DadataAuthInterceptor =
        DadataAuthInterceptor(
            apiKey = BuildConfig.DADATA_API_KEY,
            secretKey = BuildConfig.DADATA_SECRET_KEY
        )

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: DadataAuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(DADATA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideDadataApi(retrofit: Retrofit): DadataApi =
        retrofit.create(DadataApi::class.java)
}