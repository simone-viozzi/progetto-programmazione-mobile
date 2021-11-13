package com.example.receiptApp.pages.dashboard

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
import com.example.receiptApp.databinding.ActivityMainBinding
import com.example.receiptApp.databinding.DashboardFragmentBinding
import com.example.receiptApp.pages.dashboard.adapters.DashboardAdapter
import com.google.android.material.bottomappbar.BottomAppBar
import timber.log.Timber


class DashboardFragment : Fragment()
{
    val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            (activity?.application as App).sharedPrefRepository,
            (activity?.application as App).dbRepository
        )
    }

    private lateinit var binding: DashboardFragmentBinding

    var transitionEndCallback: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = DashboardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        Timber.e("onViewCreated -> DashboardFragment")

        // this is needed for binding the view model to the binding
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.homeMotionLayout.setDebugMode(1)
        binding.homeMotionLayout.setTransitionListener(motionLayoutListener)

        binding.homeMotionLayout.setState(R.id.baseConstraint, -1, -1)

        val dashAdapter = DashboardAdapter()

        val rvLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

        val callback = DragManageAdapter(viewModel)

        val helper = ItemTouchHelper(callback)

        binding.recyclerViewDashboard.apply {
            adapter = dashAdapter
            layoutManager = rvLayoutManager
        }

        viewModel.dashboard.observe(viewLifecycleOwner) {
            Timber.d("list -> $it")
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
                HomeViewModel.HomeState.EmptyDashMode ->
                {
                    Timber.e("EMPTY STATE")
                    helper.attachToRecyclerView(null)

                    with((activity as MainActivity).binding)
                    {
                        setAppbarToNormalMode()
                    }

                    with(binding)
                    {
                        welcomeScreen.setOnClickListener {
                            viewModel?.setStoreMode()
                        }

                        scrim.setOnClickListener(null)
                    }

                    viewModel.store.removeObservers(viewLifecycleOwner)

                    dashAdapter.onLongClickListener = null
                    dashAdapter.onClickListener = null

                    dashStoreAdapter.onLongClickListener = null
                    dashStoreAdapter.onClickListener = null

                    binding.homeMotionLayout.transitionToState(R.id.welcomeScreenConstraint)
                }
                is HomeViewModel.HomeState.NormalMode ->
                {
                    Timber.e("NORMAL STATE")
                    helper.attachToRecyclerView(null)

                    with((activity as MainActivity).binding)
                    {
                        setAppbarToNormalMode()
                    }

                    with(binding)
                    {
                        welcomeScreen.setOnClickListener(null)

                        scrim.setOnClickListener(null)
                    }

                    viewModel.store.removeObservers(viewLifecycleOwner)

                    dashAdapter.onLongClickListener = {
                        Timber.e("viewModel.setEditMode()")
                        viewModel.setEditMode()
                    }
                    dashAdapter.onClickListener = null

                    dashStoreAdapter.onLongClickListener = null
                    dashStoreAdapter.onClickListener = null

                    binding.homeMotionLayout.transitionToState(R.id.normalStateConstrains)

                }
                is HomeViewModel.HomeState.EditMode ->
                {
                    Timber.e("EDIT STATE")
                    helper.attachToRecyclerView(binding.recyclerViewDashboard)

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
                                    viewModel.setStoreMode()
                                    true
                                }
                                else -> false
                            }
                        }
                    }

                    with(binding)
                    {
                        welcomeScreen.setOnClickListener(null)

                        scrim.setOnClickListener(null)
                    }

                    dashAdapter.onLongClickListener = null
                    dashAdapter.onClickListener = null

                    dashStoreAdapter.onLongClickListener = null
                    dashStoreAdapter.onClickListener = null

                    binding.homeMotionLayout.transitionToState(R.id.editModeConstrains)
                }
                is HomeViewModel.HomeState.StoreMode ->
                {
                    helper.attachToRecyclerView(null)
                    Timber.e("STORE STATE")
                    with((activity as MainActivity).binding)
                    {
                        fab.hide()
                        bottomAppBar.replaceMenu(R.menu.bottom_bar_menu_hide)
                        bottomAppBar.navigationIcon = null
                    }

                    with(binding)
                    {
                        welcomeScreen.setOnClickListener(null)

                        scrim.setOnClickListener {
                            viewModel?.goBackToPreviousState()
                        }
                    }

                    viewModel.store.observe(viewLifecycleOwner) {
                        dashStoreAdapter.submitList(it)
                    }

                    dashAdapter.onLongClickListener = null
                    dashAdapter.onClickListener = null

                    dashStoreAdapter.onLongClickListener = null
                    dashStoreAdapter.onClickListener = {
                        viewModel.addToDashboard(it)

                        transitionEndCallback = {
                            binding.recyclerViewDashboard.smoothScrollToPosition(0)
                        }
                    }

                    if (viewModel.getPreviousState() is HomeViewModel.HomeState.EmptyDashMode)
                    {
                        binding.homeMotionLayout.transitionToState(R.id.storeConstraintSetWelcome)
                    }
                    else if (viewModel.getPreviousState() is HomeViewModel.HomeState.EditMode)
                    {
                        binding.homeMotionLayout.transitionToState(R.id.storeConstraintSetEdit)
                    }
                }
            }
        }

    }

    private fun ActivityMainBinding.setAppbarToNormalMode()
    {
        fab.show()

        bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
        bottomAppBar.replaceMenu(R.menu.bottom_bar_menu_main)

        fab.setImageResource(R.drawable.ic_baseline_add_24)

        bottomAppBar.setNavigationIcon(R.drawable.ic_baseline_menu_24)


        fab.setOnClickListener {
            val action = DashboardFragmentDirections.actionHomeFragmentToAddFragment()
            findNavController().navigate(action)
        }

        bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId)
            {
                R.id.bottom_bar_menu_about ->
                {
                    findNavController().navigate(DashboardFragmentDirections.actionHomeFragmentToAboutFragment())
                    true
                }
                R.id.bottom_bar_menu_edit ->
                {
                    viewModel.setEditMode()
                    true
                }
                R.id.bottom_bar_menu_clear_dash ->
                {
                    viewModel.clearDashboard()
                    viewModel.clearDb()
                    true
                }
                else -> false
            }
        }
    }


    private val motionLayoutListener = object: MotionLayout.TransitionListener
    {
        override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int)
        {
            Timber.e("changing transition -> ${motionLayout.toString()}")
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


