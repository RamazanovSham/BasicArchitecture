package ru.otus.basicarchitecture.network.dadata

import retrofit2.http.Body
import retrofit2.http.POST

interface DadataApi {
    @POST("suggestions/api/4_1/rs/suggest/address")
    suspend fun getAddressSuggestions(@Body request: DadataRequest): DadataResponse
}

