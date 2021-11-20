package com.example.receiptApp.pages.add

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


class AddViewModel(
    private val attachmentRepository: AttachmentRepository,
    private val dbRepository: DbRepository
    ) : ViewModel()
{

    // the list observed by the recyclerview
    private val _rvList = MutableLiveData<List<AddDataModel>>()
    val rvList: LiveData<List<AddDataModel>>
        get() = _rvList

    // the gallery is governed by a MutableStateFlow
    private val _galleryState = MutableStateFlow<GalleryDataState>(GalleryDataState.Idle)
    val galleryState: StateFlow<GalleryDataState> = _galleryState

    // autocompletes values -> this holt the list that should appear as suggestions
    private var autoCompleteAggregateValue: Array<String?>? = null
    private var autoCompleteElementValue: Array<String?>? = null

    // callbacks for the autocomplete
    var autoCompleteAggregateCallback: () -> Array<String?>? = { autoCompleteAggregateValue }
    var autoCompleteElementCallback: () -> Array<String?>? = { autoCompleteElementValue }

    // callbacks for the self check, this are implemented in the view holder and get passed back into the view model
    var selfCheckAggregate: (() -> Unit)? = null
    var selfCheckElements: Map<Int, (() -> Unit)?>? = null

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

                _rvList.value = _rvList.value?.toMutableList().also { it?.set(0, oldEl) }
            }

            is AddDataModel.Element ->
            {
                val oldEl = _rvList.value?.get(el.vId) as AddDataModel.Element

                val update = oldEl.update(el)
                Timber.d("update -> $update")

                val newList = _rvList.value?.toMutableList().also { it?.set(el.vId, oldEl) }

                _rvList.value = newList?.also {
                    if (el.vId == getLastId(false))
                    {
                        it.add(
                            AddDataModel.Element(
                                vId = getLastId(),
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

    // this is the flow that come from GalleryImagesPaginated
    private val flow = Pager(
        PagingConfig(
            pageSize = 32,
        ),
    ) { attachmentRepository.galleryImagesPaginated }.flow.cachedIn(viewModelScope)


    // to make the collecting of the flow async, i collect it with dispatcher.IO and put it into a stateFlow.
    //  this don't break the flow, the images are still paginated but the app doesn't freeze for half a minute
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

    // helper function to retrieve the aggregate and the list of elements separately
    private fun spitAggregateAndElements(): Pair<AddDataModel.Aggregate, List<AddDataModel.Element>>
    {
        _rvList.value?.let { currList ->

            // split the list taking the aggregate and all the elements
            val aggregate = currList[0] as AddDataModel.Aggregate
            val elements = currList.subList(1, currList.lastIndex).map { it as AddDataModel.Element }

            return Pair(aggregate, elements)
        }
        throw NullPointerException("rvList.value is null!")
    }


    // before saving this record into the db we need to be certain that the data inserted is valid and contain all
    //  the values needed by the db. this is done synchronously and before starting the save into the db
    fun selfIntegrityCheck(): Boolean
    {
        val (aggregate, elements) = spitAggregateAndElements()

        // the essential values are: the date for the aggregate, the cost and num for the elements

        var ret = true

        if (aggregate.str_date.isNullOrEmpty())
        {
            selfCheckAggregate?.invoke()
            ret = false
        }

        elements.forEach {
            if ((it.cost == null || it.num == null) && !(it.name.isNullOrEmpty() && it.elem_tag.isNullOrEmpty()) )
            {
                selfCheckElements?.get(it.vId)?.invoke()
                ret = false
            }
        }

        return ret
    }


    /**
     * Save what the user inserted into the db
     *  this is executed in GlobalScope because it will take far more time than the navController to return to the
     *   previous page. so if done with viewModelScope it will get killed before completing.
     */
    @DelicateCoroutinesApi
    fun saveToDb() = GlobalScope.launch {

        val (aggregate, elements) = spitAggregateAndElements()

        // if the attachment is not into the private memory of the app i need to copy it there
        val attachmentUri = attachment?.let {
            if (!it.needToCopy) it.uri else attachmentRepository.copyAttachment(it)
        }

        Timber.e("aggregate -> $aggregate")
        Timber.e("elements -> $elements")
        Timber.e("attachment -> $attachmentUri")

        dbRepository.insertAggregateWithElements(aggregate, elements.also {
            // the last element is always blank
            it.toMutableList().remove(it.last())
        }, attachmentUri)

        Timber.e("fine inserimento")
    }

    fun setAttachment(attachment: AttachmentRepository.Attachment)
    {
        attachment.name = attachment.name ?: attachmentRepository.getFileName(attachment.uri)
        attachment.thumbnail = attachment.thumbnail ?: attachmentRepository.generateThumbnail(attachment)

        Timber.d("${attachment.thumbnail}")

        _rvList.value = _rvList.value?.toMutableList().also { li ->
            val header = li?.get(0) as AddDataModel.Aggregate
            li[0] = header.also {
                it.thumbnail = attachment.thumbnail
            }
        }

        this.attachment = attachment
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