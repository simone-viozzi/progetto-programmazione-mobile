package com.example.receiptApp.pages.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receiptApp.App
import com.example.receiptApp.MainActivity
import com.example.receiptApp.R
import com.example.receiptApp.databinding.ArchiveFragmentBinding
import com.example.receiptApp.pages.dashboard.DashboardDataModel
import com.example.receiptApp.pages.dashboard.adapters.DashboardAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import timber.log.Timber
import kotlin.math.abs


class ArchiveFragment : Fragment(R.layout.archive_fragment)
{

    private val viewModel: ArchiveViewModel by viewModels(){
        ArchiveViewModelFactory((activity?.application as App).archiveRepository)
    }

    private lateinit var binding: ArchiveFragmentBinding
    private lateinit var archiveAdapter: ArchiveAdapter

    private var appBarLayoutHeight = 0
    var start = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = ArchiveFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        // this is needed for binding the view model to the binding
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with((activity as MainActivity).binding)
        {
            bottomAppBar.replaceMenu(R.menu.bottom_app_bar_archive)

            bottomAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId)
                {
                    R.id.bottom_app_bar_search ->
                    {
                        lockToolbar(start, binding.appBarLayout, binding.collapsingToolbarLayout)
                        start = !start

                        true
                    }
                    else -> false
                }
            }
        }

        archiveAdapter = ArchiveAdapter(
            (activity?.application as App).attachmentRepository
        )

        // observe the graph list view
        viewModel.rvList.observe(viewLifecycleOwner){
            archiveAdapter.submitList(it)
        }

        with(binding)
        {
            recyclerView.adapter = archiveAdapter
            recyclerView.layoutManager = LinearLayoutManager(activity)
        }

        //testAdapter.submitList((0..20).map { DashboardDataModel.TestBig(id = it) })


        val lp = binding.collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
        lp.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL

        val appBarLayoutParams = binding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        appBarLayoutHeight = appBarLayoutParams.height
        appBarLayoutParams.behavior = null
        appBarLayoutParams.height = 0
        binding.appBarLayout.layoutParams = appBarLayoutParams
    }


    private fun lockToolbar(
        closed: Boolean,
        appbar: AppBarLayout,
        toolbar: CollapsingToolbarLayout,
    )
    {
        val onCollapsed: () -> Unit = {
            // Fully collapsed so set the flags to lock the toolbar
            val lp = toolbar.layoutParams as AppBarLayout.LayoutParams
            lp.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
        }

        val onExpanded: () -> Unit = {
            // Expanded
            val toolbarLayoutParams = toolbar.layoutParams as AppBarLayout.LayoutParams
            toolbarLayoutParams.scrollFlags = 0
            toolbar.layoutParams = toolbarLayoutParams

            val appBarLayoutParams = appbar.layoutParams as CoordinatorLayout.LayoutParams
            appBarLayoutParams.behavior = null
            appbar.layoutParams = appBarLayoutParams
        }

        appbar.addOnOffsetChangedListener(object : OnOffsetChangedListener
        {
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int)
            {
                when
                {
                    (abs(verticalOffset) == appBarLayout.totalScrollRange) && closed ->
                    {
                        // Collapsed
                        onCollapsed.invoke()

                        Timber.e("COLLAPSED")

                        appbar.removeOnOffsetChangedListener(this)
                    }
                    (verticalOffset == 0) && !closed ->
                    {

                        onExpanded.invoke()

                        Timber.e("EXPANDED")

                        appbar.removeOnOffsetChangedListener(this)
                    }
                    else ->
                    {
                        // Somewhere in between
                    }
                }
            }
        })

        // Unlock by restoring the flags and then expand

        Timber.e("starting ${if (closed) "CLOSING" else "OPENING"}")

        val toolbarLayoutParams = toolbar.layoutParams as AppBarLayout.LayoutParams
        toolbarLayoutParams.scrollFlags =
            AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
        toolbar.layoutParams = toolbarLayoutParams

        val appBarLayoutParams = appbar.layoutParams as CoordinatorLayout.LayoutParams
        appBarLayoutParams.height = appBarLayoutHeight
        appBarLayoutParams.behavior = AppBarLayout.Behavior()
        appbar.layoutParams = appBarLayoutParams


        appbar.setExpanded(!closed, true)

    }

}