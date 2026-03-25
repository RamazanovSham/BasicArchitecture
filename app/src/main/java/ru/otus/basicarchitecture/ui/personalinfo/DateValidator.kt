package ru.otus.basicarchitecture.ui.personalinfo

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

object DateValidator {

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun isAdult(birthDate: String): Boolean =
        runCatching {
            val date = LocalDate.parse(birthDate, formatter)
            Period.between(date, LocalDate.now()).years >= 18
        }.getOrDefault(false)
}