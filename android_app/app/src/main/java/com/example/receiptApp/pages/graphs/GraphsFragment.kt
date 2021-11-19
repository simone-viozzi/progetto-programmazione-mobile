package com.example.receiptApp.pages.graphs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receiptApp.App
import com.example.receiptApp.MainActivity
import com.example.receiptApp.R
import com.example.receiptApp.databinding.GraphsFragmentBinding
import com.example.receiptApp.pages.add.AddViewModel
import com.example.receiptApp.repository.AttachmentRepository
import com.example.receiptApp.repository.GraphsRepository
import com.google.android.material.bottomappbar.BottomAppBar
import timber.log.Timber

class GraphsFragment : Fragment(R.layout.graphs_fragment)
{

    private val viewModel: GraphsViewModel by viewModels{
        GraphsViewModelFactory((activity?.application as App).graphsRepository)
    }

    private lateinit var binding: GraphsFragmentBinding
    private lateinit var graphsAdapter: GraphAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = GraphsFragmentBinding.inflate(inflater, container, false)
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
            fab.hide() // hide the fab not useful inside graphs
            // add here modification to the main activity
            bottomAppBar.replaceMenu(R.menu.bottom_bar_menu_hide)
        }

        // generate a new Graph adapter
        graphsAdapter = GraphAdapter()

        // observe the graph list view
        viewModel.rvList.observe(viewLifecycleOwner){
            graphsAdapter.submitList(it)
        }

        // apply the adapter and the layout manager to the layout recycler view list
        binding.recyclerViewGraph.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = graphsAdapter
        }
    }
}

