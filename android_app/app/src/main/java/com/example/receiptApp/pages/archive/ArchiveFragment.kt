package com.example.receiptApp.pages.archive


import android.annotation.SuppressLint
import android.app.SearchManager
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receiptApp.App
import com.example.receiptApp.MainActivity
import com.example.receiptApp.R
import com.example.receiptApp.databinding.ArchiveFragmentBinding
import com.example.receiptApp.hideKeyboard
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber
import kotlin.math.abs


class ArchiveFragment : Fragment(R.layout.archive_fragment)
{

    private val viewModel: ArchiveViewModel by viewModels(){
        ArchiveViewModelFactory((activity?.application as App).archiveRepository)
    }

    private lateinit var binding: ArchiveFragmentBinding
    private lateinit var archiveAdapter: ArchiveAdapter

    private lateinit var tagList: List<String>

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
            (activity?.application as App).attachmentRepository,
            findNavController()
        )

        // observe tagList
        viewModel.tagList.observe(viewLifecycleOwner){
            tagList = it
        }

        // observe the graph list view
        viewModel.rvList.observe(viewLifecycleOwner){
            archiveAdapter.submitList(it)
        }

        var suggestions: List<String> = listOf("")
        viewModel.tagList.observe(viewLifecycleOwner){
            suggestions = it
        }

        with(binding)
        {
            recyclerView.adapter = archiveAdapter
            recyclerView.layoutManager = LinearLayoutManager(activity)

            reloadButton.setOnClickListener {
                viewModel?.setTag(null)
                viewModel?.reloadAggregatesList()
            }

            // set the number of characters before showing the suggestions
            searchView.findViewById<AutoCompleteTextView>(R.id.search_src_text).threshold = 1
            searchView.queryHint = "Search a tag"

            val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
            val to = intArrayOf(R.id.item_label)
            val cursorAdapter = SimpleCursorAdapter(context, R.layout.suggestion_layout, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)

            searchView.suggestionsAdapter = cursorAdapter

            searchView.setOnQueryTextListener(
                object: androidx.appcompat.widget.SearchView.OnQueryTextListener {

                    override fun onQueryTextChange(newText: String?): Boolean {

                        val cursor = MatrixCursor(
                            arrayOf(
                                BaseColumns._ID,
                                SearchManager.SUGGEST_COLUMN_TEXT_1
                            )
                        )
                        newText?.let {
                            suggestions?.forEachIndexed { index, suggestion ->
                                if (suggestion.contains(newText, true))
                                    cursor.addRow(arrayOf(index, suggestion))
                            }
                        }

                        cursorAdapter.changeCursor(cursor)

                        return false
                    }

                    override fun onQueryTextSubmit(query: String?): Boolean {

                        if (query != null) {

                            // if the passed query is contained inside tag list
                            if (tagList.indexOf(query) > -1) {
                                viewModel?.setTag(query)
                            }else{
                                viewModel?.setTag(null)
                            }
                            viewModel?.reloadAggregatesList()
                        }

                        hideKeyboard()
                        return false
                    }
                }
            )

            searchView.setOnSuggestionListener(
                object: androidx.appcompat.widget.SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean {
                    return false
                }

                @SuppressLint("Range")
                override fun onSuggestionClick(position: Int): Boolean {
                    hideKeyboard()
                    val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
                    val selection = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)) // segna errore ma è farlocco
                    searchView.setQuery(selection, false)

                    // after selection applay same beahviour of submit
                    if (selection != null) {
                        // if the passed query is contained inside tag list
                        if (tagList.indexOf(selection) > -1) {
                            viewModel?.setTag(selection)
                            viewModel?.reloadAggregatesList()
                        }
                    }

                    // Do something with selection
                    return true
                }
            })

        }
        val lp = binding.collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
        lp.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL

        val appBarLayoutParams = binding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        appBarLayoutHeight = appBarLayoutParams.height
        appBarLayoutParams.behavior = null
        appBarLayoutParams.height = 0
        binding.appBarLayout.layoutParams = appBarLayoutParams

        ///////////////////////////////////////////////////////
        // override of back button function
        (activity as MainActivity).onBackPressedCallback = {
            if(!start){
                // chiudo l'app
                confirmExit()
            }else{
                // chiudo l'appBar
                lockToolbar(start, binding.appBarLayout, binding.collapsingToolbarLayout)
                start = !start
            }
        }
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

}
