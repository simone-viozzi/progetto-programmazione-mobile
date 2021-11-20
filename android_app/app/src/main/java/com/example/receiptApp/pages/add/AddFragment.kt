package com.example.receiptApp.pages.add

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
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
import com.example.receiptApp.DATE_PICKER_TAG
import com.example.receiptApp.MainActivity
import com.example.receiptApp.R
import com.example.receiptApp.databinding.AddFragmentBinding
import com.example.receiptApp.pages.add.adapters.AddAdapter
import com.example.receiptApp.pages.add.adapters.GalleryAdapter
import com.example.receiptApp.repository.AttachmentRepository
import com.example.receiptApp.utils.PermissionsHandling
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        AddViewModelFactory(
            (activity?.application as App).attachmentRepository,
                (activity?.application as App).dbRepository
        )
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
        // disable the system autofill
        disableAutofill()

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
                Timber.d("\nlist -> \n${viewModel.rvList.value}")

                viewModel.selfCheckAggregate = AddAdapter.SelfCheckCallbacks.selfCheckAggregate
                viewModel.selfCheckElements = AddAdapter.SelfCheckCallbacks.selfCheckElements

                if (viewModel.selfIntegrityCheck())
                {
                    viewModel.saveToDb()

                    Timber.e("going up")
                    findNavController().navigateUp()
                } else
                {
                    Toast.makeText(activity, "those fields are required!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        permHandler = PermissionsHandling(frag = this)

        // ask the permission for the attachment
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

        // in this fragment the animation will perform first and than the graphics change, this is a bit laggy
        binding.addMotionLayout.setTransitionListener(object : MotionLayout.TransitionListener
        {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int)
            {
                when (currentId)
                {
                    // the start state is the one with the attachment closed
                    R.id.start -> setAttachmentVisible(false)
                    // the end state is with the attachment open
                    R.id.end -> setAttachmentVisible(true)
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {}
        })

        // set the toolbar for this fragment
        NavigationUI.setupWithNavController(binding.topAppBar, findNavController())
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.topAppBar)

        // handling of the up button in the appbar
        binding.topAppBar.setNavigationOnClickListener {
            MaterialAlertDialogBuilder(activity as MainActivity)
                .setMessage("are you sure?")
                .setNegativeButton("decline") { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton("accept") { dialog, which ->
                    // Respond to positive button press
                    findNavController().navigateUp()
                }
                .show()
        }


        val dateValidatorMax: DateValidator = DateValidatorPointBackward.before(MaterialDatePicker.todayInUtcMilliseconds())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            // TODO get the string out of here!
            .setTitleText("Select date")
            // select today as default date
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(CalendarConstraints.Builder().setValidator(dateValidatorMax).build())
            .build()


        // the adapter take the four callBacks, 3 are in the View model and the other here
        addAdapter = AddAdapter(
            viewModel.textEditCallback,
            viewModel.autoCompleteAggregateCallback,
            viewModel.autoCompleteElementCallback
        ) {
            if (!datePicker.isVisible)
            {
                datePicker.show(childFragmentManager, DATE_PICKER_TAG)
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

        // pressing on the scrim close the attachment recyclerView with the animation
        binding.scrim.setOnClickListener {
            binding.addMotionLayout.transitionToState(R.id.start)
        }

        binding.recyclerViewAdd.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = addAdapter
        }

        val galleryAdapter = GalleryAdapter {
            viewModel.setAttachment(it)

            addAdapter.notifyItemChanged(0)
            binding.addMotionLayout.transitionToState(R.id.start)
        }


        binding.recyclerViewImgs.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = galleryAdapter
        }

        // the galleryAdapter need the submit data to be in a coroutine, we are not actually using the states,
        // but they are implemented.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.galleryState.collectLatest { state ->

                // TODO set the states of simplify this code
                when (state)
                {
                    is GalleryDataState.Error ->
                    {
                        Toast.makeText(activity, "there was an error with the attachments", Toast.LENGTH_SHORT).show()
                        binding.addMotionLayout.transitionToState(R.id.start)
                    }
                    is GalleryDataState.Data -> galleryAdapter.submitData(state.tasks)
                }
            }
        }

        setAttachmentVisible(false)
    }

    /**
     * modify the graphics to display / hide the attachment section
     *
     * @param visible
     */
    fun setAttachmentVisible(visible: Boolean)
    {
        with(binding)
        {
            scrim.visibility = if (visible) View.VISIBLE else View.GONE
            recyclerViewImgs.visibility = if (visible) View.VISIBLE else View.GONE
            scrim.isClickable = visible
            recyclerViewImgs.isClickable = visible

            recyclerViewAdd.isClickable = !visible
        }

        with((activity as MainActivity).binding)
        {
            if (visible) fab.hide() else fab.show()

            bottomAppBar.replaceMenu(if (visible) R.menu.bottom_bar_menu_attach else R.menu.bottom_bar_menu_add)

            val menuItemClickListener: (MenuItem) -> Boolean
            if (visible)
            {
                // if the attachment is visible the menu will be bottom_bar_menu_attach, so we need to handle the
                // camera and file attachments
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
                // if !visible, the menu need to open the attachment section
                menuItemClickListener = {
                    binding.addMotionLayout.transitionToState(R.id.end)
                    true
                }
            }
            bottomAppBar.setOnMenuItemClickListener(menuItemClickListener)
        }
    }

    var cameraUri: Uri? = null

    private fun handleCamera()
    {
        permHandler.setCallbacksAndAsk(
            permissions = arrayOf(Manifest.permission.CAMERA),
            granted = {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "take_picture")
                values.put(MediaStore.Images.Media.DESCRIPTION, "take_picture_description")
                val uri = requireContext().contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                cameraUri = uri
                getCamera.launch(uri)
            },
            denied = {
                Timber.d("permission denied")
            }
        )
    }

    // TODO this is debugged halfway
    private val getFile = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        Timber.d("$uri")


        binding.addMotionLayout.transitionToState(R.id.start)
        uri?.let {
            viewModel.setAttachment(
                AttachmentRepository.Attachment(
                    uri = it,
                    type = AttachmentRepository.TYPE.PDF,
                    needToCopy = true
                )
            )
        } ?: Toast.makeText(activity, "there was an error with the camera", Toast.LENGTH_SHORT).show()
        addAdapter.notifyItemChanged(0)
    }


    private val getCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { result_ok ->

        binding.addMotionLayout.transitionToState(R.id.start)
        if (result_ok) Timber.d("got camera") else Timber.e("no camera")

        cameraUri?.let {
            viewModel.setAttachment(
                AttachmentRepository.Attachment(
                    uri = it,
                    type = AttachmentRepository.TYPE.IMAGE,
                    needToCopy = true
                )
            )

        } ?: Toast.makeText(activity, "there was an error with the camera", Toast.LENGTH_SHORT).show()
        addAdapter.notifyItemChanged(0)
    }


    /**
     * Disable system autofill for this fragment, if we didn't put this the system will try to autofill passwords
     * in our app
     */
    @TargetApi(Build.VERSION_CODES.O)
    private fun disableAutofill()
    {
        activity?.window?.decorView?.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
    }

}