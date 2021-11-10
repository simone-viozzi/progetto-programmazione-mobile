package com.example.receiptApp.pages.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.receiptApp.MainActivity
import com.example.receiptApp.R
import com.example.receiptApp.databinding.ArchiveFragmentBinding

class ArchiveFragment : Fragment(R.layout.archive_fragment)
{

    private val viewModel: ArchiveViewModel by viewModels()
    private lateinit var binding: ArchiveFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = ArchiveFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        // this is needed for binding the view model to the binding
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with((activity as MainActivity).binding)
        {
            bottomAppBar.replaceMenu(R.menu.bottom_app_bar_archive)

            val a: SearchResultProvider
        }
    }

}