package ru.otus.basicarchitecture.ui.personalinfo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.otus.basicarchitecture.R
import ru.otus.basicarchitecture.databinding.FragmentPersonalInfoBinding

@AndroidEntryPoint
class PersonalInfoFragment : Fragment() {

    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PersonalInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupTextWatchers()
        setupNextButton()
        observeState()
    }

    private fun setupTextWatchers() {
        binding.etFirstName.addTextChangedListener(textWatcher(viewModel::onFirstNameChange))
        binding.etLastName.addTextChangedListener(textWatcher(viewModel::onLastNameChange))
        binding.etBirthDate.addTextChangedListener(dateTextWatcher())
    }

    private fun setupNextButton() {
        binding.btnNext.setOnClickListener {
            val state = viewModel.uiState.value
            if (state.isValid) {
                viewModel.saveAndProceed()
                findNavController().navigate(R.id.action_personalInfoFragment_to_addressFragment)
            } else {
                state.error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            var previousError: String? = null

            viewModel.uiState.collectLatest { state ->
                binding.btnNext.isEnabled = state.isValid

                if (state.birthDate.length == 10 &&
                    state.error != null &&
                    state.error != previousError
                ) {
                    Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
                }

                previousError = state.error
            }
        }
    }

    private fun textWatcher(onChange: (String) -> Unit) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            onChange(s.toString())
        }
    }

    private fun dateTextWatcher() = object : TextWatcher {
        private var isUpdating = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isUpdating) return

            val input = s.toString().replace(".", "")
            if (input.length > 8) return

            val formatted = buildString {
                input.forEachIndexed { i, c ->
                    append(c)
                    if (i == 1 || i == 3) append(".")
                }
            }

            isUpdating = true
            binding.etBirthDate.setText(formatted)
            binding.etBirthDate.setSelection(formatted.length)
            isUpdating = false
        }

        override fun afterTextChanged(s: Editable?) {
            viewModel.onBirthDateChange(s.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}