package com.example.receiptApp.pages.add

import android.net.Uri
import android.text.format.DateFormat
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.receiptApp.repository.AttachmentRepository
import com.example.receiptApp.repository.DbRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber


class AddViewModel(private val attachmentRepository: AttachmentRepository, private val dbRepository: DbRepository) :
    ViewModel()
{
    // the list observed by the recyclerview
    private val _rvList = MutableLiveData<List<AddDataModel>>()
    val rvList: LiveData<List<AddDataModel>>
        get() = _rvList

    private val _galleryState = MutableStateFlow<GalleryDataState>(GalleryDataState.Idle)
    val galleryState: StateFlow<GalleryDataState> = _galleryState

    private var autoCompleteAggregateValue: Array<String?>? = null
    private var autoCompleteElementValue: Array<String?>? = null

    var autoCompleteAggregateCallback: () -> Array<String?>? = {
        autoCompleteAggregateValue
    }

    var autoCompleteElementCallback: () -> Array<String?>? = {
        autoCompleteElementValue
    }

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

                val update = oldEl.update(el)
                Timber.d("update -> $update")

                val newList = _rvList.value?.toMutableList().also { it?.set(el.vId, oldEl) }

                if (el.vId == getLastId(false)-1)
                {
                    newList?.also { list ->
                        val lastIndex = list.lastIndex
                        list[lastIndex] = AddDataModel.Element(vId = lastIndex, elem_tag = el.elem_tag )
                    }
                }

                _rvList.value = newList?.also {
                    if (el.vId == getLastId(false) && update)
                    {
                        it.add(
                            AddDataModel.Element(
                                vId = getLastId(),
                                elem_tag = oldEl.elem_tag
                            )
                        )
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
        loadAutocomplete()
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


    /**
     * Save what the user inserted into the db
     *
     */
    @DelicateCoroutinesApi
    fun saveToDb() = GlobalScope.launch {
        _rvList.value?.let { currList ->
            // if the attachment is not into the private memory of the app i need to copy it there
            val attachmentUri =
                attachment?.let { if (!it.needToCopy) it.uri else attachmentRepository.copyAttachment(it) }

            // split the list taking the aggregate and all the elements
            val aggregate = currList[0] as AddDataModel.Aggregate
            val elements = currList.subList(1, currList.lastIndex).map { it as AddDataModel.Element }

            dbRepository.insertAggregateWithElements(aggregate, elements, attachmentUri)

            Timber.e("fine inserimento")
        }
    }

    fun setAttachment(uri: Uri, type: AttachmentRepository.TYPE)
    {
        attachmentRepository.getFileName(uri)?.let {
            attachment = AttachmentRepository.Attachment(it, uri, null, true, type)
        }
    }


    private fun loadAutocomplete() = viewModelScope.launch {
        autoCompleteAggregateValue = dbRepository.getAggregateTagNames()
        autoCompleteElementValue = dbRepository.getElementTagNames()
    }


}


class AddViewModelFactory(
    private val attachmentRepository: AttachmentRepository,
    private val dbRepository: DbRepository
) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(AddViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return AddViewModel(attachmentRepository, dbRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}