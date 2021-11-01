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
import com.example.receiptApp.repository.Attachment


class GalleryAdapter(private val clickListener: ((Attachment) -> Unit)) : PagingDataAdapter<Attachment, GalleryAdapter.ImgViewHolder>(DiffCallback())
{
    class DiffCallback : DiffUtil.ItemCallback<Attachment>()
    {
        override fun areItemsTheSame(oldItem: Attachment, newItem: Attachment): Boolean = oldItem.contentUri == newItem.contentUri
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
            binding.imageView.setOnClickListener {
                img?.let { it1 -> clickListener.invoke(it1) }
            }
        }

        fun bind(item: Attachment)
        {
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

            //binding.textView.text = item.name
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