package com.example.receiptApp.pages.aggregatePage

import androidx.lifecycle.*
import com.example.receiptApp.pages.archive.ArchiveDataModel
import com.example.receiptApp.repository.ArchiveRepository
import com.example.receiptApp.repository.AttachmentRepository
import com.example.receiptApp.repository.DbRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AggregatePageViewModel(
    private val attachmentRepository: AttachmentRepository,
    private val archiveRepository: ArchiveRepository,
    private val aggregate_id : Long
) : ViewModel() {
    // the list observed by the recyclerview
    private val _rvList = MutableLiveData<List<ArchiveDataModel>>()
    val rvList: LiveData<List<ArchiveDataModel>>
        get() = _rvList

    init{

    }

    fun loadData()
    {
        viewModelScope.launch {
            _rvList.value = archiveRepository.getAggregatesWithElementsByIdInArchiveFormat(aggregate_id)
        }
    }

    @DelicateCoroutinesApi
    fun deleteAggregate() = GlobalScope.launch {
        archiveRepository.deleteAggregate(aggregate_id)
    }
}

class AggregatePageViewModelFactory(
    private val attachmentRepository: AttachmentRepository,
    private val archiveRepository: ArchiveRepository,
    private val aggregate_id: Long
) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(AggregatePageViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return AggregatePageViewModel(attachmentRepository, archiveRepository, aggregate_id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}