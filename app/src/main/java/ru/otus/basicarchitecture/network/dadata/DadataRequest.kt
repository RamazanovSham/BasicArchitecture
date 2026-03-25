package ru.otus.basicarchitecture.network.dadata

data class DadataRequest(
    val query: String,
    val count: Int = 10
)

