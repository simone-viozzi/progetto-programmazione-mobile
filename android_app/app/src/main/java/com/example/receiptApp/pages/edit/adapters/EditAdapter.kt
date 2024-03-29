package com.example.receiptApp.pages.edit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.receiptApp.R
import com.example.receiptApp.databinding.EditRvHeaderBinding
import com.example.receiptApp.databinding.EditRvSingleElementBinding
import com.example.receiptApp.pages.edit.EditDataModel
import com.example.receiptApp.toEditable
import timber.log.Timber

class EditAdapter(
    private var textEditCallback: ((EditDataModel) -> Unit),
    private var autocompleteAggregate: (() -> Array<String?>?),
    private var autocompleteElement: (() -> Array<String?>?),
    private var calendarClick: (() -> Unit)
) : ListAdapter<EditDataModel, EditAdapter.AddViewHolder>(AddDiffCallback())
{

    object SelfCheckCallbacks
    {
        var selfCheckAggregate: (() -> Unit)? = null
        var selfCheckElements: MutableMap<Int, (() -> Unit)?> = mutableMapOf()
    }


    /**
     * for areItemsTheSame i use  the id, that NEED to be unique
     * for areContentsTheSame i use the equals method generated by the data class
     */
    class AddDiffCallback : DiffUtil.ItemCallback<EditDataModel>()
    {
        override fun areItemsTheSame(oldItem: EditDataModel, newItem: EditDataModel): Boolean
        {
            if (oldItem is EditDataModel.Aggregate && newItem is EditDataModel.Aggregate)
            {
                return oldItem.vId == newItem.vId
            }
            if (oldItem is EditDataModel.Element && newItem is EditDataModel.Element)
            {
                return oldItem.vId == newItem.vId
            }
            return false
        }

        override fun areContentsTheSame(oldItem: EditDataModel, newItem: EditDataModel): Boolean
        {
            if (oldItem is EditDataModel.Aggregate && newItem is EditDataModel.Aggregate)
            {
                return oldItem == newItem
            }
            if (oldItem is EditDataModel.Element && newItem is EditDataModel.Element)
            {
                return oldItem == newItem
            }
            return false
        }
    }

    /**
     * the View holder, this class have two child, one for every view type i need to use
     *
     * @constructor
     *
     * @param binding the view binding
     */
    sealed class AddViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root)
    {

        fun showSuggestions(editText: AppCompatAutoCompleteTextView, callback: (() -> Array<String?>?), context: Context)
        {
            callback.invoke()?.let { hints ->
                val adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_dropdown_item_1line,
                    hints
                )

                Timber.e("${hints.toList()}")
                editText.setAdapter(adapter)

                if (!editText.isPopupShowing && editText.text.isEmpty())
                {
                    editText.showDropDown()
                }
            }
        }

        /**
         * view holder of the block relative to the aggregate data
         *
         * @property binding
         * @constructor
         *
         * @param textEditCallback the callback for extracting the text from the text edit when the user modify it
         * @param calendarClick callback to open the date picker
         */
        class HeaderViewHolder(
            private val binding: EditRvHeaderBinding,
            textEditCallback: ((EditDataModel) -> (Unit)),
            calendarClick: (() -> Unit),
            autocomplete: (() -> Array<String?>?)
        ) : AddViewHolder(binding)
        {
            // the callbacks need to be in the init section
            init
            {
                // callback that get called when the user is modifying the edittext inside the textFieldTag.
                // with this callback i get the text that is currently into the textedit and the count of characters
                // that got replaced
                binding.textFieldTagEditText.apply {
                    doOnTextChanged { text: CharSequence?,
                                      _, _,
                                      count: Int ->

                        val position = bindingAdapterPosition
                        if (position != NO_POSITION && count > 0)
                        {
                            textEditCallback.invoke(
                                EditDataModel.Aggregate(vId = position, tag = text.toString())
                            )
                            // when the user write something, show the Suggestions
                            showSuggestions(this, autocomplete, context)
                        }
                    }

                    // show suggestions when the user move the focus to this edittext
                    setOnFocusChangeListener { _, hasFocus ->
                        if (hasFocus) showSuggestions(this, autocomplete, context)
                    }
                }

                // it was too difficult to handle the click on the editText so we just added a transparent frame layout
                //  that handle the click
                binding.dateOverlay.setOnClickListener {
                    calendarClick.invoke()
                }

                // this callback will be called when the user try to save and there are incomplete elements
                SelfCheckCallbacks.selfCheckAggregate = {
                    if (binding.dateText.text.isNullOrEmpty())
                    {
                        binding.textFieldDate.apply {
                            error = "this field is required"
                        }
                    }
                }
            }

            fun bind(aggregate: EditDataModel.Aggregate)
            {
                with(binding)
                {
                    textFieldTag.editText?.text = aggregate.tag?.toEditable()
                    textFieldDate.editText?.text = aggregate.str_date?.toEditable()

                    // the image view appear/disappears depending of if there is an attachment
                    if (aggregate.thumbnail == null)
                    {
                        thumbnail.visibility = View.GONE
                    }
                    else
                    {
                        thumbnail.visibility = View.VISIBLE
                        Glide.with(binding.root.context)
                            .load(aggregate.thumbnail)
                            .apply(
                                RequestOptions
                                    .centerInsideTransform()
                            )
                            .into(thumbnail)
                    }
                }
            }
        }

        class ElementViewHolder(
            private val binding: EditRvSingleElementBinding,
            textEditCallback: (EditDataModel) -> Unit,
            autocomplete: (() -> Array<String?>?)
        ) : AddViewHolder(binding)
        {

            init
            {
                binding.textFieldName.editText?.doOnTextChanged { text: CharSequence?,
                                                                  _, _,
                                                                  count: Int ->

                    val position = bindingAdapterPosition
                    if (position != NO_POSITION && count > 0)
                    {
                        textEditCallback.invoke(
                            EditDataModel.Element(
                                vId = position, name = text.toString()
                            )
                        )
                    }
                }

                binding.textFieldNum.editText?.doOnTextChanged { text: CharSequence?,
                                                                 _, _,
                                                                 count: Int ->
                    val position = bindingAdapterPosition
                    if (position != NO_POSITION && count > 0)
                    {
                        textEditCallback.invoke(
                            EditDataModel.Element(
                                vId = position, num = text.toString().toIntOrNull()
                            )
                        )
                        binding.textFieldNum.error = null
                    }
                }

                binding.textFieldTagEditText.apply {
                    doOnTextChanged { text: CharSequence?,
                                      _, _,
                                      count: Int ->

                        val position = bindingAdapterPosition
                        if (position != NO_POSITION && count > 0)
                        {
                            textEditCallback.invoke(
                                EditDataModel.Element(
                                    vId = position, elem_tag = text.toString()
                                )
                            )
                            showSuggestions(this, autocomplete, context)
                        }
                    }
                    setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) showSuggestions(this, autocomplete, context)
                    }
                }

                binding.textFieldCost.editText?.doOnTextChanged { text: CharSequence?,
                                                                  _, _,
                                                                  count: Int ->
                    val position = bindingAdapterPosition
                    if (position != NO_POSITION && count > 0)
                    {
                        textEditCallback.invoke(
                            EditDataModel.Element(
                                vId = position, cost = text.toString().toDoubleOrNull()
                            )
                        )
                        binding.textFieldNum.error = null
                    }
                }
            }

            fun bind(element: EditDataModel.Element)
            {
                binding.textFieldName.editText?.text = element.name?.toEditable()
                binding.textFieldNum.editText?.text = element.num?.toString()?.toEditable()
                binding.textFieldTag.editText?.text = element.elem_tag?.toEditable()
                binding.textFieldCost.editText?.text = element.cost?.toString()?.toEditable()

                SelfCheckCallbacks.selfCheckElements[element.vId] = {
                    Toast.makeText(binding.root.context, "selfCheckAggregate", Toast.LENGTH_LONG).show()
                    Timber.e("selfCheckAggregate called!!!")

                    if (binding.textFieldNumEditText.text.isNullOrEmpty())
                    {
                        binding.textFieldNum.error = "required"

                    }
                    if (binding.textFieldCostEditText.text.isNullOrEmpty())
                    {
                        binding.textFieldCost.error = "required"

                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddViewHolder
    {
        // depending on the view type i return the corresponding holder
        return when (viewType)
        {
            R.layout.edit_rv_header -> AddViewHolder.HeaderViewHolder(
                // this is the binding!
                EditRvHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                // the other parameters in the constructor
                textEditCallback,
                calendarClick,
                autocompleteAggregate
            )

            R.layout.edit_rv_single_element -> AddViewHolder.ElementViewHolder(
                // this is the binding!
                EditRvSingleElementBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                textEditCallback,
                autocompleteElement
            )

            // the else case is needed, but should never be called
            else -> throw IllegalStateException("the view type in the RecyclerView is wrongggg! ")
        }
    }


    override fun onBindViewHolder(holder: AddViewHolder, position: Int)
    {
        // depending on the type of the holder i need to bind the corresponding view
        when (holder)
        {
            is AddViewHolder.HeaderViewHolder -> holder.bind(getItem(position) as EditDataModel.Aggregate)
            is AddViewHolder.ElementViewHolder -> holder.bind(getItem(position) as EditDataModel.Element)
        }
    }

    /**
     * this is needed to let the recyclerview know the relation between holder and layout
     *
     * @param position
     * @return -> the layout that need to inflated in the position
     */
    override fun getItemViewType(position: Int): Int
    {
        return when (getItem(position))
        {
            is EditDataModel.Aggregate -> R.layout.edit_rv_header
            is EditDataModel.Element -> R.layout.edit_rv_single_element
        }
    }

}

