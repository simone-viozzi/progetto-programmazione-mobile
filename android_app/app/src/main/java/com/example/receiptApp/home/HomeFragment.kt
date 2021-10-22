package com.example.receiptApp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.receiptApp.R
import com.example.receiptApp.databinding.GraphsFragmentBinding
import com.example.receiptApp.databinding.HomeFragmentBinding
import com.example.receiptApp.graphs.GraphsViewModel

class HomeFragment : Fragment()
{

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        // this is needed for binding the view model to the binding
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

}