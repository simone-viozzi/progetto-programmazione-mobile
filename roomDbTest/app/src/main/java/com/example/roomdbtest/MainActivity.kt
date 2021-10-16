package com.example.roomdbtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdbtest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity()
{
    private val viewModel: ColorViewModel by viewModels {
        ColorViewModelFactory((application as App).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        setSupportActionBar(binding.materialToolbar)

        val recyclerView = binding.recyclerView
        val adapter = ColorListAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.allColors.observe(this) {
            adapter.submitList(it)
        }

        binding.fab.setOnClickListener {
            viewModel.addColor(MyColor.createRandomColor())
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }
}