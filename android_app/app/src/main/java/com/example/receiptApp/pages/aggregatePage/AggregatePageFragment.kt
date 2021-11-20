package com.example.receiptApp.pages.aggregatePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receiptApp.App
import com.example.receiptApp.MainActivity
import com.example.receiptApp.R
import com.example.receiptApp.databinding.AggregatePageFragmentBinding

class AggregatePageFragment : Fragment(R.layout.aggregate_page_fragment) {

    // args passed via navigation call
    private val args: AggregatePageFragmentArgs by navArgs()

    private val viewModel: AggregatePageViewModel by viewModels(){
        AggregatePageViewModelFactory(
            (activity?.application as App).attachmentRepository,
            (activity?.application as App).archiveRepository,
            args.aggregateId
        )
    }

    private lateinit var binding: AggregatePageFragmentBinding
    private lateinit var aggregatePageAdapter: AggregatePageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AggregatePageFragmentBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with((activity as MainActivity).binding){
            // here modify bottom bar aspect

        }

        aggregatePageAdapter = AggregatePageAdapter(
            (activity?.application as App).attachmentRepository
        )

        // observe the graph list view
        viewModel.rvList.observe(viewLifecycleOwner){
            aggregatePageAdapter.submitList(it)
        }

        with(binding)
        {
            recyclerViewAggregatePage.adapter = aggregatePageAdapter
            recyclerViewAggregatePage.layoutManager = LinearLayoutManager(activity)
        }
    }

    /**
     * On back pressed
     * this method override the back pression for return on the last page
     */
    override fun onBackPressed(){
        findNavController().popBackStack()
    }
}