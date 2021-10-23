package com.example.receiptApp.pages.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.receiptApp.ActivityViewModel
import com.example.receiptApp.R
import com.example.receiptApp.databinding.HomeFragmentBinding

class HomeFragment : Fragment()
{

    private val viewModel: HomeViewModel by viewModels()
    private val activityViewModel: ActivityViewModel by activityViewModels()

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


        activityViewModel.setFabOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddFragment()
            findNavController().navigate(action)
        }

        activityViewModel.setBABOnMenuItemClickListener {
            when (it.itemId)
            {
                R.id.bottom_bar_menu_about -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAboutFragment())
                    true
                }

                R.id.bottom_bar_menu_edit -> {
                    // TODO !!
                    true
                }
                else -> false
            }
        }
    }

}