package com.example.receiptApp.pages.edit

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
import java.text.SimpleDateFormat


class EditViewModel(
    private val attachmentRepository: AttachmentRepository,
    private val dbRepository: DbRepository,
    private val aggregateId: Long
) : ViewModel() {

    // the list observed by the recyclerview
    private val _rvList = MutableLiveData<List<EditDataModel>>()
    val rvList: LiveData<List<EditDataModel>>
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
    var textEditCallback: ((el: EditDataModel) -> (Unit)) = { el ->
        // need to separate the two types
        when (el) {
            is EditDataModel.Aggregate -> {
                val oldEl = _rvList.value?.get(0) as EditDataModel.Aggregate

                el.str_date?.let { oldEl.str_date = it }
                el.tag?.let { oldEl.tag = it }

                _rvList.value = _rvList.value?.toMutableList().also { it?.set(0, oldEl) }
            }

            is EditDataModel.Element -> {
                val oldEl = _rvList.value?.get(el.vId) as EditDataModel.Element

                val update = oldEl.update(el)
                Timber.d("update -> $update")

                val newList = _rvList.value?.toMutableList().also { it?.set(el.vId, oldEl) }

                _rvList.value = newList?.also {
                    if (el.vId == getLastId(false)) {
                        it.add(
                            EditDataModel.Element(
                                vId = getLastId(),
                            )
                        )
                    }
                }
            }
        }
    }

    init {
        if (aggregateId == -1L) {
            _rvList.value = listOf(
                EditDataModel.Aggregate(vId = 0),
                EditDataModel.Element(vId = getLastId())
            )
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val (aggregate, elements) = preloadAggregateElements()
                _rvList.postValue(mutableListOf(aggregate as EditDataModel).also {
                    it.addAll(
                        elements
                    )
                })
            }
        }
        loadAutocomplete()
    }

    private suspend fun preloadAggregateElements(): Pair<EditDataModel.Aggregate, List<EditDataModel.Element>> {
        val (dbAggregate, dbElements) = dbRepository.getAggregateWithElementsById(
            aggregateId
        ).asIterable().first()

        Timber.d("dbAggregate -> $dbAggregate")
        Timber.d("dbElements -> $dbElements")

        val aggregate = EditDataModel.Aggregate(
            vId = 0,
            tag = dbAggregate.tag,
            str_date = dbAggregate.date?.let { date ->
                SimpleDateFormat("dd/MM/yyyy").format(
                    date
                )
            },
            thumbnail = dbAggregate.attachment?.let { uri ->
                AttachmentRepository.Attachment(
                    name = attachmentRepository.getFileName(uri),
                    uri = uri,
                ).let { a ->
                    attachment = a
                    attachmentRepository.generateThumbnail(a)
                }
            },
            dbId = dbAggregate.id
        )

        val elements = dbElements.map {
            EditDataModel.Element(
                vId = getLastId(),
                name = it.name,
                num = it.num.toInt(),
                elem_tag = it.elem_tag,
                cost = it.cost.toDouble(),
                dbId = it.elem_id
            )
        }
        return Pair(aggregate, elements)
    }


    // this get called when the date picker return
    fun setDate(millis: Long) {
        val date = DateFormat.format("dd/MM/yyyy", millis).toString()
        _rvList.value = _rvList.value?.toMutableList().also { li ->
            val header = li?.get(0) as EditDataModel.Aggregate
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
    fun galleryCollect() {
        _galleryState.value = GalleryDataState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            flow.collectLatest { pagingData ->
                _galleryState.value = GalleryDataState.Data(pagingData)
            }
        }
    }

    // helper function to retrieve the aggregate and the list of elements separately
    private fun splitAggregateAndElements(): Pair<EditDataModel.Aggregate, List<EditDataModel.Element>> {
        _rvList.value?.let { currList ->

            // split the list taking the aggregate and all the elements
            val aggregate = currList[0] as EditDataModel.Aggregate
            val elements =
                currList.subList(1, currList.lastIndex).map { it as EditDataModel.Element }

            return Pair(aggregate, elements)
        }
        throw NullPointerException("rvList.value is null!")
    }


    // before saving this record into the db we need to be certain that the data inserted is valid and contain all
    //  the values needed by the db. this is done synchronously and before starting the save into the db
    fun selfIntegrityCheck(): Boolean
    {
        val (aggregate, elements) = splitAggregateAndElements()

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

        val (aggregate, elements) = splitAggregateAndElements()

        // if the attachment is not into the private memory of the app i need to copy it there
        val attachmentUri = attachment?.let {
            if (!it.needToCopy) it.uri else attachmentRepository.copyAttachment(it)
        }

        Timber.e("aggregate -> $aggregate")
        Timber.e("elements -> $elements")
        Timber.e("attachment -> $attachmentUri")

        if (aggregateId == -1L) {
            dbRepository.insertAggregateWithElements(aggregate, elements.also {
                // the last element is always blank
                it.toMutableList().remove(it.last())
            }, attachmentUri)
        }
        else
        {
            dbRepository.updateAggregateWithElements(
                aggregate, elements.also {
                    // the last element is always blank
                    it.toMutableList().remove(it.last())
                }, attachmentUri
            )
        }

        Timber.e("fine inserimento")
    }

    /**
     * Set the attachment and display the thumbnail
     */
    fun setAttachment(attachment: AttachmentRepository.Attachment)
    {
        // the name is not used right now
        attachment.name = attachment.name ?: attachmentRepository.getFileName(attachment.uri)
        attachment.thumbnail = attachment.thumbnail ?: attachmentRepository.generateThumbnail(attachment)

        Timber.d("${attachment.thumbnail}")

        _rvList.value = _rvList.value?.toMutableList().also { li ->
            val header = li?.get(0) as EditDataModel.Aggregate
            li[0] = header.also {
                it.thumbnail = attachment.thumbnail
            }
        }

        this.attachment = attachment
    }

    /**
     * Load the autocomplete values so they are ready when the user move the focus around
     */
    private fun loadAutocomplete() = viewModelScope.launch {
        autoCompleteAggregateValue = dbRepository.getAggregateTagNames()
        autoCompleteElementValue = dbRepository.getElementTagNames()
    }


}


class EditViewModelFactory(
    private val attachmentRepository: AttachmentRepository,
    private val dbRepository: DbRepository,
    private val aggregateId: Long
) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(EditViewModel::class.java))
        {
            @Suppress("UNCHECKED_CAST")
            return EditViewModel(
                attachmentRepository,
                dbRepository,
                aggregateId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}