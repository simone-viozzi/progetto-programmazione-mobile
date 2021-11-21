package com.example.receiptApp.pages.add.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.receiptApp.R
import com.example.receiptApp.databinding.AddImgBinding
import com.example.receiptApp.repository.AttachmentRepository.Attachment

/**
 * Gallery adapter
 *  this adapter get paginated data and display it
 */
class GalleryAdapter(
    private val clickListener: ((Attachment) -> Unit)
) : PagingDataAdapter<Attachment, GalleryAdapter.ImgViewHolder>(DiffCallback())
{
    class DiffCallback : DiffUtil.ItemCallback<Attachment>()
    {
        // the uri act as id
        override fun areItemsTheSame(oldItem: Attachment, newItem: Attachment): Boolean = oldItem.uri == newItem.uri
        override fun areContentsTheSame(oldItem: Attachment, newItem: Attachment): Boolean = oldItem == newItem
    }

    class ImgViewHolder(
        private val binding: AddImgBinding,
        clickListener: ((Attachment) -> Unit)
    ) : RecyclerView.ViewHolder(binding.root)
    {
        private var img: Attachment? = null

        init
        {
            // the click listener is common for every element
            binding.imageView.setOnClickListener {
                img?.let { it1 -> clickListener.invoke(it1) }
            }
        }

        fun bind(item: Attachment)
        {
            // loading images with glide using placeholder
            img = item
            val size = binding.imageView.width
            Glide.with(binding.root.context)
                .load(item.thumbnail)
                .apply(
                    RequestOptions
                        .centerCropTransform()
                        .override(size)
                )
                .apply(
                    RequestOptions()
                        .placeholder(
                            ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.ic_baseline_image_24
                            )
                        )
                        .override(size)
                        .dontAnimate()
                )
                .into(binding.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgViewHolder
    {
        return ImgViewHolder(
            // this is the binding!
            AddImgBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            clickListener
        )
    }

    override fun onBindViewHolder(holder: ImgViewHolder, position: Int)
    {
        getItem(position)?.let { holder.bind(it) }
    }
}