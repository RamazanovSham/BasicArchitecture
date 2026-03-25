package ru.otus.basicarchitecture.ui.personalinfo

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.otus.basicarchitecture.WizardCache
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val cache: WizardCache
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalInfoUiState())
    val uiState: StateFlow<PersonalInfoUiState> = _uiState

    init {
        validate()
    }

    fun onFirstNameChange(value: String) = update { copy(firstName = value) }

    fun onLastNameChange(value: String) = update { copy(lastName = value) }

    fun onBirthDateChange(value: String) = update { copy(birthDate = value) }

    private fun update(block: PersonalInfoUiState.() -> PersonalInfoUiState) {
        _uiState.value = _uiState.value.block()
        validate()
    }

    private fun validate() {
        val state = _uiState.value

        val error = when {
            state.firstName.isBlank() -> "Введите имя"
            state.lastName.isBlank() -> "Введите фамилию"
            state.birthDate.isBlank() -> "Введите дату рождения"
            state.birthDate.length < 10 -> null
            !DateValidator.isAdult(state.birthDate) -> "Возраст должен быть 18+"
            else -> null
        }

        val isValid = error == null && (state.birthDate.isBlank() || state.birthDate.length == 10)

        _uiState.value = state.copy(
            error = error,
            isValid = isValid
        )
    }

    fun saveAndProceed() {
        val state = _uiState.value
        cache.firstName = state.firstName
        cache.lastName = state.lastName
        cache.birthDate = state.birthDate
    }
}

data class PersonalInfoUiState(
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: String = "",
    val error: String? = null,
    val isValid: Boolean = false
)