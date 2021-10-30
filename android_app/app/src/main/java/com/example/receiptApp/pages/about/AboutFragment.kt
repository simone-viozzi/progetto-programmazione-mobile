package com.example.receiptApp.pages.about

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.receiptApp.MainActivity
import com.example.receiptApp.R
import com.example.receiptApp.databinding.AboutFragmentBinding


class AboutFragment : Fragment()
{
    private lateinit var binding: AboutFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = AboutFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        /**
         * set the toolbar for this fragment
         */
        NavigationUI.setupWithNavController(binding.topAppBar, findNavController())

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.topAppBar)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        with((activity as MainActivity).binding)
        {
            fab.hide()
            bottomAppBar.replaceMenu(R.menu.bottom_bar_menu_hide)
            bottomAppBar.navigationIcon = null
        }
    }
}