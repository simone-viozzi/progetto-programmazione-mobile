package com.example.receiptApp.pages.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.receiptApp.App
import com.example.receiptApp.MainActivity
import com.example.receiptApp.R
import com.example.receiptApp.databinding.HomeFragmentBinding
import com.google.android.material.bottomappbar.BottomAppBar


class HomeFragment : Fragment()
{
    val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((activity?.application as App).sharedPrefRepository)
    }

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

        with((activity as MainActivity).binding)
        {
            bottomAppBar.setFabAlignmentModeAndReplaceMenu(
                BottomAppBar.FAB_ALIGNMENT_MODE_CENTER,
                R.menu.bottom_bar_menu_main
            )
            fab.show()
            fab.setImageResource(R.drawable.ic_baseline_add_24)
            bottomAppBar.setNavigationIcon(R.drawable.ic_baseline_menu_24)

            fab.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToAddFragment()
                findNavController().navigate(action)
            }
            bottomAppBar.setOnMenuItemClickListener {
                when (it.itemId)
                {
                    R.id.bottom_bar_menu_about ->
                    {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAboutFragment())
                        true
                    }

                    R.id.bottom_bar_menu_edit ->
                    {
                        // TODO !!
                        true
                    }
                    R.id.bottom_bar_menu_save ->
                    {
                        viewModel.save()
                        true
                    }
                    R.id.bottom_bar_menu_load ->
                    {
                        viewModel.load()
                        true
                    }
                    else -> false
                }
            }
        }

        val dashAdapter = DashboardAdapter(viewModel.onItemMove)
        val rvLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS


        val callback = DragManageAdapter(viewModel, dashAdapter)


        val helper = ItemTouchHelper(callback)

        helper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.apply {
            adapter = dashAdapter
            layoutManager = rvLayoutManager
        }

        viewModel.list.observe(viewLifecycleOwner) {
            dashAdapter.submitList(it)
        }

    }
}