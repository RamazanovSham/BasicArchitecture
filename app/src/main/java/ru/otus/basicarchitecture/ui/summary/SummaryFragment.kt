package ru.otus.basicarchitecture.ui.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.otus.basicarchitecture.databinding.FragmentSummaryBinding

@AndroidEntryPoint
class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SummaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val cache = viewModel.getData()

        binding.tvResult.text = buildString {
            appendLine("Имя: ${cache.firstName}")
            appendLine("Фамилия: ${cache.lastName}")
            appendLine("Дата рождения: ${cache.birthDate}")
            appendLine("Адрес: ${cache.address}")
            append("Интересы: ${cache.interests.joinToString(", ")}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}