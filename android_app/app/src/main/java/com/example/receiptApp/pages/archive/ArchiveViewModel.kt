package com.example.receiptApp.pages.archive

import androidx.lifecycle.*
import com.example.receiptApp.repository.ArchiveRepository
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

        Timber.e("ArchiveViewModel INIT")
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