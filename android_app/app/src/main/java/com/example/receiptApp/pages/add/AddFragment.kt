package com.example.receiptApp.pages.add

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.FileProvider.getUriForFile
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
import com.example.receiptApp.Utils.PermissionsHandling
import com.example.receiptApp.databinding.AddFragmentBinding
import com.example.receiptApp.pages.add.adapters.AddAdapter
import com.example.receiptApp.pages.add.adapters.GalleryAdapter
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File


/**
 * TODO -> WARNING: rotating the phone with the attachment is visible bring the UI to an unstable state
 */




class AddFragment : Fragment(R.layout.add_fragment)
{
    private lateinit var permHandler: PermissionsHandling

    private val viewModel: AddViewModel by viewModels {
        AddViewModelFactory((activity?.application as App).attachmentRepository)
    }

    private lateinit var binding: AddFragmentBinding
    private lateinit var addAdapter: AddAdapter

    private val onPermissionGranted: () -> Unit = {
        setAttachmentVisible(false)
        viewModel.galleryCollect()
    }

    private var onPermissionDenied: () -> Unit = {
        (activity as MainActivity)
            .binding
            .bottomAppBar
            .replaceMenu(R.menu.bottom_bar_menu_hide)
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
            bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END

            bottomAppBar.navigationIcon = null

            fab.setImageResource(R.drawable.ic_baseline_check_24)
            fab.setOnClickListener {
                Toast.makeText(activity, "halooo dal fab", Toast.LENGTH_SHORT).show()
                Timber.d("\nlist -> \n${viewModel.rvList.value}")
            }
        }

        permHandler = PermissionsHandling(frag = this)

        permHandler.setCallbacksAndAsk(
            permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                )
            } else
            {
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            },
            granted = onPermissionGranted,
            denied = onPermissionDenied
        )

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
            if (!datePicker.isVisible)
            {
                datePicker.show(childFragmentManager, "tag")
            }
        }

        // if the user select a date and press ok, set it into the view model
        datePicker.addOnPositiveButtonClickListener {
            datePicker.selection?.let { it1 -> viewModel.setDate(it1) }

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.galleryState.collectLatest { state ->

                // TODO set the states of simplify this code
                when (state)
                {
                    is GalleryDataState.Idle ->
                    {
                    }
                    is GalleryDataState.Error ->
                    {
                        Toast.makeText(activity, "there was an error with the attachments", Toast.LENGTH_SHORT).show()
                        binding.addMotionLayout.transitionToState(R.id.start)
                    }
                    is GalleryDataState.Data -> galleryAdapter.submitData(state.tasks)
                    is GalleryDataState.Loading ->
                    {
                    }
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

            bottomAppBar.replaceMenu(if (visible) R.menu.bottom_bar_menu_attach else R.menu.bottom_bar_menu_add)

            val menuItemClickListener: (MenuItem) -> Boolean
            if (visible)
            {
                menuItemClickListener = {
                    when (it.itemId)
                    {
                        R.id.attach_camera ->
                        {
                            handleCamera()
                            true
                        }
                        R.id.attach_file ->
                        {
                            getFile.launch("application/pdf")
                            true
                        }
                        else -> false
                    }
                }

            } else
            {
                menuItemClickListener = {
                    binding.addMotionLayout.transitionToState(R.id.end)
                    true
                }
            }
            bottomAppBar.setOnMenuItemClickListener(menuItemClickListener)
        }
    }

    private fun handleCamera()
    {
        permHandler.setCallbacksAndAsk(
            permissions = arrayOf(Manifest.permission.CAMERA),
            granted = {
                context?.let { context ->

                    val imagesPath = File(context.filesDir, "images/")
                    val newFile = File(imagesPath, "default_image.jpg")
                    val contentUri: Uri = getUriForFile(
                        context,
                        "com.example.receiptApp",
                        newFile
                    )
                    Timber.d("contentUri -> $contentUri")
                    getCamera.launch(contentUri)
                }
            },
            denied = {
                Timber.d("permission denied")
            }
        )
    }


    private val getFile = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri ->
        Timber.d("$uri")

        binding.addMotionLayout.transitionToState(R.id.start)

        TODO("this action need to be done when the user click OK! not right now")
        viewModel.copyFile(uri)
    }

    private val getCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) {

        binding.addMotionLayout.transitionToState(R.id.start)


        if (it) Timber.d("got camera") else Timber.e("no camera")
    }


}