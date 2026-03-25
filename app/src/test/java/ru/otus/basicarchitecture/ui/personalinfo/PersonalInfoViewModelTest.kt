package ru.otus.basicarchitecture.ui.personalinfo

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import ru.otus.basicarchitecture.WizardCache
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PersonalInfoViewModelTest {

    private lateinit var cache: WizardCache
    private lateinit var viewModel: PersonalInfoViewModel

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    @Before
    fun setup() {
        cache = mockk(relaxed = true)
        viewModel = PersonalInfoViewModel(cache)
    }

    private fun birthDate(yearsAgo: Long): String =
        LocalDate.now().minusYears(yearsAgo).format(formatter)

    @Test
    fun `initial state invalid`() {
        val state = viewModel.uiState.value

        assertFalse(state.isValid)
        assertEquals("Введите имя", state.error)
    }

    @Test
    fun `valid data makes state valid`() {
        viewModel.onFirstNameChange("Иван")
        viewModel.onLastNameChange("Иванов")
        viewModel.onBirthDateChange(birthDate(20))

        val state = viewModel.uiState.value

        assertTrue(state.isValid)
        assertNull(state.error)
    }

    @Test
    fun `age under 18 returns error`() {
        viewModel.onFirstNameChange("Иван")
        viewModel.onLastNameChange("Иванов")
        viewModel.onBirthDateChange(birthDate(10))

        val state = viewModel.uiState.value

        assertEquals("Возраст должен быть 18+", state.error)
        assertFalse(state.isValid)
    }

    @Test
    fun `partial date does not show error`() {
        viewModel.onFirstNameChange("Иван")
        viewModel.onLastNameChange("Иванов")
        viewModel.onBirthDateChange("01.01.20")

        val state = viewModel.uiState.value

        assertNull(state.error)
        assertFalse(state.isValid)
    }

    @Test
    fun `first name update updates state`() {
        viewModel.onFirstNameChange("Петр")

        val state = viewModel.uiState.value

        assertEquals("Петр", state.firstName)
        assertEquals("Введите фамилию", state.error)
    }

    @Test
    fun `saveAndProceed saves data to cache`() {
        viewModel.onFirstNameChange("Иван")
        viewModel.onLastNameChange("Иванов")
        viewModel.onBirthDateChange(birthDate(20))

        viewModel.saveAndProceed()

        every { cache.firstName } returns "Иван"
        every { cache.lastName } returns "Иванов"
    }
}