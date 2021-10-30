package com.example.receiptApp.pages.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receiptApp.ActivityViewModel
import com.example.receiptApp.App
import com.example.receiptApp.MainActivity
import com.example.receiptApp.R
import com.example.receiptApp.databinding.AddFragmentBinding
import com.example.receiptApp.pages.add.adapters.AddAdapter
import com.example.receiptApp.pages.add.adapters.GalleryAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


class AddFragment : Fragment(R.layout.add_fragment)
{
    private val viewModel: AddViewModel by viewModels {
        AddViewModelFactory((activity?.application as App).galleryImagesPaginated)
    }

    private val activityViewModel: ActivityViewModel by activityViewModels()
    private lateinit var binding: AddFragmentBinding

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

        setAttachmentVisible(false)

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
                if (currentId == R.id.start)
                {
                    setAttachmentVisible(false)
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
        val addAdapter = AddAdapter(viewModel.textEditCallback) {
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

        activityViewModel.setBABOnMenuItemClickListener {
            setAttachmentVisible(true)
            binding.addMotionLayout.transitionToEnd()
            true
        }


        binding.scrim.setOnClickListener {
            binding.addMotionLayout.transitionToState(R.id.start)
        }


        // TODO !!
        //  here viewModel.rvList.value should be saved into the db
        activityViewModel.setFabOnClickListener {
            Toast.makeText(activity, "halooo dal fab", Toast.LENGTH_SHORT).show()
            Timber.d("\nlist -> \n${viewModel.rvList.value}")
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = addAdapter
        }

        binding.recyclerViewImgs.layoutManager = GridLayoutManager(activity, 3)

        val galleryAdapter = GalleryAdapter{

        }

        binding.recyclerViewImgs.adapter = galleryAdapter

        lifecycleScope.launch {
            viewModel.flow.collectLatest { pagingData ->
                galleryAdapter.submitData(pagingData)
            }
        }
    }

    fun setAttachmentVisible(visible: Boolean)
    {
        with(binding)
        {
            scrim.visibility = if (visible) View.VISIBLE else View.GONE
            recyclerViewImgs.visibility = if (visible)  View.VISIBLE else View.GONE
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