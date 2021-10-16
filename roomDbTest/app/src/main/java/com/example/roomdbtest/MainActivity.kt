package com.example.roomdbtest

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdbtest.databinding.ActivityMainBinding
import timber.log.Timber
import android.util.DisplayMetrics

import android.graphics.PointF
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView.SmoothScroller


class MainActivity : AppCompatActivity()
{
    private val viewModel: ColorViewModel by viewModels {
        ColorViewModelFactory((application as App).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        binding.lifecycleOwner = this

        setSupportActionBar(binding.materialToolbar)

        val recyclerView = binding.recyclerView
        val adapter = ColorListAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = SmoothLinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        adapter.onItemClick = { color ->
            viewModel.delete(color)
        }

        viewModel.allColors.observe(this) {
            adapter.submitList(it.asReversed())
        }

        binding.fab.setOnClickListener {
            val color = MyColor.createRandomColor()
            viewModel.addColor(color)
            adapter.notifyItemChanged(0)
            recyclerView.smoothScrollToPosition(0)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when(item.itemId)
        {
            R.id.app_bar_delete_all -> {
                viewModel.deleteAll()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    class SmoothLinearLayoutManager(context: Context) : LinearLayoutManager(context){
        override fun smoothScrollToPosition(
            recyclerView: RecyclerView?,
            state: RecyclerView.State?,
            position: Int
        )
        {
            val linearSmoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(
                recyclerView!!.context
            )
            {
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float
                {
                    return 0.001f / displayMetrics.densityDpi
                }
            }

            linearSmoothScroller.targetPosition = position
            startSmoothScroll(linearSmoothScroller)
        }
    }


}