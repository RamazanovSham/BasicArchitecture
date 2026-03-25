package ru.otus.basicarchitecture.network.dadata

data class DadataResponse(
    val suggestions: List<Suggestion>
)

data class Suggestion(
    val value: String,
    val unrestricted_value: String
)

