package com.example.receiptApp.archive

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.receiptApp.R

class ArchiveFragment : Fragment()
{

    companion object
    {
        fun newInstance() = ArchiveFragment()
    }

    private lateinit var viewModel: ArchiveViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        return inflater.inflate(R.layout.archive_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ArchiveViewModel::class.java)
        // TODO: Use the ViewModel
    }

}