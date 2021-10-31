package com.example.receiptApp.pages.add

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receiptApp.App
import com.example.receiptApp.MainActivity
import com.example.receiptApp.R
import com.example.receiptApp.databinding.AddFragmentBinding
import com.example.receiptApp.pages.add.adapters.AddAdapter
import com.example.receiptApp.pages.add.adapters.GalleryAdapter
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


class AddFragment : Fragment(R.layout.add_fragment)
{
    private val viewModel: AddViewModel by viewModels {
        AddViewModelFactory((activity?.application as App).galleryImagesPaginated)
    }

    private lateinit var binding: AddFragmentBinding
    private lateinit var addAdapter: AddAdapter


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        //TODO("need testing!")
        var isGranted = it.values.reduce{ b1, b2 -> b1 && b2 }

//        for (g in it.values)
//        {
//            isGranted = isGranted && g
//        }

        if (isGranted)
        {
            Snackbar.make(
                (activity as MainActivity).binding.coordinatorLayout,
                "halooo dal fab",
                Snackbar.LENGTH_SHORT
            )
                .setAnchorView((activity as MainActivity).binding.fab)
                .show()
            (activity as MainActivity)
                .binding
                .bottomAppBar
                .replaceMenu(R.menu.bottom_bar_menu_add)

            viewModel.galleryCollect()
        } else
        {
            (activity as MainActivity)
                .binding
                .bottomAppBar
                .replaceMenu(R.menu.bottom_bar_menu_hide)
        }
    }

    private fun checkPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            if ((ContextCompat.checkSelfPermission(
                    (activity as MainActivity),
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                ) != PackageManager.PERMISSION_GRANTED)
                && ContextCompat.checkSelfPermission(
                    (activity as MainActivity),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            )
            {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_MEDIA_LOCATION
                    )
                )
            } else
            {
                Snackbar.make(
                    (activity as MainActivity).binding.coordinatorLayout,
                    "permissions ok",
                    Snackbar.LENGTH_SHORT
                ).setAnchorView((activity as MainActivity).binding.fab)
                    .show()

                viewModel.galleryCollect()
            }
        } else
        {
            if (ContextCompat.checkSelfPermission(
                    (activity as MainActivity),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            )
            {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = AddFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        with(binding)
        {
            // this is needed for binding the view model to the binding
            viewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        with((activity as MainActivity).binding)
        {
            bottomAppBar.setFabAlignmentModeAndReplaceMenu(
                BottomAppBar.FAB_ALIGNMENT_MODE_END,
                R.menu.bottom_bar_menu_add
            )
            bottomAppBar.navigationIcon = null
            fab.setImageResource(R.drawable.ic_baseline_check_24)
            fab.setOnClickListener {
                Toast.makeText(activity, "halooo dal fab", Toast.LENGTH_SHORT).show()
                Timber.d("\nlist -> \n${viewModel.rvList.value}")
            }
            bottomAppBar.setOnMenuItemClickListener {
                binding.addMotionLayout.transitionToState(R.id.end)
                true
            }
        }


        checkPermissions()


        binding.addMotionLayout.setTransitionListener(object : MotionLayout.TransitionListener
        {

            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int)
            {
            }

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float)
            {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int)
            {
                when (currentId)
                {
                    R.id.start -> setAttachmentVisible(false)
                    R.id.end -> setAttachmentVisible(true)
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            )
            {
            }

        })

        // set the toolbar for this fragment
        NavigationUI.setupWithNavController(binding.topAppBar, findNavController())
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.topAppBar)


        binding.topAppBar.setNavigationOnClickListener {
            // TODO need to check the state before going up!
            findNavController().navigateUp()
        }


        val datePicker = MaterialDatePicker.Builder.datePicker()
            // TODO get the string out of here!
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()


        // the adapter take the two callBacks, one is implemented in the View model the other here
        addAdapter = AddAdapter(viewModel.textEditCallback) {
            // TODO use a constant tag in the constants
            // TODO if the user call this more than one  time the app crash, need to test if datePicker is visible
            datePicker.show(childFragmentManager, "tag")
        }


        // if the user select a date and press ok, set it into the view model
        datePicker.addOnPositiveButtonClickListener {
            datePicker.selection?.let { it1 -> viewModel.setDate(it1) }

            // TODO, there is a way to avoid this? --> prof!
            // need to notify that this element changed otherwise it doesn't update the value
            addAdapter.notifyItemChanged(0)
        }


        // observe the list of elements and submit it to the adapter
        viewModel.rvList.observe(viewLifecycleOwner) {
            addAdapter.submitList(it)
        }


        binding.scrim.setOnClickListener {
            binding.addMotionLayout.transitionToState(R.id.start)
        }


        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = addAdapter
        }

        binding.recyclerViewImgs.layoutManager = GridLayoutManager(activity, 3)

        val galleryAdapter = GalleryAdapter {
            viewModel.setAttachment(it)
            addAdapter.notifyItemChanged(0)

            binding.addMotionLayout.transitionToState(R.id.start)
        }

        binding.recyclerViewImgs.adapter = galleryAdapter

        //        val galleryCollect = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        //            withContext(Dispatchers.IO) {
        //                viewModel.flow.collectLatest { pagingData ->
        //                    galleryAdapter.submitData(pagingData)
        //                }
        //            }
        //        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.galleryState.collectLatest { state ->
                // New value received
                when (state)
                {
                    is GalleryDataState.Idle -> {}
                    is GalleryDataState.Error -> {}
                    is GalleryDataState.Data -> galleryAdapter.submitData(state.tasks)
                }
            }
        }



        setAttachmentVisible(false)
    }


    fun setAttachmentVisible(visible: Boolean)
    {
        with(binding)
        {
            scrim.visibility = if (visible) View.VISIBLE else View.GONE
            recyclerViewImgs.visibility = if (visible) View.VISIBLE else View.GONE
            scrim.isClickable = visible
            recyclerViewImgs.isClickable = visible

            recyclerView.isClickable = !visible
        }

        with((activity as MainActivity).binding)
        {
            if (visible) fab.hide() else fab.show()
        }
    }
}