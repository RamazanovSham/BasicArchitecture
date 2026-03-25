package ru.otus.basicarchitecture.ui.address

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.otus.basicarchitecture.WizardCache
import ru.otus.basicarchitecture.network.dadata.DadataApi
import ru.otus.basicarchitecture.network.dadata.DadataRequest
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val cache: WizardCache,
    private val dadataApi: DadataApi
) : ViewModel() {

    private val _addressSuggestions = MutableStateFlow<List<String>>(emptyList())
    val addressSuggestions: StateFlow<List<String>> = _addressSuggestions

    private var searchJob: Job? = null

    fun getAddressSuggestions(query: String) {
        searchJob?.cancel()

        val currentQuery = query.trim()
        if (currentQuery.length < 3) {
            _addressSuggestions.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            try {
                delay(500)

                val response = dadataApi.getAddressSuggestions(
                    DadataRequest(query = currentQuery, count = 10)
                )

                _addressSuggestions.value = response.suggestions.map { it.value }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("DaData", "Request error", e)
                _addressSuggestions.value = emptyList()
            }
        }
    }

    fun saveData(address: String) {
        cache.address = address
    }

    override fun onCleared() {
        searchJob?.cancel()
        super.onCleared()
    }
}