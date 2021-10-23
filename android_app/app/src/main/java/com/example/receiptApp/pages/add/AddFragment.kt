package com.example.receiptApp.pages.add

import android.icu.text.DateFormat.getDateInstance
import android.os.Bundle
import android.text.Editable
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.receiptApp.ActivityViewModel
import com.example.receiptApp.App
import com.example.receiptApp.R
import com.example.receiptApp.databinding.AddFragmentBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

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

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        binding.dateOverlay.setOnClickListener {
            datePicker.show(childFragmentManager, "tag");
        }

        // TODO datePicker.selection to get the selection -> in millis
        datePicker.addOnPositiveButtonClickListener {
            binding.dateText.text = Editable.Factory().newEditable(
                datePicker.selection.let {
                    if (it == null)
                    {
                        ""
                    }
                    else
                    {
                        DateFormat.format("dd/MM/yyyy", it)
                    }
                }
            )
        }
        datePicker.addOnNegativeButtonClickListener {
            // Respond to negative button click.
        }
        datePicker.addOnCancelListener {
            // Respond to cancel button click.
        }
        datePicker.addOnDismissListener {
            // Respond to dismiss events.
        }

        activityViewModel.setBABOnMenuItemClickListener {
            Toast.makeText(activity, "halooo", Toast.LENGTH_SHORT).show()
            true
        }

    }


}