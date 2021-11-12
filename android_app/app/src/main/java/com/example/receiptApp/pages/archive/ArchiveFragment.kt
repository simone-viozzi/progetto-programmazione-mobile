package com.example.receiptApp.pages.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager


class ArchiveFragment : Fragment(R.layout.archive_fragment)
{

    private val viewModel: ArchiveViewModel by viewModels()
    private lateinit var binding: ArchiveFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = ArchiveFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    var start = false

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

        val testAdapter = DashboardAdapter()

        with(binding)
        {
            recyclerView.adapter = testAdapter
            recyclerView.layoutManager = LinearLayoutManager(activity)
        }

        testAdapter.submitList((0..20).map { DashboardDataModel.TestBig(id = it) })
    }


    private fun lockToolbar(closed: Boolean, appbar: AppBarLayout, toolbar: CollapsingToolbarLayout)
    {

        appbar.addOnOffsetChangedListener(object : OnOffsetChangedListener
        {
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int)
            {
                when
                {
                    (abs(verticalOffset) == appBarLayout.totalScrollRange) && closed ->
                    {
                        // Collapsed

                        // Fully collapsed so set the flags to lock the toolbar
                        val lp = toolbar.layoutParams as AppBarLayout.LayoutParams
                        lp.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED

                        //val appBarLayoutParams = appbar.layoutParams as CoordinatorLayout.LayoutParams
                        //appBarLayoutParams.behavior = null
                        //appbar.layoutParams = appBarLayoutParams

                        Timber.e("COLLAPSED")

                        appbar.removeOnOffsetChangedListener(this)
                    }
                    (verticalOffset == 0) && !closed ->
                    {
                        // Expanded
                        val toolbarLayoutParams = toolbar.layoutParams as AppBarLayout.LayoutParams
                        toolbarLayoutParams.scrollFlags = 0
                        toolbar.layoutParams = toolbarLayoutParams

                        val appBarLayoutParams = appbar.layoutParams as CoordinatorLayout.LayoutParams
                        appBarLayoutParams.behavior = null
                        appbar.layoutParams = appBarLayoutParams

                        Timber.e("EXPANDED")

                        appbar.removeOnOffsetChangedListener(this)
                    }
                    else ->
                    {
                        // Somewhere in between
                    }
                }


//                if (toolbar.height + verticalOffset < 2 * ViewCompat.getMinimumHeight(toolbar))
//                {
//                    // Now fully expanded again so remove the listener
//                    appbar.removeOnOffsetChangedListener(this)
//                } else
//                {
//                    // Fully collapsed so set the flags to lock the toolbar
//                    val lp = toolbar.layoutParams as AppBarLayout.LayoutParams
//                    lp.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
//                }
            }
        })

        // Unlock by restoring the flags and then expand

        Timber.e("starting ${if (closed) "CLOSING" else "OPENING"}")

        val toolbarLayoutParams = toolbar.layoutParams as AppBarLayout.LayoutParams
        toolbarLayoutParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
        toolbar.layoutParams = toolbarLayoutParams

        val appBarLayoutParams = appbar.layoutParams as CoordinatorLayout.LayoutParams
        appBarLayoutParams.behavior = AppBarLayout.Behavior()
        appbar.layoutParams = appBarLayoutParams

        appbar.setExpanded(!closed, true)

    }

}