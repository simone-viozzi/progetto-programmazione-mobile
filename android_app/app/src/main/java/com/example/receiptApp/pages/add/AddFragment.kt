package com.example.receiptApp.pages.add

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receiptApp.ActivityViewModel
import com.example.receiptApp.R
import com.example.receiptApp.databinding.AddFragmentBinding
import com.google.android.material.datepicker.MaterialDatePicker
import timber.log.Timber

class AddFragment : Fragment(R.layout.add_fragment)
{
    private val viewModel: AddViewModel by viewModels()
    private val activityViewModel: ActivityViewModel by activityViewModels()
    private lateinit var binding: AddFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = AddFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        // this is needed for binding the view model to the binding
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        /**
         * set the toolbar for this fragment
         */
        NavigationUI.setupWithNavController(binding.topAppBar, findNavController())

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.topAppBar)

        binding.topAppBar.setNavigationOnClickListener {
            // TODO need to check the state before going up!
            findNavController().navigateUp()
        }

        // TODO attach this to the onclick of the textField inside the header
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        val addAdapter = AddAdapter(viewModel.textEditCallback) {
            datePicker.show(childFragmentManager, "tag")
        }

        viewModel.rvList.observe(viewLifecycleOwner) {
            Timber.d("ao")
            addAdapter.submitList(it)
        }

        datePicker.addOnPositiveButtonClickListener {
            // need a separated variable because if you do one line the setter and getter doesn't get called
            datePicker.selection?.let { it1 -> viewModel.setDate(it1) }
            addAdapter.notifyItemChanged(0)
        }

        activityViewModel.setBABOnMenuItemClickListener {
            Toast.makeText(activity, "halooo", Toast.LENGTH_SHORT).show()
            true
        }

        activityViewModel.setFabOnClickListener {
            Toast.makeText(activity, "halooo dal fab", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = addAdapter
        }


    }


}