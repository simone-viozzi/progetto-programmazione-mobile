package com.example.receiptApp.pages.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.receiptApp.R
import com.example.receiptApp.databinding.DashboardElementTestBigBinding
import com.example.receiptApp.databinding.DashboardElementTestBinding
import com.example.receiptApp.pages.home.DashboardDataModel
import com.example.receiptApp.pages.home.adapters.components.DashboardDiffCallback
import com.example.receiptApp.pages.home.adapters.components.DashboardViewHolder
import java.util.*


class DashboardAdapter: ListAdapter<DashboardDataModel, DashboardViewHolder>(DashboardDiffCallback())
{

    var onItemMove: ((List<DashboardDataModel>) -> Unit)? = null
    var onLongClickListener: (() -> Unit)? = null

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
                onLongClickListener
            )
            R.layout.dashboard_element_test_big -> DashboardViewHolder.TestBigViewHolder(
                DashboardElementTestBigBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onLongClickListener
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

        onItemMove?.invoke(currList)
    }

}