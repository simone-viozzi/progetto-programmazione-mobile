package com.example.receiptApp.pages.add

import android.net.Uri
import android.text.format.DateFormat
import android.widget.Toast
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.receiptApp.repository.AttachmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddViewModel(private val attachmentRepository: AttachmentRepository) : ViewModel()
{
    // the list observed by the recyclerview
    private val _rvList = MutableLiveData<List<AddDataModel>>()
    val rvList: LiveData<List<AddDataModel>>
        get() = _rvList

    private val _galleryState = MutableStateFlow<GalleryDataState>(GalleryDataState.Idle)
    val galleryState: StateFlow<GalleryDataState> = _galleryState

    private var attachment: AttachmentRepository.Attachment? = null

    // the callback used by every element of the views in the textWatcher to update the corresponding element in the
    // view model, it pass an element with only the updated field and the id != null
    var textEditCallback: ((el: AddDataModel) -> (Unit)) = { el ->

        // need to separate the two types
        when (el)
        {
            is AddDataModel.Aggregate ->
            {
                val oldEl = _rvList.value?.get(0) as AddDataModel.Aggregate

                el.str_date?.let { oldEl.str_date = it }
                el.tag?.let { oldEl.tag = it }

                // TODO update the list only if there was a change!
                _rvList.value = _rvList.value?.toMutableList().also { it?.set(0, oldEl) }
            }

            is AddDataModel.Element ->
            {
                val oldEl = _rvList.value?.get(el.vId) as AddDataModel.Element

                el.name?.let { oldEl.name = it }
                el.elem_tag?.let { oldEl.elem_tag = it }
                el.num?.let { oldEl.num = it }
                el.cost?.let { oldEl.cost = it }

                val newList = _rvList.value?.toMutableList().also { it?.set(el.vId, oldEl) }

                // TODO update the list only if there was a change!
                _rvList.value = newList?.also {
                    if (el.vId == getLastId(false))
                    {
                        it.add(AddDataModel.Element(vId = getLastId()))
                    }
                }
            }
        }
    }

    init
    {
        _rvList.value = listOf(
            AddDataModel.Aggregate(vId = 0),
            AddDataModel.Element(vId = getLastId())
        )
    }

    // this get called when the date picker return
    fun setDate(millis: Long)
    {
        val date = DateFormat.format("dd/MM/yyyy", millis).toString()
        _rvList.value = _rvList.value?.toMutableList().also { li ->
            val header = li?.get(0) as AddDataModel.Aggregate
            li[0] = header.also { it.str_date = date }
        }
    }

    private var lastId = 0

    private fun getLastId(autoincrement: Boolean = true): Int
    {
        return if (autoincrement) ++lastId else lastId
    }

    fun setAttachment(attachment: AttachmentRepository.Attachment)
    {
        _rvList.value = _rvList.value?.toMutableList().also { li ->
            val header = li?.get(0) as AddDataModel.Aggregate
            li[0] = header.also {
                it.thumbnail = attachment.thumbnail
            }
        }
        this.attachment = attachment
    }



    private val flow = Pager(
        PagingConfig(
            pageSize = 32,
        ),
    ) { attachmentRepository.galleryImagesPaginated }.flow.cachedIn(viewModelScope)


    fun galleryCollect()
    {
        _galleryState.value = GalleryDataState.Loading
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                flow.collectLatest { pagingData ->
                    _galleryState.value = GalleryDataState.Data(pagingData)
                }
            }
        }
    }


    private fun saveToDb() = viewModelScope.launch {
        var attachmentUri = attachment?.let { attachmentRepository.copyAttachment(it) }

    }

    fun setAttachment(uri: Uri, type: AttachmentRepository.TYPE)
    {
        attachmentRepository.getFileName(uri)?.let {
            attachment = AttachmentRepository.Attachment(it, uri, null, true, type)
        }
    }

}

class AddViewModelFactory(private val repository: AttachmentRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(AddViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return AddViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}