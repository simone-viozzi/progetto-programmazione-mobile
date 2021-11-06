package com.example.receiptApp.pages.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.receiptApp.App
import com.example.receiptApp.MainActivity
import com.example.receiptApp.R
import com.example.receiptApp.databinding.HomeFragmentBinding
import com.example.receiptApp.pages.home.adapters.DashboardAdapter
import com.google.android.material.bottomappbar.BottomAppBar


class HomeFragment : Fragment()
{
    val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((activity?.application as App).sharedPrefRepository)
    }

    private lateinit var binding: HomeFragmentBinding

    var transitionEndCallback: (() -> Unit)? = null

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

        binding.homeMotionLayout.setTransitionListener(motionLayoutListener)

        val dashAdapter = DashboardAdapter()

        val rvLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

        val callback = DragManageAdapter(viewModel, dashAdapter)

        val helper = ItemTouchHelper(callback)

        binding.recyclerView.apply {
            adapter = dashAdapter
            layoutManager = rvLayoutManager
        }

        viewModel.list.observe(viewLifecycleOwner) {
            dashAdapter.submitList(it)
        }


        val dashStoreAdapter = DashboardAdapter()

        val rvStoreLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

        binding.recyclerViewStore.apply {
            adapter = dashStoreAdapter
            layoutManager = rvStoreLayoutManager
        }

        viewModel.homeState.observe(viewLifecycleOwner) { state ->

            when (state)
            {
                is HomeViewModel.HomeState.NormalMode ->
                {
                    helper.attachToRecyclerView(null)

                    with((activity as MainActivity).binding)
                    {
                        fab.show()

                        bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                        bottomAppBar.replaceMenu(R.menu.bottom_bar_menu_main)

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
                                    viewModel.setEditMode()
                                    true
                                }
                                else -> false
                            }
                        }
                    }

                    with(binding)
                    {
                        scrim.visibility = View.GONE
                        scrim.isClickable = false
                        recyclerViewStore.isClickable = false

                        recyclerView.isClickable = true

                        scrim.setOnClickListener(null)
                    }

                    viewModel.store.removeObservers(viewLifecycleOwner)

                    dashAdapter.onItemMove = null
                    dashAdapter.onLongClickListener = viewModel.setEditMode
                }
                is HomeViewModel.HomeState.EditMode ->
                {
                    helper.attachToRecyclerView(binding.recyclerView)

                    with((activity as MainActivity).binding)
                    {
                        fab.show()

                        bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                        bottomAppBar.replaceMenu(R.menu.bottom_bar_munu_home_add_widget)

                        fab.setImageResource(R.drawable.ic_baseline_check_24)

                        bottomAppBar.navigationIcon = null

                        fab.setOnClickListener { viewModel.saveDashboard() }

                        bottomAppBar.setOnMenuItemClickListener {
                            when (it.itemId)
                            {
                                R.id.bottom_bar_menu_add ->
                                {
                                    viewModel.setStoreMode.invoke()
                                    true
                                }
                                else -> false
                            }
                        }
                    }

                    with(binding)
                    {
                        scrim.visibility = View.GONE
                        scrim.isClickable = false
                        recyclerViewStore.isClickable = false

                        recyclerView.isClickable = true

                        scrim.setOnClickListener(null)
                    }

                    dashAdapter.onItemMove = viewModel.onItemMove
                    dashAdapter.onLongClickListener = null


                }
                is HomeViewModel.HomeState.StoreMode ->
                {
                    with((activity as MainActivity).binding)
                    {
                        fab.hide()

                        bottomAppBar.replaceMenu(R.menu.bottom_bar_menu_hide)
                    }

                    with(binding)
                    {
                        scrim.visibility = View.VISIBLE
                        scrim.isClickable = true
                        recyclerViewStore.isClickable = true

                        recyclerView.isClickable = false

                        scrim.setOnClickListener {
                            binding.homeMotionLayout.transitionToState(R.id.start)

                            transitionEndCallback = {
                                viewModel!!.setEditMode.invoke()
                            }
                        }
                    }

                    viewModel.store.observe(viewLifecycleOwner) {
                        dashStoreAdapter.submitList(it)
                    }

                    binding.homeMotionLayout.transitionToState(R.id.end)
                }
                HomeViewModel.HomeState.NullState ->
                {

                }
            }
        }

    }

    private val motionLayoutListener = object: MotionLayout.TransitionListener
    {
        override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int)
        {

        }

        override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float)
        {

        }

        override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int)
        {
            transitionEndCallback?.invoke()
            transitionEndCallback = null
        }

        override fun onTransitionTrigger(
            motionLayout: MotionLayout?,
            triggerId: Int,
            positive: Boolean,
            progress: Float
        )
        {

        }

    }

}


