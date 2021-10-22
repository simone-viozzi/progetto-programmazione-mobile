package com.example.receiptApp.graphs

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.receiptApp.R

class GraphsFragment : Fragment()
{

    companion object
    {
        fun newInstance() = GraphsFragment()
    }

    private lateinit var viewModel: GraphsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        return inflater.inflate(R.layout.graphs_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GraphsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}