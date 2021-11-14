package com.example.receiptApp.pages.add.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.receiptApp.R
import com.example.receiptApp.databinding.AddHeadBinding
import com.example.receiptApp.databinding.AddSingleElementBinding
import com.example.receiptApp.pages.add.AddDataModel
import com.example.receiptApp.toEditable
import timber.log.Timber

class AddAdapter(
    private var textEditCallback: ((AddDataModel) -> Unit),
    private var autocompleteAggregate: (() -> Array<String?>?),
    private var autocompleteElement: (() -> Array<String?>?),
    private var calendarClick: (() -> Unit)
) :
    ListAdapter<AddDataModel, AddAdapter.AddViewHolder>(AddDiffCallback())
{

    object SelfCheckCallbacks
    {
        var selfCheckAggregate: (() -> Unit)? = null
    }


    /**
     * for areItemsTheSame i use  the id, that NEED to be unique
     * for areContentsTheSame i use the equals method generated by the data class
     */
    class AddDiffCallback : DiffUtil.ItemCallback<AddDataModel>()
    {
        override fun areItemsTheSame(oldItem: AddDataModel, newItem: AddDataModel): Boolean
        {
            if (oldItem is AddDataModel.Aggregate && newItem is AddDataModel.Aggregate)
            {
                return oldItem.vId == newItem.vId
            }
            if (oldItem is AddDataModel.Element && newItem is AddDataModel.Element)
            {
                return oldItem.vId == newItem.vId
            }
            return false
        }

        override fun areContentsTheSame(oldItem: AddDataModel, newItem: AddDataModel): Boolean
        {
            if (oldItem is AddDataModel.Aggregate && newItem is AddDataModel.Aggregate)
            {
                return oldItem == newItem
            }
            if (oldItem is AddDataModel.Element && newItem is AddDataModel.Element)
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

        // TODO!!
        var selfCheckElement: ((Int) -> Unit)? = null


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
            private val binding: AddHeadBinding,
            textEditCallback: ((AddDataModel) -> (Unit)),
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
                                // adapterPosition -> Returns the Adapter position of the item represented by this ViewHolder.
                                AddDataModel.Aggregate(vId = position, tag = text.toString())
                            )
                            showSuggestions(this, autocomplete, context)
                        }
                    }

                    setOnFocusChangeListener { _, hasFocus ->
                        if (hasFocus) showSuggestions(this, autocomplete, context)
                    }
                }

                binding.dateOverlay.setOnClickListener {
                    calendarClick.invoke()
                }

                SelfCheckCallbacks.selfCheckAggregate = {
                    Toast.makeText(binding.root.context, "selfCheckAggregate", Toast.LENGTH_LONG).show()
                    Timber.e("selfCheckAggregate called!!!")
                }
            }

            fun bind(aggregate: AddDataModel.Aggregate)
            {
                with(binding)
                {
                    textFieldTag.editText?.text = aggregate.tag?.toEditable()
                    textFieldDate.editText?.text = aggregate.str_date?.toEditable()

                    thumbnail.visibility = if (aggregate.thumbnail != null) View.VISIBLE else View.GONE

                    Glide.with(binding.root.context)
                        .load(aggregate.thumbnail)
                        .apply(
                            RequestOptions
                                .centerCropTransform()
                                .override(thumbnail.width)
                        )
                        .apply(
                            RequestOptions()
                                .placeholder(
                                    ContextCompat.getDrawable(
                                        binding.root.context,
                                        R.drawable.ic_baseline_image_24
                                    )
                                )
                                .override(thumbnail.width)
                                .dontAnimate()
                        )
                        .into(thumbnail)
                }
            }
        }

        class ElementViewHolder(
            private val binding: AddSingleElementBinding,
            textEditCallback: (AddDataModel) -> Unit,
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
                            AddDataModel.Element(
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
                            AddDataModel.Element(
                                vId = position, num = text.toString().toIntOrNull()
                            )
                        )
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
                                AddDataModel.Element(
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
                            AddDataModel.Element(
                                vId = position, cost = text.toString().toDoubleOrNull()
                            )
                        )
                    }
                }
            }

            fun bind(element: AddDataModel.Element)
            {
                binding.textFieldName.editText?.text = element.name?.toEditable()
                binding.textFieldNum.editText?.text = element.num?.toString()?.toEditable()
                binding.textFieldTag.editText?.text = element.elem_tag?.toEditable()
                binding.textFieldCost.editText?.text = element.cost?.toString()?.toEditable()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddViewHolder
    {
        // depending on the view type i return the corresponding holder
        return when (viewType)
        {
            R.layout.add_head -> AddViewHolder.HeaderViewHolder(
                // this is the binding!
                AddHeadBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                // the other parameters in the constructor
                textEditCallback,
                calendarClick,
                autocompleteAggregate
            )

            R.layout.add_single_element -> AddViewHolder.ElementViewHolder(
                // this is the binding!
                AddSingleElementBinding.inflate(
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
            is AddViewHolder.HeaderViewHolder -> holder.bind(getItem(position) as AddDataModel.Aggregate)
            is AddViewHolder.ElementViewHolder -> holder.bind(getItem(position) as AddDataModel.Element)
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
            is AddDataModel.Aggregate -> R.layout.add_head
            is AddDataModel.Element -> R.layout.add_single_element
        }
    }

}

