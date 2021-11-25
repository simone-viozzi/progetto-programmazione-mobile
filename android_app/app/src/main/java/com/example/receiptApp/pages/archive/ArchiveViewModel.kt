package com.example.receiptApp.pages.archive

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.receiptApp.databinding.AddHeadBinding
import com.example.receiptApp.databinding.AddSingleElementBinding
import com.example.receiptApp.pages.add.AddDataModel
import com.example.receiptApp.pages.add.adapters.AddAdapter
import com.example.receiptApp.pages.graphs.GraphsViewModel
import com.example.receiptApp.repository.ArchiveRepository
import com.example.receiptApp.repository.GraphsRepository
import com.example.receiptApp.toEditable
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class ArchiveViewModel(private val archiveRepository: ArchiveRepository) : ViewModel()
{
    // the list observed by the recyclerview
    private val _rvList = MutableLiveData<List<ArchiveDataModel>>()
    val rvList: LiveData<List<ArchiveDataModel>>
        get() = _rvList

    // list of all aggregates tag inside the database
    private val _tagList = MutableLiveData<List<String>>()
    val tagList: LiveData<List<String>>
        get() = _tagList

    // tag filter
    private var selectedTag: String? = null

    // start filter date
    var startDate: Date

    // end filter date
    var endDate: Date

    init {
        // set date filter parameters
        val cal = Calendar.getInstance()
        endDate = cal.time // put as end date now
        cal.add(Calendar.YEAR, -1)
        startDate = cal.time // put as start date of the filter one year ago

        loadingTags()

        reloadAggregatesList()
    }

    /**
     * Set tag
     * method that change the tag filter
     * @param tag
     */
    fun setTag(tag: String?){
        selectedTag = tag
    }

    fun loadingTags(){
        viewModelScope.launch {
            // loading tags list
            _tagList.value = archiveRepository.getAggregatesTagsList()
        }
    }

    fun reloadAggregatesList(){
        viewModelScope.launch {

            // generate aggregate list
            _rvList.value = archiveRepository.getAggregates(
                start = startDate,
                end = endDate,
                tag_name =  selectedTag
            )
        }
    }
}

class ArchiveViewModelFactory(private val repository: ArchiveRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(ArchiveViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return ArchiveViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}