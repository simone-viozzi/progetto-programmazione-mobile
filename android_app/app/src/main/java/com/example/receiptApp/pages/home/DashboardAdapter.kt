package com.example.receiptApp.pages.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.receiptApp.R

import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.receiptApp.databinding.DashboardElementTestBigBinding
import com.example.receiptApp.databinding.DashboardElementTestBinding
import java.util.*


class DashboardAdapter(private val onItemMove: ((List<DashboardDataModel>) -> Unit)) : ListAdapter<DashboardDataModel, DashboardAdapter.DashboardViewHolder>(DashboardDiffCallback())
{
    sealed class DashboardViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)
    {
        class TestViewHolder(private val binding: DashboardElementTestBinding) : DashboardViewHolder(binding)
        {
            fun bind(holder: DashboardDataModel.Test)
            {
                (binding.cardView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = binding.cardView.isBig

                binding.textView1.text = holder.id.toString()
            }
        }

        class TestBigViewHolder(private val binding: DashboardElementTestBigBinding) : DashboardViewHolder(binding)
        {
            fun bind(holder: DashboardDataModel.TestBig)
            {
                (binding.cardView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = binding.cardView.isBig

                binding.textView1.text = holder.id.toString()
            }
        }
    }

    class DashboardDiffCallback : DiffUtil.ItemCallback<DashboardDataModel>()
    {
        override fun areItemsTheSame(oldItem: DashboardDataModel, newItem: DashboardDataModel): Boolean
        {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DashboardDataModel, newItem: DashboardDataModel): Boolean
        {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder
    {
        return when (viewType)
        {
            R.layout.dashboard_element_test -> DashboardViewHolder.TestViewHolder(
                DashboardElementTestBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
            )
            R.layout.dashboard_element_test_big -> DashboardViewHolder.TestBigViewHolder(
                DashboardElementTestBigBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
            )
            else -> throw IllegalStateException("the view type in the RecyclerView is wrongggg! ")
        }
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int)
    {
        when (holder)
        {
            is DashboardViewHolder.TestViewHolder -> holder.bind(getItem(position) as DashboardDataModel.Test)
            is DashboardViewHolder.TestBigViewHolder -> holder.bind(getItem(position) as DashboardDataModel.TestBig)
        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return when (getItem(position))
        {
            is DashboardDataModel.Test -> R.layout.dashboard_element_test
            is DashboardDataModel.TestBig -> R.layout.dashboard_element_test_big
        }
    }

    fun swapItems(fromPosition: Int, toPosition: Int) {
        val currList = currentList.toMutableList()

        Collections.swap(currList, fromPosition, toPosition)

        onItemMove(currList)
    }

}