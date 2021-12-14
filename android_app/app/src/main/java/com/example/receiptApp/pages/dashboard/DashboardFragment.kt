package com.example.receiptApp.pages.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import timber.log.Timber


class DashboardFragment : Fragment()
{
    val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(
            (activity?.application as App).dbRepository,
            (activity?.application as App).dashboardRepository
        )
    }

    private lateinit var binding: DashboardFragmentBinding

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



        with((activity as MainActivity))
        {
            onBottomSheetOpen = {
                binding.fab.hide()
            }
            onBottomSheetClose = {
                binding.fab.show()
            }
        }

        ///// dashboard setup section /////
        val dashAdapter = DashboardAdapter()
        val rvLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvLayoutManager.gapStrategy =
            StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

        // to setup drag and drop
        val callback = DragManager(viewModel)
        val helper = ItemTouchHelper(callback)

        binding.recyclerViewDashboard.apply {
            adapter = dashAdapter
            layoutManager = rvLayoutManager
        }
        ////////////////////////////////////

        ///// store setup section /////
        val dashStoreAdapter = DashboardAdapter()

        val rvStoreLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

        binding.recyclerViewStore.apply {
            adapter = dashStoreAdapter
            layoutManager = rvStoreLayoutManager
        }
        ///////////////////////////////


        // the state of the dashboard are:
        //  -> NoState      -> initial state
        //  -> EmptyDashMode-> when the dashboard do not have any widget
        //  -> NormalMode   -> dashboard is displaying widget
        //  -> EditMode     -> the user want to edit the dashboard
        //  -> StoreMode    -> the store is open
        viewModel.dashboardState.observe(viewLifecycleOwner) { state ->
            when (state)
            {
                DashboardViewModel.DashboardState.NoState -> {
                    // force the start state of motion layout
                    binding.homeMotionLayout.setState(R.id.baseConstraint, -1, -1)
                }
                DashboardViewModel.DashboardState.EmptyDashMode ->
                {
                    Timber.e("EMPTY STATE")

                    // drag and drop are disabled in this state
                    helper.attachToRecyclerView(null)

                    with((activity as MainActivity).binding)
                    {
                        // the settings of the appbar are common for EmptyDashMode and NormalMode
                        setAppbarToNormalMode()
                    }

                    with(binding)
                    {
                        // when the user tap into the screen it will switch to store mode
                        welcomeScreen.setOnClickListener {
                            viewModel?.setStoreMode()
                        }

                        scrim.setOnClickListener(null)
                    }
                    // the store does not have to be filled in this state
                    viewModel.store.removeObservers(viewLifecycleOwner)

                    dashAdapter.onLongClickListener = null
                    dashAdapter.onClickListener = null

                    dashStoreAdapter.onLongClickListener = null
                    dashStoreAdapter.onClickListener = null

                    (activity as MainActivity).onBackPressedCallback = {
                        confirmExit()
                    }

                    binding.homeMotionLayout.transitionToState(R.id.welcomeScreenConstraint)
                }
                is DashboardViewModel.DashboardState.NormalMode ->
                {
                        Timber.e("NORMAL STATE")

                    // drag and drop are disabled in this state
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

                    viewModel.dashboard.observe(viewLifecycleOwner) {
                        dashAdapter.submitList(it)
                    }

                    // when the user will do a long click on an element of the dashboard it will switch to edit mode
                    dashAdapter.onLongClickListener = {
                        Timber.e("viewModel.setEditMode()")
                        viewModel.setEditMode()
                    }
                    dashAdapter.onClickListener = null

                    dashStoreAdapter.onLongClickListener = null
                    dashStoreAdapter.onClickListener = null

                    (activity as MainActivity).onBackPressedCallback = {
                        confirmExit()
                    }

                    binding.homeMotionLayout.transitionToState(R.id.normalStateConstrains)

                }
                is DashboardViewModel.DashboardState.EditMode ->
                {
                    Timber.e("EDIT STATE")
                    // drag and drop are enabled in this state
                    helper.attachToRecyclerView(binding.recyclerViewDashboard)

                    // the app bar need to be transformed to match the needs of this state
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
                                R.id.bottom_bar_menu_store ->
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

                    viewModel.dashboard.observe(viewLifecycleOwner) {
                        dashAdapter.submitList(it)
                    }

                    dashAdapter.onLongClickListener = null
                    dashAdapter.onClickListener = null

                    dashStoreAdapter.onLongClickListener = null
                    dashStoreAdapter.onClickListener = null

                    (activity as MainActivity).onBackPressedCallback = {
                        MaterialAlertDialogBuilder(activity as MainActivity)
                            .setMessage(getString(R.string.want_to_save))
                            .setNegativeButton(getString(R.string.no)) { _, _ ->
                                // Respond to negative button press
                                viewModel.reloadDashboard()
                            }
                            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                                // Respond to positive button press
                                viewModel.saveDashboard()
                            }
                            .show()
                    }

                    // always rescroll to position 0, when adding elements from the store this create a nice animation
                    binding.recyclerViewDashboard.smoothScrollToPosition(0)
                    binding.homeMotionLayout.transitionToState(R.id.editModeConstrains)
                }
                is DashboardViewModel.DashboardState.StoreMode ->
                {
                    Timber.e("STORE STATE")

                    // disable drag and drop
                    helper.attachToRecyclerView(null)

                    // this state is reachable only from edit or welcome, so i don't need to edit all the
                    // aspect of the appbar
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
                        Timber.d("submitting list -> $it")
                        dashStoreAdapter.submitList(it)
                    }

                    dashAdapter.onLongClickListener = null
                    dashAdapter.onClickListener = null

                    dashStoreAdapter.onLongClickListener = null

                    // when the user click an element, it will be added to the current dashboard
                    dashStoreAdapter.onClickListener = {
                        viewModel.addToDashboard(it)
                    }

                    (activity as MainActivity).onBackPressedCallback = {
                        viewModel.setEditMode()
                    }

                    // depending of the previous state i need to animate a transition to different contains set
                    when(viewModel.getPreviousState())
                    {
                        is DashboardViewModel.DashboardState.EmptyDashMode -> {
                            // there are a constrain set that have the welcome page under the store
                            binding.homeMotionLayout.transitionToState(R.id.storeConstraintSetWelcome)
                        }
                        is DashboardViewModel.DashboardState.EditMode -> {
                            // or the normal state
                            binding.homeMotionLayout.transitionToState(R.id.storeConstraintSetEdit)
                        }
                        else -> throw IllegalStateException("from what state you come from?? how?")
                    }
                    // without this line the graphs refuse to load
                    dashStoreAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun confirmExit()
    {
        MaterialAlertDialogBuilder(activity as MainActivity)
            .setMessage(getString(R.string.sure_to_exit))
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                // Respond to negative button press
            }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                // Respond to positive button press
                requireActivity().finish()
            }
            .show()
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
                    // TODO debug only
                    viewModel.clearDashboard()
                    viewModel.clearDb()
                    true
                }
                R.id.bottom_bar_menu_generate_data -> {
                    // TODO debug only
                    viewLifecycleOwner.lifecycleScope.launch {
                        (activity?.application as App).dbRepository.RandomFillDatabase()
                    }
                    true
                }
                else -> false
            }
        }
    }

}




