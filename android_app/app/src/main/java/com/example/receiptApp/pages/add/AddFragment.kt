package com.example.receiptApp.pages.add

import android.os.Bundle
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


// TODO ! the bottom app bar should be under the keyboard!!!!!


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

        // set the toolbar for this fragment
        NavigationUI.setupWithNavController(binding.topAppBar, findNavController())

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.topAppBar)

        binding.topAppBar.setNavigationOnClickListener {
            // TODO need to check the state before going up!
            findNavController().navigateUp()
        }

        val datePicker = MaterialDatePicker.Builder.datePicker()
            // TODO get the string out of here!
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        // the adapter take the two callBacks, one is implemented in the View model the other here
        val addAdapter = AddAdapter(viewModel.textEditCallback) {
            // TODO use a constant tag in the constants
            datePicker.show(childFragmentManager, "tag")
        }

        // if the user select a date and press ok, set it into the view model
        datePicker.addOnPositiveButtonClickListener {
            datePicker.selection?.let { it1 -> viewModel.setDate(it1) }
            // need to notify that this element changed otherwise it doesn't update the value
            addAdapter.notifyItemChanged(0)
        }

        // observe the list of elements and submit it to the adapter
        viewModel.rvList.observe(viewLifecycleOwner) {
            addAdapter.submitList(it)
        }

        // TODO !!
        activityViewModel.setBABOnMenuItemClickListener {
            Toast.makeText(activity, "halooo", Toast.LENGTH_SHORT).show()
            true
        }

        // TODO !!
        activityViewModel.setFabOnClickListener {
            Toast.makeText(activity, "halooo dal fab", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = addAdapter
        }

    }


}