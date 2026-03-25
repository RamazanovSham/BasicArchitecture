package ru.otus.basicarchitecture.ui.address

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.otus.basicarchitecture.R
import ru.otus.basicarchitecture.databinding.FragmentAddressBinding

@AndroidEntryPoint
class AddressFragment : Fragment() {

    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddressViewModel by viewModels()

    private lateinit var suggestionsAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAdapter()
        setupObservers()
        setupListeners()
    }

    private fun setupAdapter() {
        suggestionsAdapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        ) {
            override fun getFilter(): Filter {
                return object : Filter() {
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        return FilterResults().apply {
                            values = (0 until count).mapNotNull { getItem(it) }
                            count = this@AddressFragment.suggestionsAdapter.count
                        }
                    }

                    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                        if ((results?.count ?: 0) > 0) notifyDataSetChanged()
                        else notifyDataSetInvalidated()
                    }
                }
            }
        }

        binding.etAddress.apply {
            setAdapter(suggestionsAdapter)
            threshold = 0
            onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
                post { dismissDropDown() }
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addressSuggestions.collect { suggestions ->
                suggestionsAdapter.clear()
                suggestionsAdapter.addAll(suggestions)
                suggestionsAdapter.notifyDataSetChanged()

                binding.etAddress.post {
                    val shouldShow =
                        binding.etAddress.hasFocus() &&
                                binding.etAddress.text.isNotEmpty() &&
                                suggestionsAdapter.count > 0

                    if (shouldShow) binding.etAddress.showDropDown()
                    else binding.etAddress.dismissDropDown()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.etAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.getAddressSuggestions(s?.toString().orEmpty())
            }

            override fun afterTextChanged(s: Editable?) {
                if (suggestionsAdapter.count > 0 && binding.etAddress.hasFocus()) {
                    binding.etAddress.post { binding.etAddress.showDropDown() }
                }
            }
        })

        binding.etAddress.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && suggestionsAdapter.count > 0 && binding.etAddress.text.isNotEmpty()) {
                binding.etAddress.post { binding.etAddress.showDropDown() }
            }
        }

        binding.btnNext.setOnClickListener {
            val address = binding.etAddress.text.toString()
            viewModel.saveData(address)
            findNavController().navigate(R.id.action_addressFragment_to_interestsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}