package com.example.viewmodelbindinggraph.hello

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.viewmodelbindinggraph.R
import com.example.viewmodelbindinggraph.databinding.HelloFragmentBinding
import kotlinx.android.synthetic.main.hello_fragment.*

class HelloFragment : Fragment(R.layout.hello_fragment)
{
    private val viewModel: HelloViewModel by viewModels()
    private lateinit var binding: HelloFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = HelloFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.button.setOnClickListener {
            viewModel.setName(editText.text.toString())
        }
    }



}