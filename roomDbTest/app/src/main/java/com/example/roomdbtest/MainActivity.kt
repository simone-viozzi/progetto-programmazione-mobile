package com.example.roomdbtest

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.roomdbtest.databinding.ActivityMainBinding
import com.example.roomdbtest.db.MyColor


class MainActivity : AppCompatActivity()
{
    // create the view model with the view model factory and passing the repository to it
    private val viewModel: ColorViewModel by viewModels {
        // the declaration of the repository is on the application (App)
        ColorViewModelFactory((application as App).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        // this is needed for the view model to work
        binding.lifecycleOwner = this

        // set the toolbar as action bar
        setSupportActionBar(binding.materialToolbar)

        val recyclerView = binding.recyclerView

        // create and set the adapter
        val adapter = ColorListAdapter()
        recyclerView.adapter = adapter

        // this sets the layoutManager, the possible choice are: LinearLayoutManager, GridLayoutManager, StaggeredGridLayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // if the recyclerView does not change in size, this add optimizations
        recyclerView.setHasFixedSize(true)


        /**
         * this is the click listener of every element of the recyclerView
         */
        adapter.onItemClick = { color ->
            // if the element corresponding to the element color get clicked, delete it.
            viewModel.delete(color)
        }


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            /**
             * if i'm scrolling up i display the fab, if i'm scrolling down i hide it
             *
             * @param recyclerView -> parent recyclerView
             * @param dx -> derivate of movement with respect to the horizontal axis
             * @param dy -> vertical derivate of movement
             */
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                if (dy > 0)
                {
                    binding.fab.hide()
                }
                if (dy < 0)
                {
                    binding.fab.show()
                }
            }
        })

        // observe the live data from the viewModel
        viewModel.allColors.observe(this) {
            // set it as the list of the adapter (reversed so new item are on top)
            adapter.submitList(it.asReversed())
        }


        binding.fab.setOnClickListener {
            // create a random color using the utility from Color
            val color = MyColor.createRandomColor()
            // adding it to the database
            viewModel.addColor(color)
            // notify the adapter that the element 0 is changing
            adapter.notifyItemChanged(0)
            // add autoscroll to reveal the element just added
            recyclerView.smoothScrollToPosition(0)
        }

    }

    /**
     * to add the three dot menu in the appBar
     *
     * @param menu
     * @return
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    /**
     * when a menu item get called, this function handle it
     *
     * @param item
     * @return
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            R.id.app_bar_delete_all ->
            {
                viewModel.deleteAll()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }


}