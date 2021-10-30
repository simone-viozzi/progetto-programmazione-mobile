package com.example.cursoradapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.cursoradapter.databinding.SingleElemBinding
import kotlinx.coroutines.CoroutineDispatcher
import androidx.constraintlayout.widget.Placeholder


class Adapter(var placeholder: Drawable) :
    PagingDataAdapter<MyImg, Adapter.ImgViewHolder>(DiffCallback())
{
    class DiffCallback : DiffUtil.ItemCallback<MyImg>()
    {
        override fun areItemsTheSame(oldItem: MyImg, newItem: MyImg): Boolean = oldItem.contentUri == newItem.contentUri
        override fun areContentsTheSame(oldItem: MyImg, newItem: MyImg): Boolean = oldItem == newItem
    }

    class ImgViewHolder(private val binding: SingleElemBinding, var placeholder: Drawable) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item: MyImg)
        {
            Glide.with(binding.root.context)
                .load(item.thumbnail)
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions().placeholder(placeholder)
                    .override(500, 500)
                    .dontAnimate())
                .into(binding.imageView)

            binding.textView.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgViewHolder
    {
        return ImgViewHolder(
            // this is the binding!
            SingleElemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            placeholder
        )
    }

    override fun onBindViewHolder(holder: ImgViewHolder, position: Int)
    {
        getItem(position)?.let { holder.bind(it) }
    }


}