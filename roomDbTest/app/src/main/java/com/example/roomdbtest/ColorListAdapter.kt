package com.example.roomdbtest

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdbtest.databinding.RvSingleItemBinding


class ColorListAdapter(): ListAdapter<MyColor, ColorListAdapter.ColorViewHolder>(ColorDiffCallback())
{
    var onItemClick: ((MyColor) -> Unit)? = null

    class ColorViewHolder private constructor(val binding: RvSingleItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        companion object {
            fun from(parent: ViewGroup): ColorViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvSingleItemBinding.inflate(layoutInflater, parent, false)
                return ColorViewHolder(binding)
            }
        }
    }

    class ColorDiffCallback: DiffUtil.ItemCallback<MyColor>()
    {
        override fun areItemsTheSame(oldItem: MyColor, newItem: MyColor): Boolean
        {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MyColor, newItem: MyColor): Boolean
        {
            return (oldItem.red == newItem.red) &&
                    (oldItem.green == newItem.green) &&
                    (oldItem.blue == newItem.blue)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder
    {
        return ColorViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int)
    {

        val c = getItem(position)

        holder.binding.textView.text = c.generateHex()

        holder.binding.imageView.setBackgroundColor(Color.rgb(c.red, c.green, c.blue))

        holder.binding.singleElement.setOnClickListener {

            if (position != RecyclerView.NO_POSITION)
            {
                onItemClick?.invoke(c)
            }
        }

    }

}