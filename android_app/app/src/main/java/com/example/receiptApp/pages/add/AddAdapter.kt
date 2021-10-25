package com.example.receiptApp.pages.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.receiptApp.R
import com.example.receiptApp.databinding.AddHeadBinding
import com.example.receiptApp.databinding.AddSingleElementBinding
import com.example.receiptApp.toEditable
import timber.log.Timber

class AddAdapter(var textEditCallback: ((AddDataModel) -> (Unit)), var calendarClick: (() -> Unit)) :
    ListAdapter<AddDataModel, AddAdapter.AddViewHolder>(AddDiffCallback())
{
    class AddDiffCallback : DiffUtil.ItemCallback<AddDataModel>()
    {
        override fun areItemsTheSame(oldItem: AddDataModel, newItem: AddDataModel): Boolean
        {
            if (oldItem is AddDataModel.Header && newItem is AddDataModel.Header)
            {
                return oldItem.id == newItem.id
            }
            if (oldItem is AddDataModel.SingleElement && newItem is AddDataModel.SingleElement)
            {
                return oldItem.id == newItem.id
            }
            return false
        }

        override fun areContentsTheSame(oldItem: AddDataModel, newItem: AddDataModel): Boolean
        {
            if (oldItem is AddDataModel.Header && newItem is AddDataModel.Header)
            {
                return oldItem == newItem
            }
            if (oldItem is AddDataModel.SingleElement && newItem is AddDataModel.SingleElement)
            {
                return oldItem == newItem
            }
            return false
        }

    }


    sealed class AddViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        class HeaderViewHolder(private val binding: AddHeadBinding, textEditCallback: ((AddDataModel) -> (Unit)), calendarClick: (() -> Unit)) :
            AddViewHolder(binding)
        {
            init {
                binding.textFieldTag.editText?.doOnTextChanged { text: CharSequence?,
                                                                 _, _,
                                                                 count: Int ->
                    if (count > 0) textEditCallback.invoke(
                        AddDataModel.Header(id = adapterPosition, tag = text.toString())
                    )
                }
                binding.dateOverlay.setOnClickListener {
                    calendarClick.invoke()
                }
            }

            fun bind(header: AddDataModel.Header)
            {
                binding.textFieldTag.editText?.text = header.tag?.toEditable()
                Timber.d("date -> ${header.date}")
                binding.textFieldDate.editText?.text = header.date?.toEditable()
            }
        }

        class ElementViewHolder(
            private val binding: AddSingleElementBinding,
            textEditCallback: ((AddDataModel) -> (Unit))
        ) : AddViewHolder(binding)
        {
            init
            {
                binding.textFieldName.editText?.doOnTextChanged { text: CharSequence?,
                                                                  _, _,
                                                                  count: Int ->
                    if (count > 0) textEditCallback.invoke(
                        AddDataModel.SingleElement(id = adapterPosition, name = text.toString())
                    )
                }
                binding.textFieldNum.editText?.doOnTextChanged { text: CharSequence?,
                                                                 _, _,
                                                                 count: Int ->
                    if (count > 0) textEditCallback.invoke(
                        AddDataModel.SingleElement(id = adapterPosition, num = text.toString().toIntOrNull())
                    )
                }
                binding.textFieldTag.editText?.doOnTextChanged { text: CharSequence?,
                                                                 _, _,
                                                                 count: Int ->
                    if (count > 0) textEditCallback.invoke(
                        AddDataModel.SingleElement(id = adapterPosition, tag = text.toString())
                    )
                }
                binding.textFieldCost.editText?.doOnTextChanged { text: CharSequence?,
                                                                  _, _,
                                                                  count: Int ->
                    if (count > 0) textEditCallback.invoke(
                        AddDataModel.SingleElement(id = adapterPosition, cost = text.toString().toDoubleOrNull())
                    )
                }
            }

            fun bind(element: AddDataModel.SingleElement)
            {
                binding.textFieldName.editText?.text = element.name?.toEditable()
                binding.textFieldNum.editText?.text = element.num?.toString()?.toEditable()
                binding.textFieldTag.editText?.text = element.tag?.toEditable()
                binding.textFieldCost.editText?.text = element.cost?.toString()?.toEditable()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddViewHolder
    {
        return when (viewType)
        {
            R.layout.add_head -> AddViewHolder.HeaderViewHolder(
                AddHeadBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                textEditCallback,
                calendarClick
            )
            R.layout.add_single_element -> AddViewHolder.ElementViewHolder(
                AddSingleElementBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                textEditCallback
            )
            else -> throw IllegalStateException("the view type in the RecyclerView is wrongggg! ")
        }
    }

    override fun onBindViewHolder(holder: AddViewHolder, position: Int)
    {
        when (holder)
        {
            is AddViewHolder.HeaderViewHolder -> holder.bind(getItem(position) as AddDataModel.Header)
            is AddViewHolder.ElementViewHolder -> holder.bind(getItem(position) as AddDataModel.SingleElement)
        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return when (getItem(position))
        {
            is AddDataModel.Header -> R.layout.add_head
            is AddDataModel.SingleElement -> R.layout.add_single_element
        }
    }

}

