package ru.otus.basicarchitecture.ui.summary

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.otus.basicarchitecture.WizardCache
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    val cache: WizardCache
) : ViewModel() {
    fun getData() = cache
}

