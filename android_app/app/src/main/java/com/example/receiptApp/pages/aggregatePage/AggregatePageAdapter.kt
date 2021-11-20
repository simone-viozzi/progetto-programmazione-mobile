package com.example.receiptApp.pages.aggregatePage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.receiptApp.databinding.ArchiveAggregateBinding
import com.example.receiptApp.databinding.ArchiveElementBinding
import com.example.receiptApp.pages.archive.ArchiveDataModel
import com.example.receiptApp.repository.AttachmentRepository
import com.example.receiptApp.round

class AggregatePageAdapter (
    val attachemntRepository: AttachmentRepository
) : ListAdapter<ArchiveDataModel, AggregatePageAdapter.AggregatePageViewHolder>(AggregatePageAdapter.AggregatePageDiffCallback()){

    class AggregatePageDiffCallback : DiffUtil.ItemCallback<ArchiveDataModel>()
    {
        override fun areItemsTheSame(oldItem: ArchiveDataModel, newItem: ArchiveDataModel): Boolean
        {
            if (oldItem is ArchiveDataModel.Aggregate && newItem is ArchiveDataModel.Aggregate)
            {
                return oldItem.id == newItem.id
            }
            if (oldItem is ArchiveDataModel.Element && newItem is ArchiveDataModel.Element)
            {
                return oldItem.id == newItem.id
            }
            return false
        }

        override fun areContentsTheSame(oldItem: ArchiveDataModel, newItem: ArchiveDataModel): Boolean
        {
            if (oldItem is ArchiveDataModel.Aggregate && newItem is ArchiveDataModel.Aggregate)
            {
                return oldItem == newItem
            }
            if (oldItem is ArchiveDataModel.Element && newItem is ArchiveDataModel.Element)
            {
                return oldItem == newItem
            }
            return false
        }
    }

    sealed class AggregatePageViewHolder(
        binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        class AggregateViewHolder(
            private val binding: ArchiveAggregateBinding,
            private val attachemntRepository: AttachmentRepository
        ): AggregatePageAdapter.AggregatePageViewHolder(binding) {

            // the callbacks need to be in the init section
            init {}

            fun bind(aggregate: ArchiveDataModel.Aggregate) {

                with(binding){
                    tagTextView.text = aggregate.tag
                    dateTextView.text = aggregate.str_date
                    costTextView.text = aggregate.tot_cost?.round(2).toString() + "€"

                    if(aggregate.thumbnail != null){
                        // if attachemnt isn't null load bitmap from uri at bind time
                        attachemntRepository.generateThumbnailFromUri(aggregate.thumbnail)?.let{
                            imageAttachment.setImageBitmap(it)
                        }
                    }else{
                        imageAttachment.setImageBitmap(attachemntRepository.getDefaultBitmap())
                    }
                }
            }
        }

        class ElementViewHolder(
            private val binding: ArchiveElementBinding
        ): AggregatePageAdapter.AggregatePageViewHolder(binding) {

            // the callbacks need to be in the init section
            init {}

            fun bind(element: ArchiveDataModel.Element) {

                with(binding){
                    nameTextView.text = element.name ?: " - "
                    tagTextView.text = element.elem_tag ?: " - "
                    numTextView.text = element.num.toString()
                    costTextView.text = element.cost?.round(2).toString() + "€"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AggregatePageAdapter.AggregatePageViewHolder
    {
        // depending on the view type i return the corresponding holder
        return when (viewType){
            com.example.receiptApp.R.layout.archive_aggregate -> AggregatePageAdapter.AggregatePageViewHolder.AggregateViewHolder(
                // this is the binding!
                ArchiveAggregateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                attachemntRepository
            )
            com.example.receiptApp.R.layout.archive_element -> AggregatePageAdapter.AggregatePageViewHolder.ElementViewHolder(
                // this is the binding!
                ArchiveElementBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            // the else case is needed, but should never be called
            else -> throw IllegalStateException("the view type in the RecyclerView is wrongggg! ")
        }
    }

    override fun onBindViewHolder(holder: AggregatePageAdapter.AggregatePageViewHolder, position: Int)
    {
        // depending on the type of the holder i need to bind the corresponding view
        when (holder){
            is AggregatePageAdapter.AggregatePageViewHolder.AggregateViewHolder -> holder.bind(getItem(position) as ArchiveDataModel.Aggregate)
            is AggregatePageAdapter.AggregatePageViewHolder.ElementViewHolder -> holder.bind(getItem(position) as ArchiveDataModel.Element)
        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return when (getItem(position)){
            is ArchiveDataModel.Aggregate -> com.example.receiptApp.R.layout.archive_aggregate
            is ArchiveDataModel.Element -> com.example.receiptApp.R.layout.archive_element
            else -> throw IllegalStateException("only ArchiveDataModel.Aggregate item expected, something goes wrong.")
        }
    }
}