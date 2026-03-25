package ru.otus.basicarchitecture.ui.address

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.otus.basicarchitecture.WizardCache
import ru.otus.basicarchitecture.network.dadata.*

@OptIn(ExperimentalCoroutinesApi::class)
class AddressViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var cache: WizardCache
    private lateinit var api: DadataApi
    private lateinit var viewModel: AddressViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        cache = mockk(relaxed = true)
        api = mockk()

        viewModel = AddressViewModel(cache, api)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `returns suggestions from api`() = runTest {
        val response = DadataResponse(
            listOf(
                Suggestion("Москва 1", "Москва 1"),
                Suggestion("Москва 2", "Москва 2")
            )
        )

        coEvery { api.getAddressSuggestions(any()) } returns response

        viewModel.getAddressSuggestions("Москва")

        dispatcher.scheduler.advanceUntilIdle()

        val result = viewModel.addressSuggestions.value

        assertEquals(2, result.size)
        assertEquals("Москва 1", result[0])
    }

    @Test
    fun `empty query clears suggestions`() = runTest {
        viewModel.getAddressSuggestions("ab")

        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.addressSuggestions.value.isEmpty())

        coVerify(exactly = 0) {
            api.getAddressSuggestions(any())
        }
    }

    @Test
    fun `api error returns empty list`() = runTest {
        coEvery { api.getAddressSuggestions(any()) } throws RuntimeException()

        viewModel.getAddressSuggestions("Москва")

        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.addressSuggestions.value.isEmpty())
    }

    @Test
    fun `saveData writes address to cache`() {
        viewModel.saveData("Москва")

        assertEquals("Москва", cache.address)
    }

    @Test
    fun `new query cancels previous request`() = runTest {
        coEvery { api.getAddressSuggestions(any()) } returns DadataResponse(emptyList())

        viewModel.getAddressSuggestions("моск")
        viewModel.getAddressSuggestions("москва")

        dispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            api.getAddressSuggestions(DadataRequest("москва", 10))
        }
    }
}