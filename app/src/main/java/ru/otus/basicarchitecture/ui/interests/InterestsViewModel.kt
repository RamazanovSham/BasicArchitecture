package ru.otus.basicarchitecture.ui.interests

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.otus.basicarchitecture.WizardCache
import javax.inject.Inject

@HiltViewModel
class InterestsViewModel @Inject constructor(
    private val cache: WizardCache
) : ViewModel() {

    val interests = listOf("Running", "Books", "Clubbing", "Knitting", "Dota 2")

    fun savedInterests(selected: List<String>) {
        cache.interests = selected
    }
}

