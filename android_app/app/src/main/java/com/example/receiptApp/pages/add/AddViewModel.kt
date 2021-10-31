package com.example.receiptApp.pages.add

import android.net.Uri
import android.text.format.DateFormat
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.receiptApp.sources.Attachment
import com.example.receiptApp.sources.GalleryImagesPaginated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddViewModel(private val imagesPaginated: GalleryImagesPaginated) : ViewModel()
{
    // the list observed by the recyclerview
    private val _rvList = MutableLiveData<List<AddDataModel>>()
    val rvList: LiveData<List<AddDataModel>>
        get() = _rvList

    private var attachmentUri: Uri? = null

    // the callback used by every element of the views in the textWatcher to update the corresponding element in the
    // view model, it pass an element with only the updated field and the id != null
    var textEditCallback: ((el: AddDataModel) -> (Unit)) = { el ->

        // need to separate the two types
        when (el)
        {
            is AddDataModel.Header ->
            {
                val oldEl = _rvList.value?.get(0) as AddDataModel.Header

                // TODO (el... != "") in sill needed? need to test
                if (el.date != null && el.date != "") oldEl.date = el.date
                if (el.tag != null && el.tag != "") oldEl.tag = el.tag

                // TODO update the list only if there was a change!
                _rvList.value = _rvList.value?.toMutableList().also { it?.set(0, oldEl) }
            }

            is AddDataModel.SingleElement ->
            {
                val oldEl = _rvList.value?.get(el.id) as AddDataModel.SingleElement

                // TODO (el... != "") in sill needed? need to test
                if (el.name != null && el.name != "") oldEl.name = el.name
                if (el.tag != null && el.tag != "") oldEl.tag = el.tag
                if (el.num != null) oldEl.num = el.num
                if (el.cost != null) oldEl.cost = el.cost

                val newList = _rvList.value?.toMutableList().also { it?.set(el.id, oldEl) }

                if (el.id == getLastId(false))
                {
                    // TODO update the list only if there was a change!
                    _rvList.value = newList?.plus(listOf(AddDataModel.SingleElement(id = getLastId())))
                } else
                {
                    // TODO update the list only if there was a change!
                    _rvList.value = newList!!
                }
            }
        }
    }

    // this get called when the date picker return
    fun setDate(millis: Long)
    {
        val date = DateFormat.format("dd/MM/yyyy", millis).toString()
        _rvList.value = _rvList.value?.toMutableList().also { li ->
            val header = li?.get(0) as AddDataModel.Header
            li[0] = header.also { it.date = date }
        }
    }

    private var lastId = 0

    private fun getLastId(autoincrement: Boolean = true): Int
    {
        return if (autoincrement) ++lastId else lastId
    }

    fun setAttachment(attachment: Attachment)
    {
        _rvList.value = _rvList.value?.toMutableList().also { li ->
            val header = li?.get(0) as AddDataModel.Header
            li[0] = header.also {
                it.attachment_name = attachment.name
                it.thumbnail = attachment.thumbnail
            }
        }
        attachmentUri = attachment.contentUri
    }

    init
    {
        _rvList.value = listOf(
            AddDataModel.Header(id = 0),
            AddDataModel.SingleElement(id = getLastId())
        )
    }

    private val flow = Pager(
        PagingConfig(
            pageSize = 24,
            initialLoadSize = 6,
            //jumpThreshold = 24
        ),
    ) { imagesPaginated }.flow.cachedIn(viewModelScope).flowOn(Dispatchers.IO)

    private val _galleryState = MutableStateFlow<GalleryDataState>(GalleryDataState.Idle)
    val galleryState: StateFlow<GalleryDataState> = _galleryState

    fun galleryCollect() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                flow.collectLatest { pagingData ->
                    _galleryState.value = GalleryDataState.Data(pagingData)
                }
            }
        }
    }

}

class AddViewModelFactory(private val imagesPaginated: GalleryImagesPaginated) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(AddViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return AddViewModel(imagesPaginated) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}