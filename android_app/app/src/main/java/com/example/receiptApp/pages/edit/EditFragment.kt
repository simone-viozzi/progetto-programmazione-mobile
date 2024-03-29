package com.example.receiptApp.pages.edit

import android.Manifest
import android.annotation.TargetApi
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receiptApp.App
import com.example.receiptApp.DATE_PICKER_TAG
import com.example.receiptApp.MainActivity
import com.example.receiptApp.R
import com.example.receiptApp.databinding.EditFragmentBinding
import com.example.receiptApp.pages.edit.adapters.EditAdapter
import com.example.receiptApp.pages.edit.adapters.GalleryAdapter
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


class EditFragment : Fragment(R.layout.edit_fragment)
{
    private lateinit var permHandler: PermissionsHandling

    // args passed via navigation call
    private val args: EditFragmentArgs by navArgs()


    private val viewModel: EditViewModel by viewModels {
        EditViewModelFactory(
            (activity?.application as App).attachmentRepository,
            (activity?.application as App).dbRepository,
            args.aggregateId
        )
    }

    private lateinit var binding: EditFragmentBinding
    private lateinit var editAdapter: EditAdapter

    // temp var to store the uri of the photo the user is taking
    var cameraUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = EditFragmentBinding.inflate(inflater, container, false)
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

        // the app bar need to be transformed to the needs of this fragment
        with((activity as MainActivity).binding)
        {
            bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END

            // from here the user must not open the navigation drawer (this is not a top level destination)
            bottomAppBar.navigationIcon = null

            fab.setImageResource(R.drawable.ic_baseline_check_24)

            // the fab is used as save button, before saving it will be performed a self check
            fab.setOnClickListener {
                viewModel.selfCheckAggregate = EditAdapter.SelfCheckCallbacks.selfCheckAggregate
                viewModel.selfCheckElements = EditAdapter.SelfCheckCallbacks.selfCheckElements

                // the test is passed i can start the save and return to the previous page
                if (viewModel.selfIntegrityCheck())
                {
                    // this is done asynchronously using a global scope, with the view model scope the coroutine will
                    //  get killed before completing
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
            // depending on the sdk i need different permissions
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
            // if the user grant the permissions i can start loading the images
            granted = {
                setAttachmentVisible(false)
                viewModel.galleryCollect()
            },
            // otherwise just hide the button
            denied = {
                (activity as MainActivity)
                    .binding
                    .bottomAppBar
                    .replaceMenu(R.menu.bottom_bar_menu_hide)
            }
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
            confirmExit()
        }


        val dateValidatorMax: DateValidator = DateValidatorPointBackward.before(MaterialDatePicker.todayInUtcMilliseconds())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_date))
            // select today as default date
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(CalendarConstraints.Builder().setValidator(dateValidatorMax).build())
            .build()


        // the adapter take the four callBacks, 3 are in the View model and the other one here
        editAdapter = EditAdapter(
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
            editAdapter.notifyItemChanged(0)
        }

        // observe the list of elements and submit it to the adapter
        viewModel.rvList.observe(viewLifecycleOwner) {
            editAdapter.submitList(it)
        }

        // pressing on the scrim close the attachment recyclerView with the animation
        binding.scrim.setOnClickListener {
            binding.addMotionLayout.transitionToState(R.id.start)
        }

        binding.recyclerViewAdd.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = editAdapter
        }

        val galleryAdapter = GalleryAdapter {
            viewModel.setAttachment(it)

            editAdapter.notifyItemChanged(0)
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

                when (state)
                {
                    is GalleryDataState.Error ->
                    {
                        Toast.makeText(activity, "there was an error with the attachments", Toast.LENGTH_SHORT).show()
                        binding.addMotionLayout.transitionToState(R.id.start)
                    }
                    is GalleryDataState.Data -> galleryAdapter.submitData(state.tasks)
                    GalleryDataState.Idle -> { }
                    GalleryDataState.Loading -> { }
                }
            }
        }

        setAttachmentVisible(false)
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
                findNavController().navigateUp()
            }
            .show()
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
                            // start the standard file picker filtering only pdfs
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

            (activity as MainActivity).onBackPressedCallback = {
                if (visible)
                {
                    binding.addMotionLayout.transitionToState(R.id.start)
                }
                else
                {
                    confirmExit()
                }
            }

        }
    }


    // helper function to open the camera
    private fun handleCamera()
    {
        // need to always check for permissions
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
                Toast.makeText(activity, getString(R.string.no_camera_permission), Toast.LENGTH_SHORT).show()
            }
        )
    }


    private val getFile = registerForActivityResult(
        ActivityResultContracts.GetContent()
    )
    { uri: Uri? ->
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
        } ?: Toast.makeText(activity, getString(R.string.file_error), Toast.LENGTH_SHORT).show()
        editAdapter.notifyItemChanged(0)
    }


    private val getCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { result_ok ->

        binding.addMotionLayout.transitionToState(R.id.start)

        cameraUri?.let {
            viewModel.setAttachment(
                AttachmentRepository.Attachment(
                    uri = it,
                    type = AttachmentRepository.TYPE.IMAGE,
                    needToCopy = true
                )
            )

        } ?: Toast.makeText(activity, getString(R.string.camera_error), Toast.LENGTH_SHORT).show()
        editAdapter.notifyItemChanged(0)
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